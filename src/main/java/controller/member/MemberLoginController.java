package controller.member;

import domain.Member;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import repository.MemberRepository;
import service.MemberService;
import validate.LoginValidate;
import static service.MyUtils.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MemberLoginController implements Initializable {

    @FXML
    private TextField phone;

    @FXML
    private PasswordField password;

    private final MemberRepository repository = new MemberRepository();
    private final MemberService service = new MemberService(repository);
    private final LoginValidate validator = new LoginValidate();

    @FXML
    private void login() {
        String inputPhone = phone.getText();
        String inputPassword = password.getText();

        if (inputPhone.isEmpty()) {
            showAlert("로그인 실패", "전화번호를 입력하세요.", Alert.AlertType.WARNING);
            return;
        }

        if (validator.isWrongLengthPhone(inputPhone)) {
            showAlert("로그인 실패", "010을 제외한 8자리를 입력하세요.", Alert.AlertType.WARNING);
            return;
        }

        if (inputPassword.isEmpty()) {
            showAlert("로그인 실패", "비밀번호를 입력하세요.", Alert.AlertType.WARNING);
            return;
        }

        if (validator.isWrongLengthPassword(inputPassword)) {
            showAlert("로그인 실패", "비밀번호는 4자리입니다.", Alert.AlertType.WARNING);
            return;
        }

        if (repository.findByPhone(inputPhone) == null) {
            showAlert("로그인 실패", "등록되지 않은 회원입니다.", Alert.AlertType.WARNING);
            return;
        }

        Member loginMember = service.login(inputPhone, inputPassword);

        if (loginMember == null) {
            showAlert("로그인 실패", "비밀번호가 일치하지 않습니다.", Alert.AlertType.WARNING);
        } else {
            showAlert("로그인 성공", loginMember.getName() + "님 환영합니다^^", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void showSignUp(ActionEvent event) throws IOException {
        Parent signUpRoot = FXMLLoader.load(getClass().getResource("/view/signUp.fxml"));
        Scene signUpScene = new Scene(signUpRoot);

        signUpScene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        // 현재 Stage 정보를 가져오기 (회원가입 버튼이 있는 Stage)
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("SignUp");

        // Stage에 새로운 Scene 설정
        stage.setScene(signUpScene);
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        TextFormatter<String> phoneFormatter = new TextFormatter<>(change -> {
            String phone = change.getControlNewText();
            if (phone.matches("\\d{0,8}")) {
                return change;
            }
            return null;
        });

        TextFormatter<String> passwordFormatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d{0,4}")) {
                return change;
            }
            return null;
        });

        phone.setTextFormatter(phoneFormatter);
        password.setTextFormatter(passwordFormatter);
    }

    @FXML
    public void showAdmin(ActionEvent event) throws IOException {
        Parent signUpRoot = FXMLLoader.load(getClass().getResource("/view/adminLogin.fxml"));
        Scene signUpScene = new Scene(signUpRoot);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Admin");

        stage.setScene(signUpScene);
        stage.show();
    }
}