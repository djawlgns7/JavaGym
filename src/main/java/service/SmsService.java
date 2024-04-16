package service;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;
import repository.CodeStore;

import java.util.ResourceBundle;

import static domain.member.SelectedMember.currentMember;
import static util.DialogUtil.showDialog;

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

    //관리자 호출 버튼을 누를 경우
    public void callAdmin(){
        if(callAdminTimer <= 0){
            int adminPhone = 12345678;
            Message message = new Message();
            message.setFrom(smsConfig.getString("myPhone"));
            message.setTo("010" + adminPhone);
            if(currentMember != null){
                String memberName = currentMember.getName();
                message.setText("[JavaGym] " + memberName + " 회원님께서 관리자를 호출했습니다");
            }else{
                message.setText("[JavaGym] " + "회원님께서 관리자를 호출했습니다");
            }

            try {
//            messageService.send(message); // 실행하면 돈 나감..!
                System.out.println(message.getText());
            } catch (Exception e) {
                e.printStackTrace();
            }

            countTimer();
            showDialog("관리자를 호출했습니다");
        }else{
            showDialog("관리자가 오고 있습니다 잠시만 기다려 주세요~");
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