package nior_near.server.domain.store.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nior_near.server.domain.order.entity.Place;
import nior_near.server.domain.store.dto.request.CompanyChefRegistrationRequestDto;
import nior_near.server.domain.store.dto.request.FreelanceChefRegistrationRequestDto;
import nior_near.server.domain.store.dto.response.ChefRegistrationResponseDto;
import nior_near.server.domain.store.entity.*;
import nior_near.server.domain.store.exception.handler.StoreHandler;
import nior_near.server.domain.store.repository.*;
import nior_near.server.domain.user.entity.Member;
import nior_near.server.domain.user.repository.MemberRepository;
import nior_near.server.global.common.AwsS3;
import nior_near.server.global.common.BaseResponseDto;
import nior_near.server.global.common.ResponseCode;
import nior_near.server.global.util.FileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreCommandServiceImpl implements StoreCommandService {

    private final StoreRepository storeRepository;
    private final PlaceRepository placeRepository;
    private final RegionRepository regionRepository;
    private final FileService fileService;
    private final AuthRepository authRepository;
    private final StoreRegionRepository storeRegionRepository;
    private final StoreAuthRepository storeAuthRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public BaseResponseDto<ChefRegistrationResponseDto> registerCompanyChef(Long memberId, CompanyChefRegistrationRequestDto companyChefRegistrationRequestDto) throws IOException {

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new StoreHandler(ResponseCode.MEMBER_NOT_FOUND));

        List<Auth> authList = new ArrayList<>();

        // 1. store 저장
        Place place = placeRepository.findById(companyChefRegistrationRequestDto.getPlaceId()).orElseThrow(() -> new StoreHandler(ResponseCode.PLACE_NOT_FOUND));

        Store store = storeRepository.save(
                Store.builder()
                .name(companyChefRegistrationRequestDto.getName())
                .title(companyChefRegistrationRequestDto.getShortDescription())
                .introduction(companyChefRegistrationRequestDto.getDetailedDescription())
                .profileImage(member.getProfileImage())
                .temperature(BigDecimal.valueOf(36.5))
                .message(companyChefRegistrationRequestDto.getMessage())
                .letter(getS3ImageLink(companyChefRegistrationRequestDto.getLetter(), "letters")) // 요리사 별 편지 이미지 저장(S3) - 그리고 그 링크를 Store 의 letter 에 저장
                .member(member)
                .place(place)
                .build()
        );

        // 인증 정보 저장
        if (companyChefRegistrationRequestDto.getQualification()) {
            authList.add(authRepository.findById(1L).orElseThrow(() -> new StoreHandler(ResponseCode.AUTH_NOT_FOUND)));
        }

        authList.add(authRepository.findById(2L).orElseThrow(() -> new StoreHandler(ResponseCode.AUTH_NOT_FOUND)));
        authList.add(authRepository.findById(companyChefRegistrationRequestDto.getAuth()).orElseThrow(() -> new StoreHandler(ResponseCode.AUTH_NOT_FOUND)));

        List<StoreAuth> storeAuthList = convertToStoreAuth(store, authList);
        storeAuthRepository.saveAll(storeAuthList);

        // 2. 만든 store 지역 매핑해서 StoreRegion 에 저장
        List<Region> regions = convertToRegion(companyChefRegistrationRequestDto.getRegionId1(), companyChefRegistrationRequestDto.getRegionId2(), companyChefRegistrationRequestDto.getRegionId3());
        List<StoreRegion> storeRegionList = convertToStoreRegion(store, regions);

        storeRegionRepository.saveAll(storeRegionList);

        return BaseResponseDto.onSuccess(ChefRegistrationResponseDto.builder().storeId(store.getId()).build(), ResponseCode.OK);
    }

    @Override
    @Transactional
    public BaseResponseDto<ChefRegistrationResponseDto> registerFreelanceChef(Long memberId, FreelanceChefRegistrationRequestDto freelanceChefRegistrationRequestDto) throws IOException {

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new StoreHandler(ResponseCode.MEMBER_NOT_FOUND));

        List<Auth> authList = new ArrayList<>();

        // 0. place 생성 및 저장
        Place place = placeRepository.save(Place.builder()
                .address(freelanceChefRegistrationRequestDto.getPlaceAddress())
                .name(freelanceChefRegistrationRequestDto.getPlaceName())
                .build());



        // 1. store 저장
        Store store = storeRepository.save(
                Store.builder()
                        .name(freelanceChefRegistrationRequestDto.getName())
                        .title(freelanceChefRegistrationRequestDto.getShortDescription())
                        .introduction(freelanceChefRegistrationRequestDto.getDetailedDescription())
                        .profileImage(member.getProfileImage())
                        .temperature(BigDecimal.valueOf(36.5))
                        .message(freelanceChefRegistrationRequestDto.getMessage())
                        .letter(getS3ImageLink(freelanceChefRegistrationRequestDto.getLetter(), "letters")) // 요리사 별 편지 이미지 저장(S3) - 그리고 그 링크를 Store 의 letter 에 저장
                        .member(member)
                        .place(place)
                        .build()
        );

        // 인증 정보 저장
        if (freelanceChefRegistrationRequestDto.getQualification()) {
            authList.add(authRepository.findById(1L).orElseThrow(() -> new StoreHandler(ResponseCode.AUTH_NOT_FOUND)));
        }

        authList.add(authRepository.findById(3L).orElseThrow(() -> new StoreHandler(ResponseCode.AUTH_NOT_FOUND)));
        authList.add(authRepository.findById(freelanceChefRegistrationRequestDto.getAuth()).orElseThrow(() -> new StoreHandler(ResponseCode.AUTH_NOT_FOUND)));

        storeAuthRepository.saveAll(convertToStoreAuth(store, authList));

        // 2. 만든 store 지역 매핑해서 StoreRegion 에 저장
        List<Region> regions = convertToRegion(freelanceChefRegistrationRequestDto.getRegionId1(), freelanceChefRegistrationRequestDto.getRegionId2(), freelanceChefRegistrationRequestDto.getRegionId3());
        storeRegionRepository.saveAll(convertToStoreRegion(store, regions));

        return BaseResponseDto.onSuccess(ChefRegistrationResponseDto.builder().storeId(store.getId()).build(), ResponseCode.OK);

    }

    private List<Region> convertToRegion(Long regionId1, Long regionId2, Long regionId3) {
        List<Region> regionList = new ArrayList<>();
        regionList.add(regionRepository.findById(regionId1).orElseThrow(() -> new StoreHandler(ResponseCode.REGION_NOT_FOUND)));
        regionList.add(regionRepository.findById(regionId2).orElseThrow(() -> new StoreHandler(ResponseCode.REGION_NOT_FOUND)));
        regionList.add(regionRepository.findById(regionId3).orElseThrow(() -> new StoreHandler(ResponseCode.REGION_NOT_FOUND)));

        return regionList;
    }

    private List<StoreRegion> convertToStoreRegion(Store store, List<Region> regions) {
        List<StoreRegion> storeRegionList = new ArrayList<>();
        for(Region region: regions) {
            storeRegionList.add(
                    StoreRegion.builder()
                    .region(region)
                    .store(store)
                    .build()
            );
        }

        return storeRegionList;
    }

    private List<StoreAuth> convertToStoreAuth(Store store, List<Auth> auths) {
        List<StoreAuth> storeAuthList = new ArrayList<>();
        for(Auth auth: auths) {
            storeAuthList.add(StoreAuth.builder()
                    .auth(auth)
                    .store(store)
                    .build());
        }

        return storeAuthList;
    }

    private String getS3ImageLink(MultipartFile multipartFile, String dirName) throws IOException {

        AwsS3 storeImage = (AwsS3) fileService.upload(multipartFile, dirName);

        return storeImage.getPath();

    }
}
