package controller.member;

import util.AlertUtil;
import domain.Gender;
import domain.Member;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.mindrot.jbcrypt.BCrypt;
import repository.MemberRepository;
import service.MemberService;
import util.PageUtil;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.ResourceBundle;

import static util.AlertUtil.showAlert;
import static util.ControllerUtil.*;
import static converter.StringToDateConverter.stringToDate;
import static util.StyleUtil.stylePassword;

public class SignUpController implements Initializable {

    private final MemberRepository repository = new MemberRepository();
    private final MemberService service = new MemberService(repository);

    @FXML
    private TextField name, emailId, birth, phone;

    @FXML
    private ComboBox<String> emailDomain;

    @FXML
    private PasswordField password, passwordConfirm;

    @FXML
    private RadioButton male, female;

    @FXML
    private void signUp(ActionEvent event) throws IOException, ParseException {

        if (isEmptyAnyField(name, emailId, emailDomain, birth, phone, password, passwordConfirm, male, female)) {
            AlertUtil.showAlertSignUpFail("emptyAnyField");
            return;
        }

        String inputPw = password.getText().trim();
        String inputPwConfirm = passwordConfirm.getText().trim();
        String inputPhone = phone.getText().trim();
        String fullEmail = getFullEmail(emailId.getText().trim(), emailDomain.getValue().trim());
        String inputBirth = birth.getText().trim();

        if (signUpValidate(inputPw, inputPwConfirm, inputPhone, fullEmail, inputBirth)) return;

        Member member = new Member();
        member.setName(name.getText().trim());
        member.setPassword(BCrypt.hashpw(inputPw, BCrypt.gensalt()));
        member.setGender(Gender.valueOf(getSelectedGender(male, female)));
        member.setEmail(fullEmail);
        member.setBirthDate(stringToDate(inputBirth));
        member.setPhone(inputPhone);

        Member signUpMember = service.signUp(member);
        showAlert("알림", signUpMember.getName() + "님 회원가입 완료^^", Alert.AlertType.INFORMATION);
        goBack(event);
    }

    @FXML
    private void goBack(ActionEvent event) throws IOException {
        // static import
        PageUtil.movePage(event, "Login", "/view/member/memberLogin");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        password.textProperty().addListener((observable, oldValue, newValue) -> {
            stylePassword(password, passwordConfirm);
        });
        passwordConfirm.textProperty().addListener((observable, oldValue, newValue) -> {
            stylePassword(password, passwordConfirm);
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
}