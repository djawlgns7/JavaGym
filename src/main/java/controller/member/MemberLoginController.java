package controller.member;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import repository.MemberRepository;
import service.member.MemberService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static util.PageUtil.*;

public class MemberLoginController implements Initializable {

    private final MemberRepository repository = new MemberRepository();
    private final MemberService service = new MemberService(repository);

    @FXML
    private TextField phoneField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void login(ActionEvent event) throws IOException {
        service.login(phoneField, passwordField, event);
    }

    @FXML
    private void showSignUp(ActionEvent event) throws IOException {
        movePage(event, "/view/member/signUpForm", "/css/password");
    }

    @FXML
    private void entry(ActionEvent event) throws IOException {
        service.entry(phoneField, passwordField, event);
    }

    @FXML
    public void showAdminLogin(ActionEvent event) throws IOException {
        movePage(event, "/view/admin/adminLogin");
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
    }
}