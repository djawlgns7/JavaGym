package controller.member;

import domain.Gender;
import domain.member.Member;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.mindrot.jbcrypt.BCrypt;
import repository.CodeStore;
import repository.MemberRepository;
import service.MemberService;
import service.SmsService;
//import service.SmsService;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.ResourceBundle;

import static util.AlertUtil.*;
import static util.AlertUtil.showAlert;
import static util.ControllerUtil.*;
import static converter.StringToDateConverter.stringToDate;
import static util.PageUtil.*;
import static util.StyleUtil.stylePassword;
import static util.ValidateUtil.*;
import static util.ValidateUtil.isEmptyAnyField;
import static util.ValidateUtil.signUpValidate;

public class SignUpController implements Initializable {

    private final MemberRepository repository = new MemberRepository();
    private final MemberService service = new MemberService(repository);
    private final SmsService smsService = new SmsService();
    private final CodeStore codeStore = CodeStore.getInstance();

    @FXML
    private TextField nameField, emailIdField, birthField, phoneField, codeField;

    @FXML
    private ComboBox<String> emailDomainField;

    @FXML
    private PasswordField passwordField, passwordConfirmField;

    @FXML
    private RadioButton maleButton, femaleButton;

    @FXML
    private Button sendButton, checkButton;

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

        // 전화번호를 인증한 사용자만 회원가입 가능
        if (!codeStore.codeCheck) {
            showAlertSignUpFail("NotAuthentication");
            return;
        }

        if (signUpValidate(password, passwordConfirm, phone, email, birth)) return;

        Member member = new Member();
        member.setName(name);
        member.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        member.setGender(Gender.valueOf(getSelectedGender(maleButton, femaleButton)));
        member.setEmail(email);
        member.setBirthDate(stringToDate(birth));
        member.setPhone(phone);

        Member signUpMember = service.signUp(member);
        showAlert(signUpMember.getName() + "님. 회원가입을 환영합니다!", Alert.AlertType.INFORMATION);
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

        TextFormatter<String> codeFormatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d{0,4}")) {
                return change;
            }
            return null;
        });

        passwordField.setTextFormatter(passwordFormatter);
        passwordConfirmField.setTextFormatter(passwordConfirmFormatter);
        birthField.setTextFormatter(birthFormatter);
        phoneField.setTextFormatter(phoneFormatter);
        codeField.setTextFormatter(codeFormatter);

        codeStore.codeCheck = false;
        codeStore.phoneCheck = false;
        codeStore.isSend = false;
    }


    @FXML
    private void sendNumber() {
        String phone = phoneField.getText();

        // 전화번호를 입력하지 않은 경우
        if (phone.isEmpty()) {
            showAlertSignUpFail("emptyPhone");
            return;
        }

        // 전화번호가 중복 됐을 경우
        if (isDuplicatePhone(phone)) {
            showAlertSignUpFail("duplicatePhone");
            return;
        }

        // 전화번호 길이를 잘못 입력했을 경우
        if (isWrongLengthPhone(phone)) {
            showAlertSignUpFail("wrongPhone");
            return;
        }

        // 정상 로직

        // 인증번호 전송
        smsService.send(phone);

        // 전송 버튼을 재전송으로 바꾼다.
        sendButton.setText("재전송");

        codeStore.phoneCheck = true;
        codeStore.isSend = true;
        showAlertUseMessage("sendCode");
    }

    @FXML
    private void checkNumber() {
        String phone = phoneField.getText();
        String code = codeField.getText();

        // 전화번호와 인증번호를 입력하지 않았을 경우
        if (phone.isEmpty() && code.isEmpty()) {
            showAlertSignUpFail("emptyPhone");
            return;
        }

        // 인증번호를 발송하지 않은 경우
        if (!codeStore.isSend) {
            showAlertSignUpFail("notSend");
            return;
        }

        // 인증번호를 발송했으나 입력하지 않은 경우
        if (code.isEmpty() && codeStore.phoneCheck) {
            showAlertSignUpFail("emptyCode");
            return;
        }

        // 정상 로직

        int inputCode = Integer.parseInt(codeField.getText());

        // 입력한 값과 인증번호가 일치하는 경우
        if (codeStore.verifyCode(phoneField.getText(), inputCode)) {
            showAlertSignUpFail("correctCode");
            codeStore.codeCheck = true;

            // 버튼을 사용할 수 없도록 변경
            sendButton.setDisable(true);
            checkButton.setDisable(true);

            // 전화번호와 인증번호를 수정할 수 없도록 변경
            phoneField.setEditable(false);
            codeField.setEditable(false);

            // 메모리에서 전화번호와 인증번호 제거
            codeStore.removeCode(phone);
        } else {
            showAlertSignUpFail("failCode");
        }
    }
}