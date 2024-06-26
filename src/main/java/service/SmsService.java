package service;

import javafx.scene.control.ButtonType;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;
import repository.CodeStore;

import java.util.Optional;
import java.util.ResourceBundle;

import static domain.member.SelectedMember.loginMember;
import static util.DialogUtil.*;

public class SmsService {

    private static final ResourceBundle smsConfig = ResourceBundle.getBundle("config.sms");
    static DefaultMessageService messageService = NurigoApp.INSTANCE.initialize(smsConfig.getString("apiKey"), smsConfig.getString("apiSecret"), "https://api.coolsms.co.kr");
    private final CodeStore store = CodeStore.getInstance();
    private static int callAdminTimer = 0;

    public void sendCode(String phone) {
        Message message = new Message();
        message.setFrom(smsConfig.getString("myPhone"));
        message.setTo("010" + phone);
        int code = getRandomPassword();
        message.setText("[JavaGym]\n인증번호를 정확히 입력해 주세요.\n" + "인증번호 : " + code);

        try {
//            messageService.send(message);
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
//            messageService.send(message);
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
//            messageService.send(message);
            System.out.println(password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getRandomPassword() {
        return (int) (Math.random() * 9000) + 1000;
    }

    //관리자 호출 버튼을 누를 경우
    public void callAdmin(){
        if (callAdminTimer > 0) {
            showDialogBasicMessage("waitAdmin");
            return;
        }

        Optional<ButtonType> result = showDialogChooseMessage("callAdmin?");

        if (result.get() == ButtonType.OK) {
            if (callAdminTimer <= 0) {
                String adminPhone = smsConfig.getString("myPhone");
                Message message = new Message();
                message.setFrom(adminPhone);
                message.setTo(adminPhone);
                if (loginMember != null) {
                    String memberName = loginMember.getName();
                    message.setText("[JavaGym]\n" + memberName + " 회원님이 직원을 호출했습니다");
                } else {
                    message.setText("[JavaGym]\n" + "회원님이 직원을 호출했습니다");
                }

                try {
//                    messageService.send(message);
                    System.out.println(message.getText());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                countTimer();
                showDialogBasicMessage("callAdmin");
            }
        }
    }

    public void countTimer(){
        callAdminTimer = 60;

        Thread thread = new Thread(() -> {
            while(callAdminTimer-- > 0){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        thread.start();
    }
}