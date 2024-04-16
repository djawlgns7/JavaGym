package controller.member;

import domain.member.Member;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import repository.MemberRepository;
import service.MemberService;
import service.SmsService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static domain.member.SelectedMember.currentMember;
import static util.PageUtil.movePage;

public class MemberLoginController implements Initializable {

    @FXML
    private TextField phoneField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ImageView profileImage;

    private final MemberRepository memberRepository = new MemberRepository();
    private final MemberService service = new MemberService(memberRepository);
    private final SmsService smsService = new SmsService();

    @FXML
    private void login(ActionEvent event) throws IOException {
        service.login(phoneField, passwordField, event);
    }

    @FXML
    private void showSignUp(ActionEvent event) throws IOException {
        currentMember = new Member();
        movePage(event, "/view/member/signUpForm");
    }

    @FXML
    public void showAdminLogin(ActionEvent event) throws IOException {
        currentMember = new Member();
        movePage(event, "/view/admin/adminLogin");
    }

    @FXML
    public void showTrainerLogin(ActionEvent event) throws IOException {
        currentMember = new Member();
        movePage(event, "/view/trainer/trainerLogin");
    }

    @FXML
    public void immediateEntry(ActionEvent event) throws IOException {
        service.immediateEntry(phoneField, passwordField, event);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        TextFormatter<String> phoneFormatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d{0,8}")) {
                return change;
            }
            return null;
        });

        TextFormatter<String> passwordFormatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d{0,4}")) {
                return change; // 변경을 허용
            }
            return null; // 변경을 무시
        });
        phoneField.setTextFormatter(phoneFormatter);
        passwordField.setTextFormatter(passwordFormatter);

        Image image = new Image("/image/JavaGym_Logo.jpeg");
        profileImage.setImage(image);

        Circle cilpCircle = new Circle(150, 150, 150);
        profileImage.setClip(cilpCircle);
    }

    @FXML
    public void callAdmin(){
        smsService.callAdmin();
    }
}