package controller.admin;

import domain.Gender;
import domain.Member;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import org.mindrot.jbcrypt.BCrypt;
import repository.AdminRepository;
import repository.MemberRepository;
import service.admin.AdminService;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.Optional;
import java.util.ResourceBundle;

import static converter.StringToDateConverter.stringToDate;
import static util.AlertUtil.*;
import static util.ControllerUtil.*;
import static util.PageUtil.*;

public class MemberInfoController implements Initializable {

    private static final ResourceBundle config = ResourceBundle.getBundle("config.init");
    private static final AdminRepository adminRepository = new AdminRepository();
    private static final AdminService service = new AdminService(adminRepository);

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
        member.setPassword(BCrypt.hashpw(config.getString("initial.password"), BCrypt.gensalt()));
        member.setGender(Gender.valueOf(getSelectedGender(maleButton, femaleButton)));
        member.setEmail(email);
        member.setBirthDate(stringToDate(birth));
        member.setPhone(phone);

        service.addMember(member);
        showAlertAndMove("알림", "회원 등록 성공", Alert.AlertType.INFORMATION, "/view/admin/memberInfo", event);
    }

    @FXML
    private void updateMember(ActionEvent event) throws IOException {

    }

    @FXML
    private void deleteMember(ActionEvent event) throws IOException {

    }

    @FXML
    private void memberDetail(Member member, MouseEvent event) throws IOException {
        if (member != null && event.getClickCount() == 2) {
            System.out.println(member);
        }
    }

    @FXML
    private TableView<Member> membersTable;

    @FXML
    private TableColumn<Member, String> numCol, nameCol, genderCol, emailCol, birthCol, phoneCol, enrollCol;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        columnBinding(numCol, nameCol, genderCol, emailCol, birthCol, phoneCol, enrollCol);
        loadMemberData(membersTable);

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