package domain.service;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;
import repository.CodeStore;

import java.util.ResourceBundle;

public class SmsService {

    private static final ResourceBundle smsConfig = ResourceBundle.getBundle("config.sms");
    DefaultMessageService messageService = NurigoApp.INSTANCE.initialize(smsConfig.getString("apiKey"), smsConfig.getString("apiSecret"), "https://api.coolsms.co.kr");
    private final CodeStore store = CodeStore.getInstance();

    public void send(String phone) {

        Message message = new Message();
        message.setFrom(smsConfig.getString("myPhone"));
        message.setTo("010" + phone);
        int code = (int) (Math.random() * 9000) + 1000;
        message.setText("[JavaGym] 인증번호를 정확히 입력해 주세요.\n" + "인증번호 : " + String.valueOf(code));

        try {
//            messageService.send(message); // 실행하면 돈 나감..!
            System.out.println(code);
            store.storeCode(phone, code);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}