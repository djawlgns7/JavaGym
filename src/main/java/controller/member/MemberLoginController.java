package controller.member;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import repository.MemberRepository;
import service.MemberService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static util.PageUtil.*;

public class MemberLoginController implements Initializable {

    @FXML
    private TextField phone;

    @FXML
    private PasswordField password;

    private final MemberRepository repository = new MemberRepository();
    private final MemberService service = new MemberService(repository);

    @FXML
    private void login(ActionEvent event) throws IOException {
        String inputPhone = phone.getText();
        String inputPassword = password.getText();

        service.login(inputPhone, inputPassword, event);
    }

    @FXML
    private void showSignUp(ActionEvent event) throws IOException {
        movePageAddCss(event, "SignUp", "/view/member/signUpForm", "/css/style");
    }

    @FXML
    public void showAdminLogin(ActionEvent event) throws IOException {
        movePage(event, "Admin", "/view/admin/adminLogin");
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
        phone.setTextFormatter(phoneFormatter);
        password.setTextFormatter(passwordFormatter);
    }
}