package controller.admin;

import domain.Gender;
import domain.member.Member;
import domain.member.SelectedMember;
import domain.member.UsingLocker;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.mindrot.jbcrypt.BCrypt;
import repository.AdminRepository;
import repository.MemberRepository;
import repository.PurchaseRepository;
import service.AdminService;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

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
    private final PurchaseRepository purchaseRepository = new PurchaseRepository();

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

        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String birth = birthField.getText().trim();

        if (addMemberValidate(name, phone, email, birth)) return;

        Member member = new Member();
        member.setName(name);
        member.setPassword(BCrypt.hashpw(config.getString("initial.member.password"), BCrypt.gensalt()));
        member.setGender(Gender.valueOf(getSelectedGender(maleButton, femaleButton)));
        member.setEmail(email);
        member.setBirthDate(stringToDate(birth));
        member.setPhone(phone);

        service.addMember(member);
        showAlertAndMove("회원 등록 성공", Alert.AlertType.INFORMATION, "/view/admin/memberInfo", event);
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

        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getText();
            if (text.matches("[^0-9]*")) {
                return change;
            }
            return null;
        };

        TextFormatter<String> nameFormatter = new TextFormatter<>(filter);
        TextFormatter<String> searchNameFormatter = new TextFormatter<>(filter);

        nameField.setTextFormatter(nameFormatter);
        searchNameField.setTextFormatter(searchNameFormatter);
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
    private void showUsingLocker() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("사물함 정보");

        ButtonType closeButtonType = new ButtonType("닫기", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(closeButtonType);
        Button closeButton = (Button) dialog.getDialogPane().lookupButton(closeButtonType);
        closeButton.getStyleClass().add("closeBtn");

        TableView<UsingLocker> table = new TableView<>();
        table.getStyleClass().add("tableView");
        loadLockerInfo(table, purchaseRepository);

        VBox vbox = new VBox(table);
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setContent(vbox);
        dialogPane.getStylesheets().add(getClass().getResource("/css/LockerInfo.css").toExternalForm());
        dialog.showAndWait();
    }

    @FXML
    private TextField searchNameField;

    @FXML
    private void searchMember() {
        String searchName = searchNameField.getText().trim();

        if (searchName.isEmpty()) {
            showAlert("이름을 입력해 주세요.", Alert.AlertType.INFORMATION);
            return;
        }

        List<Member> searchedMembers = memberRepository.searchMembersByName(searchName);

        if (searchedMembers.isEmpty()) {
            showAlert("해당 이름의 회원이 없습니다.", Alert.AlertType.INFORMATION);
            return;
        }
        ObservableList<Member> observableList = FXCollections.observableArrayList(searchedMembers);
        membersTable.setItems(observableList);
    }

    @FXML
    private void resetPage(ActionEvent event) throws IOException {
        movePageCenter(event, "/view/admin/memberInfo");
    }

    @FXML
    private void goBack(ActionEvent event) throws IOException {
        movePageCenter(event, "/view/admin/helloAdmin");
    }
}