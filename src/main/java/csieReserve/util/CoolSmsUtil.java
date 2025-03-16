package csieReserve.util;

import jakarta.annotation.PostConstruct;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.model.MessageType;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CoolSmsUtil {


    private final String apiKey = "NCSR4XZHFG3PREFM";

    private final String apiSecret = "BNPAQSN1SI3MD4FYDOPX2SCUOS7CBHYC";

    @Value("${coolsms.from.number}") // 발신자 번호 주입
    private String fromNumber;

    DefaultMessageService messageService; // 메시지 서비스를 위한 객체

    @PostConstruct // 의존성 주입이 완료된 후 초기화를 수행하는 메서드
    public void init(){
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr"); // 메시지 서비스 초기화
    }

    // 단일 메시지 발송
    public void sendSMS(String to, String msg){
        Message message = new Message(); // 새 메시지 객체 생성
        message.setFrom("01071430417"); // 발신자 번호 설정
        message.setTo(to); // 수신자 번호 설정
        message.setText(msg); // 메시지 내용 설정

        this.messageService.sendOne(new SingleMessageSendingRequest(message)); // 메시지 발송 요청
    }

    public void sendLMS(String to, String msg) {
        Message message = new Message();
        message.setFrom("01071430417"); // 발신자 번호
        message.setTo(to); // 수신자 번호
        message.setText(msg); // 메시지 내용
        message.setSubject("[회의실 예약 취소 안내]");
        message.setType(MessageType.LMS); // LMS 타입으로 설정

        this.messageService.sendOne(new SingleMessageSendingRequest(message));
    }
}