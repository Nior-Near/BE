package nior_near.server.domain.order.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nior_near.server.domain.letter.entity.Letter;
import nior_near.server.domain.letter.entity.LetterStatus;
import nior_near.server.domain.letter.repository.LetterRepository;
import nior_near.server.domain.order.dto.request.OrderAddRequestDto;
import nior_near.server.domain.order.dto.response.OrderAddResponseDto;
import nior_near.server.domain.order.entity.Order;
import nior_near.server.domain.order.entity.OrderMenu;
import nior_near.server.domain.order.exception.handler.OrderHandler;
import nior_near.server.domain.order.repository.OrderMenuRepository;
import nior_near.server.domain.order.repository.OrderRepository;
import nior_near.server.domain.payment.entity.Payment;
import nior_near.server.domain.payment.entity.PaymentStatus;
import nior_near.server.domain.payment.repository.PaymentRepository;
import nior_near.server.domain.store.entity.Menu;
import nior_near.server.domain.store.entity.Store;
import nior_near.server.domain.store.repository.MenuRepository;
import nior_near.server.domain.store.repository.StoreRepository;
import nior_near.server.domain.user.entity.Member;
import nior_near.server.domain.user.repository.MemberRepository;
import nior_near.server.global.common.BaseResponseDto;
import nior_near.server.global.common.ResponseCode;
import nior_near.server.global.util.SmsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderCommandServiceImpl implements OrderCommandService {

    private final MemberRepository memberRepository;
    private final StoreRepository storeRepository;
    private final MenuRepository menuRepository;
    private final OrderRepository orderRepository;
    private final OrderMenuRepository orderMenuRepository;
    private final PaymentRepository paymentRepository;
    private final LetterRepository letterRepository;
    private final SmsService smsService;

    @Override
    @Transactional
    public BaseResponseDto<OrderAddResponseDto> addOrder(String memberName, OrderAddRequestDto orderAddRequestDto) {
        log.info("addOrder 함수 시작");

        log.info("memberRepository.findByName 실행");
        Member member = memberRepository.findByName(memberName).orElseThrow(() -> new OrderHandler(ResponseCode.MEMBER_NOT_FOUND));
        log.info("storeRepository.findById 실행");
        Store store = storeRepository.findById(orderAddRequestDto.getStoreId()).orElseThrow(() -> new OrderHandler(ResponseCode.STORE_NOT_FOUND));
        HashMap<Long, Menu> menus = getMenu(orderAddRequestDto.getMenus());

        Long totalPrice = getTotalPrice(menus, orderAddRequestDto.getMenus());

        log.info("paymentRepository.save 실행");
        Payment payment = paymentRepository.save(
                Payment.builder()
                        .price(totalPrice)
                        .paymentStatus(PaymentStatus.READY)
                        .build()
        );

        log.info("orderRepository.save 실행");
        Order order = orderRepository.save(
                Order.builder()
                        .member(member)
                        .store(store)
                        .place(store.getPlace())
                        .phone(orderAddRequestDto.getMemberPhone())
                        .requestMessage(orderAddRequestDto.getRequestMessage())
                        .totalPrice(totalPrice)
                        .orderUID(UUID.randomUUID().toString())
                        .payment(payment)
                        .build()
        );

        List<OrderMenu> orderMenuList = getOrderMenuList(menus, orderAddRequestDto.getMenus(), order);
        log.info("orderMenuRepository.saveAll 실행");
        orderMenuRepository.saveAll(orderMenuList);

        // 주문 생성 후, 편지 보내기
        log.info("store.getMember() 로 memberRepository.findById 발생");
        sendChefLetterToMember(store.getMember(), member, store.getLetter());

        // response 생성
        List<OrderAddResponseDto.OrderMenuInfo> orderMenuInfoList = getOrderMenuInfoList(menus, orderAddRequestDto.getMenus());

        OrderAddResponseDto orderAddResponseDto = OrderAddResponseDto.builder()
                .orderId(order.getId())
                .message(store.getMessage())
                .profileImage(store.getProfileImage())
                .totalPrice(totalPrice)
                .orderMenus(orderMenuInfoList)
                .build();

        smsService.sendMessage(order);

        return BaseResponseDto.onSuccess(orderAddResponseDto, ResponseCode.OK);
    }

    private Long getTotalPrice(HashMap<Long, Menu> menus, List<OrderAddRequestDto.OrderMenuItem> orderMenuItems) {
        long totalPrice = 0;

        for (int i = 0; i<menus.size(); i++) {
            totalPrice += menus.get(orderMenuItems.get(i).getMenuId()).getPrice() * orderMenuItems.get(i).getQuantity();
        }

        return totalPrice;
    }

    private List<OrderMenu> getOrderMenuList(HashMap<Long, Menu> menus, List<OrderAddRequestDto.OrderMenuItem> orderMenuItems, Order order) {
        List<OrderMenu> orderMenuList = new ArrayList<>();

        for (int i = 0; i<menus.size(); i++) {
            orderMenuList.add(
                    OrderMenu.builder()
                            .menu(menus.get(orderMenuItems.get(i).getMenuId()))
                            .order(order)
                            .quantity(orderMenuItems.get(i).getQuantity())
                            .build()
            );
        }

        return orderMenuList;
    }

    private List<OrderAddResponseDto.OrderMenuInfo> getOrderMenuInfoList(HashMap<Long, Menu> menus, List<OrderAddRequestDto.OrderMenuItem> orderMenuItems) {
        List<OrderAddResponseDto.OrderMenuInfo> orderMenuInfoList = new ArrayList<>();

        for (int i = 0; i<menus.size(); i++) {
            orderMenuInfoList.add(
                    OrderAddResponseDto.OrderMenuInfo.builder()
                    .menuName(menus.get(orderMenuItems.get(i).getMenuId()).getName())
                    .menuPrice(menus.get(orderMenuItems.get(i).getMenuId()).getPrice())
                    .quantity(orderMenuItems.get(i).getQuantity()).build()
            );
        }

        return orderMenuInfoList;
    }

    private HashMap<Long, Menu> getMenu(List<OrderAddRequestDto.OrderMenuItem> orderMenuItems) {
        HashMap<Long, Menu> menus = new HashMap<>();

        for(OrderAddRequestDto.OrderMenuItem orderMenuItem : orderMenuItems) {
            log.info("menuRepository.findById 실행");
            menus.put(
                    orderMenuItem.getMenuId(),
                    menuRepository.findById(orderMenuItem.getMenuId()).orElseThrow(() -> new OrderHandler(ResponseCode.MENU_NOT_FOUND))
            );
        }

        return menus;
    }

    private void sendChefLetterToMember(Member sender, Member receiver, String letterImageLink) {

        Letter letter = Letter.builder()
                .senderName(sender.getNickname())
                .imageLink(letterImageLink)
                .status(LetterStatus.UNREAD)
                .sender(sender)
                .receiver(receiver)
                .build();

        log.info("letterRepository.save 실행");
        letterRepository.save(letter);
    }
}
