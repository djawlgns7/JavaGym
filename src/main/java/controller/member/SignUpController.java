package controller.member;

import domain.Gender;
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
import org.mindrot.jbcrypt.BCrypt;
import repository.MemberRepository;
import service.MemberService;
import validate.SignUpValidate;
import static service.MyUtils.*;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.ResourceBundle;

import static converter.StringToDateConverter.*;

public class SignUpController implements Initializable {

    private final MemberRepository repository = new MemberRepository();
    private final MemberService service = new MemberService(repository);
    private final SignUpValidate validator = new SignUpValidate();

    @FXML
    private TextField name, emailId, birth, phone;

    @FXML
    private ComboBox<String> emailDomain;

    @FXML
    private PasswordField password, passwordConfirm;

    @FXML
    private RadioButton male, female;

    public String getSelectedGender() {
        if (male.isSelected()) return "M";
        if (female.isSelected()) return "F";
        return null;
    }

    @FXML
    private void signUp(ActionEvent event) throws IOException, ParseException {

        if (signUpValidate()) return;

        Member member = new Member();
        member.setName(name.getText().trim());
        member.setPassword(BCrypt.hashpw(password.getText().trim(), BCrypt.gensalt()));
        member.setGender(Gender.valueOf(getSelectedGender()));
        member.setEmail(getFullEmail(emailId.getText().trim(), emailDomain.getValue().trim()));
        member.setBirthDate(stringToDate(birth.getText().trim()));
        member.setPhone(phone.getText().trim());

        Member signUpMember = service.signUp(member);
        showAlert("성공", signUpMember.getName() + "님 회원가입 완료", Alert.AlertType.INFORMATION);
        goBack(event);
    }

    private boolean signUpValidate() {

        if (emptyAnyField()) {
            showAlert("경고", "모든 값을 입력해주세요.", Alert.AlertType.WARNING);
            return true;
        }

        String inputPassword = password.getText();
        String inputPasswordConfirm = passwordConfirm.getText();
        String inputPhone = phone.getText();
        String inputEmail = getFullEmail(emailId.getText().trim(), emailDomain.getValue().trim());

        if (!inputPassword.equals(inputPasswordConfirm)) {
            showAlert("오류", "비밀번호 확인.", Alert.AlertType.WARNING);
            return true;
        }

        if (validator.isDuplicatePhone(inputPhone) && validator.isDuplicateEmail(inputEmail)) {
            showAlert("오류", "이메일과 전화번호 중복", Alert.AlertType.WARNING);
            return true;
        }

        if (validator.isDuplicateEmail(inputEmail)) {
            showAlert("오류", "이메일 중복", Alert.AlertType.WARNING);
            return true;
        }

        if (validator.isWrongBirth(birth.getText())) {
            showAlert("오류", "생년월일 오류", Alert.AlertType.WARNING);
            return true;
        }

        if (validator.isDuplicatePhone(inputPhone)) {
            showAlert("오류", "전화번호 중복", Alert.AlertType.INFORMATION);
            return true;
        }
        return false;
    }

    private boolean emptyAnyField() {
        return name.getText().trim().isEmpty() ||
                password.getText().trim().isEmpty() ||
                passwordConfirm.getText().trim().isEmpty() ||
                getSelectedGender() == null ||
                emailId.getText().trim().isEmpty() ||
                emailDomain.getValue() == null ||
                emailDomain.getEditor().getText().trim().isEmpty() ||
                birth.getText().trim().isEmpty() ||
                phone.getText().trim().isEmpty();
    }

    @FXML
    private void goBack(ActionEvent event) throws IOException {
        Parent loginRoot = FXMLLoader.load(getClass().getResource("/view/memberLogin.fxml"));
        Scene scene = new Scene(loginRoot);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Login");

        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        password.textProperty().addListener((observable, oldValue, newValue) -> {
            stylePassword();
        });
        passwordConfirm.textProperty().addListener((observable, oldValue, newValue) -> {
            stylePassword();
        });

        TextFormatter<String> passwordFormatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d{0,4}")) {
                return change; // 변경을 허용
            }
            return null; // 변경을 무시
        });

        TextFormatter<String> passwordConfirmFormatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d{0,4}")) {
                return change;
            }
            return null;
        });

        TextFormatter<String> birthFormatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d{0,6}")) {
                return change;
            }
            return null;
        });

        TextFormatter<String> phoneFormatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d{0,8}")) {
                return change;
            }
            return null;
        });

        password.setTextFormatter(passwordFormatter);
        passwordConfirm.setTextFormatter(passwordConfirmFormatter);
        birth.setTextFormatter(birthFormatter);
        phone.setTextFormatter(phoneFormatter);
    }

    private void stylePassword() {
        boolean containsStyle = passwordConfirm.getStyleClass().contains("password-field-error");

        if (!password.getText().equals(passwordConfirm.getText())) {
            if (!containsStyle) { // 스타일이 아직 없으면 추가
                passwordConfirm.getStyleClass().add("password-field-error");
            }
        } else {
            passwordConfirm.getStyleClass().remove("password-field-error"); // 비밀번호가 일치하면 스타일 제거
        }
    }
}