package nior_near.server.domain.store.application;

import nior_near.server.domain.store.dto.request.CompanyChefRegistrationRequestDto;
import nior_near.server.domain.store.dto.request.FreelanceChefRegistrationRequestDto;
import nior_near.server.domain.store.dto.request.MenuAddRequestDto;
import nior_near.server.domain.store.dto.response.ChefRegistrationResponseDto;
import nior_near.server.domain.store.dto.response.MenuAddResponseDto;
import nior_near.server.global.common.BaseResponseDto;

import java.io.IOException;

public interface StoreCommandService {

    BaseResponseDto<ChefRegistrationResponseDto> registerCompanyChef(String memberName, CompanyChefRegistrationRequestDto companyChefRegistrationRequestDto) throws IOException;

    BaseResponseDto<ChefRegistrationResponseDto> registerFreelanceChef(String memberName, FreelanceChefRegistrationRequestDto freelanceChefRegistrationRequestDto) throws IOException;

    BaseResponseDto<String> deleteStore(String memberName) throws IOException;

    BaseResponseDto<MenuAddResponseDto> addMenu(Long storeId, String memberName, MenuAddRequestDto menuAddRequestDto) throws IOException;
}
