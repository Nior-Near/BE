package nior_near.server.domain.store.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nior_near.server.domain.store.application.StoreCommandService;
import nior_near.server.domain.store.application.StoreQueryService;
import nior_near.server.domain.store.dto.request.CompanyChefRegistrationRequestDto;
import nior_near.server.domain.store.dto.request.FreelanceChefRegistrationRequestDto;
import nior_near.server.domain.store.dto.request.MenuAddRequestDto;
import nior_near.server.domain.store.dto.response.*;
import nior_near.server.global.common.BaseResponseDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stores")
@Slf4j
public class StoreController {

    private final StoreCommandService storeCommandService;
    private final StoreQueryService storeQueryService;

    @PostMapping("/near-company")
    public BaseResponseDto<ChefRegistrationResponseDto> createCompanyStore(@Valid @ModelAttribute CompanyChefRegistrationRequestDto companyChefRegistrationRequestDto, @AuthenticationPrincipal UserDetails userDetails) throws IOException {

        return storeCommandService.registerCompanyChef(userDetails.getUsername(), companyChefRegistrationRequestDto);

    }

    @PostMapping("/freelance")
    public BaseResponseDto<ChefRegistrationResponseDto> createFreelanceStore(@Valid @ModelAttribute FreelanceChefRegistrationRequestDto freelanceChefRegistrationRequestDto, @AuthenticationPrincipal UserDetails userDetails) throws IOException {

        return storeCommandService.registerFreelanceChef(userDetails.getUsername(), freelanceChefRegistrationRequestDto);

    }

    @GetMapping("/chef-registration-info")
    public BaseResponseDto<ChefRegistrationInfoResponseDto> getChefRegistrationInfo(@AuthenticationPrincipal UserDetails userDetails) {

        return storeQueryService.getChefRegistrationInfo(userDetails.getUsername());

    }

    @PostMapping("/{storeId}/menu")
    public BaseResponseDto<MenuAddResponseDto> addMenu(@Valid @ModelAttribute MenuAddRequestDto menuAddRequestDto, @PathVariable("storeId") Long storeId, @AuthenticationPrincipal UserDetails userDetails) throws IOException {

        return storeCommandService.addMenu(storeId, userDetails.getUsername(), menuAddRequestDto);

    }

    @GetMapping("{storeId}")
    public BaseResponseDto<StoreResponseDto> getStore(@PathVariable("storeId") Long storeId) {

        return storeQueryService.getStore(storeId);
    }

    @GetMapping("/near-company/{placeId}")
    public BaseResponseDto<PlaceRegionGetResponse> getPlaceRegion(@PathVariable("placeId") Long placeId) {

        return storeQueryService.getPlaceRegion(placeId);

    }

    @DeleteMapping("/testDelete")
    public BaseResponseDto<String> deleteTestStore(@AuthenticationPrincipal UserDetails userDetails) throws IOException {
        return storeCommandService.deleteStore(userDetails.getUsername());
    }


}
