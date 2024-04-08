package controller.admin;

import domain.Gender;
import domain.member.Member;
import domain.member.SelectedMember;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.mindrot.jbcrypt.BCrypt;
import repository.AdminRepository;
import repository.MemberRepository;
import service.AdminService;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.ResourceBundle;

import static converter.StringToDateConverter.stringToDate;
import static util.AlertUtil.*;
import static util.ControllerUtil.*;
import static util.PageUtil.*;
import static util.ValidateUtil.addMemberValidate;
import static util.ValidateUtil.isEmptyAnyField;

public class MemberInfoController implements Initializable {

    private final ResourceBundle config = ResourceBundle.getBundle("config.init");
    private final AdminRepository adminRepository = new AdminRepository();
    private final AdminService service = new AdminService(adminRepository);
    private final MemberRepository memberRepository = new MemberRepository();

    @FXML
    private TextField nameField, birthField, phoneField, emailField;

    @FXML
    private RadioButton maleButton, femaleButton;

    @FXML
    private void addMember(ActionEvent event) throws ParseException, IOException {
        if (isEmptyAnyField(nameField, emailField, birthField, phoneField, maleButton, femaleButton)) {
            showAlertAddMemberFail("emptyAnyField");
            return;
        }

        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String birth = birthField.getText().trim();

        if (addMemberValidate(phone, email, birth)) return;

        Member member = new Member();
        member.setName(nameField.getText().trim());
        member.setPassword(BCrypt.hashpw(config.getString("initial.member.password"), BCrypt.gensalt()));
        member.setGender(Gender.valueOf(getSelectedGender(maleButton, femaleButton)));
        member.setEmail(email);
        member.setBirthDate(stringToDate(birth));
        member.setPhone(phone);

        service.addMember(member);
        showAlertAndMove("알림", "회원 등록 성공", Alert.AlertType.INFORMATION, "/view/admin/memberInfo", event);
    }

    @FXML
    private void memberDetail(Member member, MouseEvent event) throws IOException {
        if (member != null && event.getClickCount() == 2) {
            SelectedMember.currentMember = member;
            movePageCenter(event, "/view/admin/memberDetail");
        }
    }

    @FXML
    private TableView<Member> membersTable;

    @FXML
    private TableColumn<Member, String> numCol, nameCol, genderCol, emailCol, birthCol, phoneCol, enrollCol;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        columnBindingMember(numCol, nameCol, genderCol, emailCol, birthCol, phoneCol, enrollCol);
        loadMemberData(membersTable, memberRepository);

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

        birthField.setTextFormatter(birthFormatter);
        phoneField.setTextFormatter(phoneFormatter);

        membersTable.setRowFactory(tv -> {
            TableRow<Member> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                try {
                    Member member = row.getItem();
                    memberDetail(member, event);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            return row;
        });
    }

    @FXML
    private void goBack(ActionEvent event) throws IOException {
        movePageCenter(event, "/view/admin/helloAdmin");
    }
}