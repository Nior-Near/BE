package nior_near.server.domain.store.application;

import nior_near.server.domain.store.dto.request.CompanyChefRegistrationRequestDto;
import nior_near.server.domain.store.dto.request.FreelanceChefRegistrationRequestDto;
import nior_near.server.domain.store.dto.response.ChefRegistrationResponseDto;
import nior_near.server.global.common.BaseResponseDto;

import java.io.IOException;

public interface StoreCommandService {

    BaseResponseDto<ChefRegistrationResponseDto> registerCompanyChef(Long memberId, CompanyChefRegistrationRequestDto companyChefRegistrationRequestDto) throws IOException;
    BaseResponseDto<ChefRegistrationResponseDto> registerFreelanceChef(Long memberId, FreelanceChefRegistrationRequestDto freelanceChefRegistrationRequestDto) throws IOException;

}
