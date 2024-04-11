package controller.member;

import domain.Gender;
import domain.member.Member;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.mindrot.jbcrypt.BCrypt;
import repository.MemberRepository;
import service.MemberService;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

import static util.AlertUtil.*;
import static util.AlertUtil.showAlert;
import static util.ControllerUtil.*;
import static converter.StringToDateConverter.stringToDate;
import static util.PageUtil.*;
import static util.StyleUtil.stylePassword;
import static util.ValidateUtil.isEmptyAnyField;
import static util.ValidateUtil.signUpValidate;

public class SignUpController implements Initializable {

    private final MemberRepository repository = new MemberRepository();
    private final MemberService service = new MemberService(repository);

    @FXML
    private TextField nameField, emailIdField, birthField, phoneField;

    @FXML
    private ComboBox<String> emailDomainField;

    @FXML
    private PasswordField passwordField, passwordConfirmField;

    @FXML
    private RadioButton maleButton, femaleButton;

    @FXML
    private void signUp(ActionEvent event) throws IOException, ParseException {

        if (isEmptyAnyField(nameField, emailIdField, emailDomainField, birthField, phoneField, passwordField, passwordConfirmField, maleButton, femaleButton)) {
            showAlertSignUpFail("emptyAnyField");
            return;
        }

        String name = nameField.getText().trim();
        String password = passwordField.getText().trim();
        String passwordConfirm = passwordConfirmField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = getFullEmail(emailIdField.getText().trim(), emailDomainField.getValue().trim());
        String birth = birthField.getText().trim();

        if (signUpValidate(name, password, passwordConfirm, phone, email, birth)) return;

        Member member = new Member();
        member.setName(name);
        member.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        member.setGender(Gender.valueOf(getSelectedGender(maleButton, femaleButton)));
        member.setEmail(email);
        member.setBirthDate(stringToDate(birth));
        member.setPhone(phone);

        Member signUpMember = service.signUp(member);
        showAlert(signUpMember.getName() + "님 회원가입 완료^^", Alert.AlertType.INFORMATION);
        goBack(event);
    }

    @FXML
    private void goBack(ActionEvent event) throws IOException {
        movePage(event, "/view/member/memberLogin");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            stylePassword(passwordField, passwordConfirmField);
        });
        passwordConfirmField.textProperty().addListener((observable, oldValue, newValue) -> {
            stylePassword(passwordField, passwordConfirmField);
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

        // 이름에 숫자 입력 못하도록 추가 (성진)
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getText();
            if (text.matches("[^0-9]*")) {
                return change;
            }
            return null;
        };

        TextFormatter<String> nameFormatter = new TextFormatter<>(filter);

        nameField.setTextFormatter(nameFormatter);
        passwordField.setTextFormatter(passwordFormatter);
        passwordConfirmField.setTextFormatter(passwordConfirmFormatter);
        birthField.setTextFormatter(birthFormatter);
        phoneField.setTextFormatter(phoneFormatter);
    }
}