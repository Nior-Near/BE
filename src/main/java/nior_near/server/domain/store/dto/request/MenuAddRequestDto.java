package nior_near.server.domain.store.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class MenuAddRequestDto {
    @NotBlank(message = "메뉴 이름을 입력해주세요.")
    private String menuName;
    @NotNull(message = "메뉴의 1인분 g(그램)을 입력해주세요.")
    private Integer menuOneServing;
    @NotBlank(message = "메뉴 설명을 입력해주세요.")
    private String menuIntroduction;
    @NotNull(message = "메뉴 사진을 업로드 해주세요.")
    private MultipartFile menuImage;
}
