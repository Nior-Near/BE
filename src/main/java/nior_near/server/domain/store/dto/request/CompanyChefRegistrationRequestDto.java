package nior_near.server.domain.store.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
public class CompanyChefRegistrationRequestDto {
    @NotBlank(message = "요리사님을 설명하는 짧은 소개를 입력해주세요.")
    private String shortDescription; // title
    @NotBlank(message = "요리사님을 설명하는 자세한 소개를 입력해주세요.")
    private String detailedDescription; // introduction
    @NotNull(message = "자격 등록 부분을 선택해주세요.")
    private Boolean qualification;
    @NotNull(message = "경력을 선택해주세요.")
    private Long auth;
    @NotNull(message = "편지 사진을 업로드해주세요.")
    private MultipartFile letter;
    @NotNull
    private Long placeId;
    @NotBlank(message = "주문 완료 시 고객에게 보여질 문구를 입력해주세요.")
    private String message;
}
