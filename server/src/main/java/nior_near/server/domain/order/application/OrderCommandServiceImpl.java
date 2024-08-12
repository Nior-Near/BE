package nior_near.server.domain.order.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nior_near.server.domain.order.dto.request.OrderAddRequestDto;
import nior_near.server.domain.order.dto.response.OrderAddResponseDto;
import nior_near.server.domain.order.entity.Order;
import nior_near.server.domain.order.entity.OrderMenu;
import nior_near.server.domain.order.exception.handler.OrderHandler;
import nior_near.server.domain.order.repository.OrderMenuRepository;
import nior_near.server.domain.order.repository.OrderRepository;
import nior_near.server.domain.store.entity.Menu;
import nior_near.server.domain.store.entity.Store;
import nior_near.server.domain.store.repository.MenuRepository;
import nior_near.server.domain.store.repository.StoreRepository;
import nior_near.server.domain.user.entity.Member;
import nior_near.server.domain.user.repository.MemberRepository;
import nior_near.server.global.common.BaseResponseDto;
import nior_near.server.global.common.ResponseCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderCommandServiceImpl implements OrderCommandService {

    private final MemberRepository memberRepository;
    private final StoreRepository storeRepository;
    private final MenuRepository menuRepository;
    private final OrderRepository orderRepository;
    private final OrderMenuRepository orderMenuRepository;

    @Override
    @Transactional
    public BaseResponseDto<OrderAddResponseDto> addOrder(Long memberId, OrderAddRequestDto orderAddRequestDto) {

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new OrderHandler(ResponseCode.MEMBER_NOT_FOUND));
        Store store = storeRepository.findById(orderAddRequestDto.getStoreId()).orElseThrow(() -> new OrderHandler(ResponseCode.STORE_NOT_FOUND));

        Long totalPrice = getTotalPrice(orderAddRequestDto.getMenus());

        /**
         * TODO: totalPrice 와 결제할 멤버 정보를 매개변수로 가지는 결제 로직 추가될 것
         */

        Order order = orderRepository.save(
                Order.builder()
                        .member(member)
                        .store(store)
                        .place(store.getPlace())
                        .phone(orderAddRequestDto.getMemberPhone())
                        .requestMessage(orderAddRequestDto.getRequestMessage())
                        .totalPrice(totalPrice)
                        .build()
        );

        List<OrderMenu> orderMenuList = getOrderMenuList(orderAddRequestDto.getMenus(), order);
        orderMenuRepository.saveAll(orderMenuList);

        List<OrderAddResponseDto.OrderMenuInfo> orderMenuInfoList = getOrderMenuInfoList(orderAddRequestDto.getMenus());

        OrderAddResponseDto orderAddResponseDto = OrderAddResponseDto.builder()
                .orderId(order.getId())
                .message(store.getMessage())
                .profileImage(store.getProfileImage())
                .totalPrice(totalPrice)
                .orderMenus(orderMenuInfoList)
                .build();

        return BaseResponseDto.onSuccess(orderAddResponseDto, ResponseCode.OK);
    }

    private Long getTotalPrice(List<OrderAddRequestDto.OrderMenuItem> menus) {
        long totalPrice = 0;

        for (OrderAddRequestDto.OrderMenuItem orderMenuItem : menus) {
            Menu menu = menuRepository.findById(orderMenuItem.getMenuId()).orElseThrow(() -> new OrderHandler(ResponseCode.MENU_NOT_FOUND));
            totalPrice += menu.getPrice() * orderMenuItem.getQuantity();
        }

        return totalPrice;
    }

    private List<OrderMenu> getOrderMenuList(List<OrderAddRequestDto.OrderMenuItem> menus, Order order) {
        List<OrderMenu> orderMenuList = new ArrayList<>();

        for (OrderAddRequestDto.OrderMenuItem orderMenuItem : menus) {
            Menu menu = menuRepository.findById(orderMenuItem.getMenuId()).orElseThrow(() -> new OrderHandler(ResponseCode.MENU_NOT_FOUND));
            orderMenuList.add(OrderMenu.builder().menu(menu).order(order).quantity(orderMenuItem.getQuantity()).build());
        }

        return orderMenuList;
    }

    private List<OrderAddResponseDto.OrderMenuInfo> getOrderMenuInfoList(List<OrderAddRequestDto.OrderMenuItem> menus) {
        List<OrderAddResponseDto.OrderMenuInfo> orderMenuInfoList = new ArrayList<>();

        for (OrderAddRequestDto.OrderMenuItem orderMenuItem : menus) {
            Menu menu = menuRepository.findById(orderMenuItem.getMenuId()).orElseThrow(() -> new OrderHandler(ResponseCode.MENU_NOT_FOUND));
            orderMenuInfoList.add(
                    OrderAddResponseDto.OrderMenuInfo.builder()
                    .menuName(menu.getName())
                    .menuPrice(menu.getPrice())
                    .quantity(orderMenuItem.getQuantity()).build()
            );
        }

        return orderMenuInfoList;
    }
}