package nior_near.server.domain.store.application;

import nior_near.server.domain.order.entity.Place;
import nior_near.server.domain.store.dto.request.CompanyChefRegistrationRequestDto;
import nior_near.server.domain.store.dto.request.FreelanceChefRegistrationRequestDto;
import nior_near.server.domain.store.dto.response.ChefRegistrationResponseDto;
import nior_near.server.domain.store.entity.Auth;
import nior_near.server.domain.store.entity.Region;
import nior_near.server.domain.store.entity.Store;
import nior_near.server.domain.store.repository.*;
import nior_near.server.domain.user.entity.Member;
import nior_near.server.domain.user.repository.MemberRepository;
import nior_near.server.global.common.AwsS3;
import nior_near.server.global.common.BaseResponseDto;
import nior_near.server.global.util.FileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StoreCommandServiceImplTest {
    @Mock
    private StoreRepository storeRepository;
    @Mock
    private PlaceRepository placeRepository;
    @Mock
    private RegionRepository regionRepository;
    @Mock
    private FileService fileService;
    @Mock
    private AuthRepository authRepository;
    @Mock
    private StoreAuthRepository storeAuthRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private StoreImageRepository storeImageRepository;

    @InjectMocks
    private StoreCommandServiceImpl storeCommandService;

    @Test
    void registerCompanyChef() {
        // given
        MockMultipartFile multipartFile = new MockMultipartFile("test_file", "test_file".getBytes());
        String memberName = "test";
        CompanyChefRegistrationRequestDto companyChefRegistrationRequestDto = CompanyChefRegistrationRequestDto.builder()
                .shortDescription("shortDescription")
                .detailedDescription("detailDescription")
                .auth(1L)
                .letter(multipartFile)
                .message("message")
                .placeId(1L)
                .qualification(true)
                .build();
        Member member = Member.builder()
                .nickname("nickname")
                .email("email")
                .type("type")
                .phone("010")
                .userId("memberName")
                .profileImage("profileImage").build();
        Region region = Region.builder()
                .id(1L).name("test region").upperId(2L).build();
        Place place = Place.builder()
                .region(region)
                .address("test address")
                .name("test place").build();
        Store store = Store.builder()
                .name(member.getNickname())
                .title(companyChefRegistrationRequestDto.getShortDescription())
                .introduction(companyChefRegistrationRequestDto.getDetailedDescription())
                .profileImage(member.getProfileImage())
                .message(companyChefRegistrationRequestDto.getMessage())
                .letter("letter link") // 요리사 별 편지 이미지 저장(S3) - 그리고 그 링크를 Store 의 letter 에 저장
                .member(member)
                .place(place)
                .region(region)
                .build();
        Auth auth = Auth.builder().id(1L).authName("test auth").build();
        AwsS3 awsS3 = AwsS3.builder().key("test key").path("letter link").build();

        // when
        when(memberRepository.findByName(any(String.class))).thenReturn(Optional.of(member));
        when(storeRepository.findByMember(any(Member.class))).thenReturn(Optional.empty());
        when(regionRepository.findById(any(Long.class))).thenReturn(Optional.of(region));
        when(placeRepository.findById(any(Long.class))).thenReturn(Optional.of(place));
        when(storeRepository.save(any(Store.class))).thenReturn(store);
        when(authRepository.findById(any(Long.class))).thenReturn(Optional.of(auth));
        when(fileService.upload(any(MultipartFile.class), any(String.class))).thenReturn(awsS3);

        BaseResponseDto<ChefRegistrationResponseDto> result = storeCommandService.registerCompanyChef(memberName, companyChefRegistrationRequestDto);

        // then
        assertEquals(store.getId(), result.getResult().getStoreId());

    }

    @Test
    void registerFreelanceChef() {
        // given
        MockMultipartFile multipartFile = new MockMultipartFile("test_file", "test_file".getBytes());
        String memberName = "test";
        FreelanceChefRegistrationRequestDto freelanceChefRegistrationRequestDto = FreelanceChefRegistrationRequestDto.builder()
                .shortDescription("shortDescription")
                .detailedDescription("detailDescription")
                .regionId(1L)
                .auth(1L)
                .placeAddress("placeAddress")
                .placeName("placeName")
                .letter(multipartFile)
                .message("message")
                .qualification(true)
                .build();
        Member member = Member.builder()
                .nickname("nickname")
                .email("email")
                .type("type")
                .phone("010")
                .userId("memberName")
                .profileImage("profileImage").build();
        Region region = Region.builder()
                .id(1L).name("test region").upperId(2L).build();
        Place place = Place.builder()
                .region(region)
                .address("test address")
                .name("test place").build();
        Store store = Store.builder()
                .name(member.getNickname())
                .title(freelanceChefRegistrationRequestDto.getShortDescription())
                .introduction(freelanceChefRegistrationRequestDto.getDetailedDescription())
                .profileImage(member.getProfileImage())
                .message(freelanceChefRegistrationRequestDto.getMessage())
                .letter("letter link") // 요리사 별 편지 이미지 저장(S3) - 그리고 그 링크를 Store 의 letter 에 저장
                .member(member)
                .place(place)
                .region(region)
                .build();
        Auth auth = Auth.builder().id(1L).authName("test auth").build();
        AwsS3 awsS3 = AwsS3.builder().key("test key").path("letter link").build();

        // when
        when(memberRepository.findByName(any(String.class))).thenReturn(Optional.of(member));
        when(storeRepository.findByMember(any(Member.class))).thenReturn(Optional.empty());
        when(regionRepository.findById(any(Long.class))).thenReturn(Optional.of(region));
        when(placeRepository.save(any(Place.class))).thenReturn(place);
        when(storeRepository.save(any(Store.class))).thenReturn(store);
        when(authRepository.findById(any(Long.class))).thenReturn(Optional.of(auth));
        when(fileService.upload(any(MultipartFile.class), any(String.class))).thenReturn(awsS3);

        BaseResponseDto<ChefRegistrationResponseDto> result = storeCommandService.registerFreelanceChef(memberName, freelanceChefRegistrationRequestDto);

        // then
        assertEquals(store.getId(), result.getResult().getStoreId());
    }

    @Test
    void deleteStore() {
    }

    @Test
    void addMenu() {
    }
}