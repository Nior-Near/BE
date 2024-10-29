package nior_near.server.domain.store.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nior_near.server.domain.order.entity.Place;
import nior_near.server.domain.store.dto.request.CompanyChefRegistrationRequestDto;
import nior_near.server.domain.store.dto.request.FreelanceChefRegistrationRequestDto;
import nior_near.server.domain.store.dto.request.MenuAddRequestDto;
import nior_near.server.domain.store.dto.response.ChefRegistrationResponseDto;
import nior_near.server.domain.store.dto.response.MenuAddResponseDto;
import nior_near.server.domain.store.entity.*;
import nior_near.server.domain.store.exception.handler.StoreHandler;
import nior_near.server.domain.store.repository.*;
import nior_near.server.domain.user.entity.Member;
import nior_near.server.domain.user.entity.UserAuthorization;
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
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreCommandServiceImpl implements StoreCommandService {

    private final StoreRepository storeRepository;
    private final PlaceRepository placeRepository;
    private final RegionRepository regionRepository;
    private final FileService fileService;
    private final AuthRepository authRepository;
    private final StoreAuthRepository storeAuthRepository;
    private final MemberRepository memberRepository;
    private final MenuRepository menuRepository;
    private final StoreImageRepository storeImageRepository;

    @Override
    @Transactional
    public BaseResponseDto<ChefRegistrationResponseDto> registerCompanyChef(String memberName, CompanyChefRegistrationRequestDto companyChefRegistrationRequestDto) throws IOException {

        Member member = memberRepository.findByName(memberName).orElseThrow(() -> new StoreHandler(ResponseCode.MEMBER_NOT_FOUND));

        List<Auth> authList = new ArrayList<>();

        // 1. store 저장
        Place place = placeRepository.findById(companyChefRegistrationRequestDto.getPlaceId()).orElseThrow(() -> new StoreHandler(ResponseCode.PLACE_NOT_FOUND));
        Region region = regionRepository.findById(place.getRegion().getId()).orElseThrow(() -> new StoreHandler(ResponseCode.REGION_NOT_FOUND));

        Store store = storeRepository.save(
                Store.builder()
                .name(member.getNickname())
                .title(companyChefRegistrationRequestDto.getShortDescription())
                .introduction(companyChefRegistrationRequestDto.getDetailedDescription())
                .profileImage(member.getProfileImage())
                .message(companyChefRegistrationRequestDto.getMessage())
                .letter(getS3ImageLink(companyChefRegistrationRequestDto.getLetter(), "letters")) // 요리사 별 편지 이미지 저장(S3) - 그리고 그 링크를 Store 의 letter 에 저장
                .member(member)
                .place(place)
                .region(region)
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

        // 멤버 권한 요리사로 변경
        member.setUserAuthorization(UserAuthorization.CHEF);

        return BaseResponseDto.onSuccess(ChefRegistrationResponseDto.builder().storeId(store.getId()).build(), ResponseCode.OK);
    }

    @Override
    @Transactional
    public BaseResponseDto<ChefRegistrationResponseDto> registerFreelanceChef(String memberName, FreelanceChefRegistrationRequestDto freelanceChefRegistrationRequestDto) throws IOException {

        Member member = memberRepository.findByName(memberName).orElseThrow(() -> new StoreHandler(ResponseCode.MEMBER_NOT_FOUND));

        List<Auth> authList = new ArrayList<>();

        // 0. place 생성 및 저장 & region 찾기
        Region region = regionRepository.findById(freelanceChefRegistrationRequestDto.getRegionId()).orElseThrow(() -> new StoreHandler(ResponseCode.STORE_NOT_FOUND));

        Place place = placeRepository.save(Place.builder()
                .address(freelanceChefRegistrationRequestDto.getPlaceAddress())
                .name(freelanceChefRegistrationRequestDto.getPlaceName())
                .region(region)
                .build());

        // 1. store 저장
        Store store = storeRepository.save(
                Store.builder()
                        .name(member.getNickname())
                        .title(freelanceChefRegistrationRequestDto.getShortDescription())
                        .introduction(freelanceChefRegistrationRequestDto.getDetailedDescription())
                        .profileImage(member.getProfileImage())
                        .message(freelanceChefRegistrationRequestDto.getMessage())
                        .letter(getS3ImageLink(freelanceChefRegistrationRequestDto.getLetter(), "letters")) // 요리사 별 편지 이미지 저장(S3) - 그리고 그 링크를 Store 의 letter 에 저장
                        .member(member)
                        .place(place)
                        .region(region)
                        .build()
        );

        // 인증 정보 저장
        if (freelanceChefRegistrationRequestDto.getQualification()) {
            authList.add(authRepository.findById(1L).orElseThrow(() -> new StoreHandler(ResponseCode.AUTH_NOT_FOUND)));
        }

        authList.add(authRepository.findById(3L).orElseThrow(() -> new StoreHandler(ResponseCode.AUTH_NOT_FOUND)));
        authList.add(authRepository.findById(freelanceChefRegistrationRequestDto.getAuth()).orElseThrow(() -> new StoreHandler(ResponseCode.AUTH_NOT_FOUND)));

        storeAuthRepository.saveAll(convertToStoreAuth(store, authList));

        // 멤버 권한 요리사로 변경
        member.setUserAuthorization(UserAuthorization.CHEF);

        return BaseResponseDto.onSuccess(ChefRegistrationResponseDto.builder().storeId(store.getId()).build(), ResponseCode.OK);

    }

    @Override
    @Transactional
    public BaseResponseDto<MenuAddResponseDto> addMenu(Long storeId, String memberName, MenuAddRequestDto menuAddRequestDto) throws IOException {

        Member member = memberRepository.findByName(memberName).orElseThrow(() -> new StoreHandler(ResponseCode.MEMBER_NOT_FOUND));
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new StoreHandler(ResponseCode.STORE_NOT_FOUND));

        // 권한 확인
        if (!store.getMember().equals(member)) {
            throw new StoreHandler(ResponseCode.STORE_UNAUTHORIZED);
        }

        Menu menu = menuRepository.save(
                Menu.builder().store(store)
                        .oneServing(menuAddRequestDto.getMenuOneServing())
                        .imageLink(getS3ImageLink(menuAddRequestDto.getMenuImage(), "menus"))
                        .introduction(menuAddRequestDto.getMenuIntroduction())
                        .name(menuAddRequestDto.getMenuName())
                        .build()
        );

        // 메뉴 음식 사진 storeImage 에 등록해놓기 - store 조회할 때 필요
         storeImageRepository.save(
                StoreImage.builder()
                        .store(store)
                        .imageLink(menu.getImageLink()).build()
        );

        return BaseResponseDto.onSuccess(MenuAddResponseDto.builder().menuId(menu.getId()).build(), ResponseCode.OK);

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
