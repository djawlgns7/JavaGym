package service;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;
import repository.CodeStore;

import java.util.ResourceBundle;

public class SmsService {

    private static final ResourceBundle smsConfig = ResourceBundle.getBundle("config.sms");
    static DefaultMessageService messageService = NurigoApp.INSTANCE.initialize(smsConfig.getString("apiKey"), smsConfig.getString("apiSecret"), "https://api.coolsms.co.kr");
    private final CodeStore store = CodeStore.getInstance();

    public void sendCode(String phone) {
        Message message = new Message();
        message.setFrom(smsConfig.getString("myPhone"));
        message.setTo("010" + phone);
        int code = getRandomPassword();
        message.setText("[JavaGym]\n인증번호를 정확히 입력해 주세요.\n" + "인증번호 : " + code);

        try {
//            messageService.send(message); // 실행하면 돈 나감..!
            System.out.println(code);
            store.storeCode(phone, code);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMemberInitPassword(String phone, int password) {
        Message message = new Message();
        message.setFrom(smsConfig.getString("myPhone"));
        message.setTo("010" + phone);
        message.setText("[JavaGym]\n키오스크에서 사용하는 비밀번호입니다. 외부에 노출되지 않게 주의하세요!\n" + "비밀번호 : " + password);

        try {
//            messageService.send(message); // 실행하면 돈 나감..!
            System.out.println(password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendTrainerInitPassword(String phone, String password) {
        Message message = new Message();
        message.setFrom(smsConfig.getString("myPhone"));
        message.setTo("010" + phone);
        message.setText("[JavaGym]\n트레이너 페이지에서 사용하는 비밀번호입니다. 외부에 노출되지 않게 주의하세요!\n" + "비밀번호 : " + password);

        try {
//            messageService.send(message); // 실행하면 돈 나감..!
            System.out.println(password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getRandomPassword() {
        return (int) (Math.random() * 9000) + 1000;
    }
}