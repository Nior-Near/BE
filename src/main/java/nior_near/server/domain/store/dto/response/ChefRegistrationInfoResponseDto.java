package nior_near.server.domain.store.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChefRegistrationInfoResponseDto {
    String name;
    String phone;
}
