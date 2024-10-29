package nior_near.server.domain.store.application;

import nior_near.server.domain.store.dto.request.CompanyChefRegistrationRequestDto;
import nior_near.server.domain.store.dto.request.FreelanceChefRegistrationRequestDto;
import nior_near.server.domain.store.dto.response.ChefRegistrationResponseDto;
import nior_near.server.domain.store.entity.Store;
import nior_near.server.domain.store.repository.StoreRepository;
import nior_near.server.global.common.BaseResponseDto;
import nior_near.server.global.util.FileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
@SqlGroup({
        @Sql(value = "classpath:sql/store-service-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS),
        @Sql(value = "classpath:sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
//@DataJpaTest(showSql = true)
class StoreCommandServiceImplTest {

    @Autowired
    StoreCommandService storeCommandService;
    @Autowired
    StoreRepository storeRepository;
    @Autowired
    FileService fileService;
    @Value("${s3.path}")
    String path;

    @Test
    void registerCompanyChef는_memberName과_CompanyChefRegistrationRequestDto로_store를_등록할_수_있다() throws IOException {

        //given
        MockMultipartFile letterFile = new MockMultipartFile(
                "편지 이미지",
                "letter.png",
                MediaType.IMAGE_PNG_VALUE,
                "letter".getBytes()
        );
        CompanyChefRegistrationRequestDto companyChefRegistrationRequestDto = CompanyChefRegistrationRequestDto.builder()
                .auth(4L)
                .letter(letterFile)
                .detailedDescription("detailedDescription")
                .qualification(true)
                .placeId(1L)
                .shortDescription("shortDescription")
                .message("message")
                .build();
        String memberName = "a12324546578987798";

        //when
        BaseResponseDto<ChefRegistrationResponseDto> chefRegistrationResponseDtoBaseResponseDto = storeCommandService.registerCompanyChef(memberName, companyChefRegistrationRequestDto);
        Optional<Store> store = storeRepository.findById(chefRegistrationResponseDtoBaseResponseDto.getResult().getStoreId());

        //then
        assertThat(store.isPresent()).isEqualTo(true);

        // S3 데이터 삭제
        fileService.remove(store.get().getLetter().replaceAll(path, ""));
    }

    @Test
    void registerFreelanceChef는_memberName과_FreelanceChefRgistrationRequestDto로_store를_등록할_수_있다() throws IOException {
        //given
        MockMultipartFile letterFile = new MockMultipartFile(
                "편지 이미지",
                "letter.png",
                MediaType.IMAGE_PNG_VALUE,
                "letter".getBytes()
        );
        FreelanceChefRegistrationRequestDto freelanceChefRegistrationRequestDto = FreelanceChefRegistrationRequestDto.builder()
                .auth(4L)
                .letter(letterFile)
                .detailedDescription("detailedDescription")
                .qualification(true)
                .placeAddress("placeAddress")
                .regionId(1L)
                .placeName("placeName")
                .shortDescription("shortDescription")
                .message("message")
                .build();
        String memberName = "a12324546578987798";

        //when
        BaseResponseDto<ChefRegistrationResponseDto> chefRegistrationResponseDtoBaseResponseDto = storeCommandService.registerFreelanceChef(memberName, freelanceChefRegistrationRequestDto);
        Optional<Store> store = storeRepository.findById(chefRegistrationResponseDtoBaseResponseDto.getResult().getStoreId());

        //then
        assertThat(store.isPresent()).isEqualTo(true);

        // S3 데이터 삭제
        fileService.remove(store.get().getLetter().replaceAll(path, ""));
    }

    @Test
    void addMenu() {
        //given
        //when
        //then
    }
}