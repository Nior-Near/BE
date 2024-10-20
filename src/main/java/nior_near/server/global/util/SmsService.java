package nior_near.server.global.util;

import lombok.RequiredArgsConstructor;
import nior_near.server.domain.order.entity.Order;
import nior_near.server.domain.order.repository.OrderRepository;
import nior_near.server.domain.user.entity.Member;
import org.springframework.beans.factory.annotation.Value;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
public class SmsService {
//    private static final Logger logger = LoggerFactory.getLogger(SmsService.class);
    private DefaultMessageService messageService;
    private final OrderRepository orderRepository;

    @Value("${coolsms.api.key}")
    private String apiKey;

    @Value("${coolsms.api.secret}")
    private String apiSecretKey;

    @Value("${coolsms.from.number}")
    private String senderNumber;

    @PostConstruct
    public void init() {
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecretKey, "https://api.coolsms.co.kr");
    }

    public void sendMessage(Order order) {
        String phoneNumber = order.getPhone().replaceAll("-", "");  // 반드시 01012345678 형식
        String name = order.getMember().getName();
        String datetime = order.getCreatedAt().toString();
        String orderNo = order.getId().toString();
        String text = "[니어니어] 주문 완료 안내\n" + "\n" +
                "안녕하세요, " + name + "님.\n" + "\n" +
                "니어니어에서의 주문이 성공적으로 완료되었습니다.\n" + "\n" +
                "- 주문 일시: " + datetime + "\n" +
                "- 주문 번호: " + orderNo + "\n" + "\n" +
                "실시간 주문 현황은 니어니어 마이페이지에서 확인하실 수 있습니다.\n" + "\n" +
                "주문에 대한 문의는 니어니어 고객센터로 연락해 주세요.\n" + "\n" +
                "따뜻한 편지와 함께 소중한 식사 경험을 즐기세요!\n" + "\n" +
                "감사합니다.\n" + "\n" +
                "니어니어 바로가기: https://www.niornear.store/main";

        Message message = new Message();
        message.setFrom(senderNumber);
        message.setTo(phoneNumber);
        message.setText(text);
        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
    }

    public void sendLetterMessage(Member sender, Member receiver, String letterContent) {
        String phoneNumber = receiver.getPhone().replaceAll("-", "");  // 반드시 01012345678 형식
//        String cookName = order.getStore().getMember().getName();
//        String clientName = order.getMember().getName();
        String text = "[니어니어] 편지가 도착했어요 💌\n" + "\n" +
                "안녕하세요, " + receiver.getName() + " 요리사님.\n" + "\n" +
                "요리사님의 음식을 주문한 " + sender.getName() + " 고객님으로부터 따뜻한 편지가 도착했습니다. \n" + "\n" +
                "• 편지내용: \n" +
                letterContent + "\n" + "\n" +
                "니어니어 바로가기: https://www.niornear.store/main";

        Message message = new Message();
        message.setFrom(senderNumber);
        message.setTo(phoneNumber);
        message.setText(text);
        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
    }

}
