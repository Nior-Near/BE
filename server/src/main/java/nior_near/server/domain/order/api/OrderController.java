package nior_near.server.domain.order.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nior_near.server.domain.order.application.OrderCommandService;
import nior_near.server.domain.order.dto.request.OrderAddRequestDto;
import nior_near.server.domain.order.dto.response.OrderAddResponseDto;
import nior_near.server.global.common.BaseResponseDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
@Slf4j
public class OrderController {

    private final OrderCommandService orderCommandService;

    @PostMapping
    public BaseResponseDto<OrderAddResponseDto> addOrder(@ModelAttribute OrderAddRequestDto orderAddRequestDto) {

        /**
         * TODO: 추후에 accessToken 에서 받아올 정보
         */
        Long memberId = 3L;

        return orderCommandService.addOrder(memberId, orderAddRequestDto);

    }
}
