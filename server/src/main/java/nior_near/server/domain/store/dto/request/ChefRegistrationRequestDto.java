package nior_near.server.domain.store.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class ChefRegistrationRequestDto {
    private String name;
    private String shortDescription; // title
    private String detailedDescription; // introduction
    private Boolean qualification;
    private Long auth;
    private MultipartFile letter;
    private Long placeId;
    private Long regionId1;
    private Long regionId2;
    private Long regionId3;
    private String message;
}