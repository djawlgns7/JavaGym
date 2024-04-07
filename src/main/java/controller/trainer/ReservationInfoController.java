package controller.trainer;

import domain.Gender;
import domain.Member;
import domain.SelectedMember;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.mindrot.jbcrypt.BCrypt;
import repository.TrainerRepository;
import service.trainer.TrainerService;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.ResourceBundle;

import static converter.StringToDateConverter.stringToDate;
import static util.AlertUtil.showAlertAddMemberFail;
import static util.AlertUtil.showAlertAndMove;
import static util.ControllerUtil.*;
import static util.PageUtil.movePageCenter;

public class ReservationInfoController implements Initializable {

    private static final ResourceBundle config = ResourceBundle.getBundle("config.init");
    private static final TrainerRepository trainerRepository = new TrainerRepository();
    private static final TrainerService service = new TrainerService(trainerRepository);

    @FXML
    private TextField nameField, birthField, phoneField, enrollField;

    @FXML
    private RadioButton maleButton, femaleButton;

    @FXML
    private void addMember(ActionEvent event) throws ParseException, IOException {
        if (isEmptyAnyField(nameField, enrollField, birthField, phoneField, maleButton, femaleButton)) {
            showAlertAddMemberFail("emptyAnyField");
            return;
        }

        String phone = phoneField.getText().trim();
        String enroll = enrollField.getText().trim();
        String birth = birthField.getText().trim();

        if (addMemberValidate(phone, enroll, birth)) return;

        Member member = new Member();
        member.setName(nameField.getText().trim());
        member.setPassword(BCrypt.hashpw(config.getString("initial.password"), BCrypt.gensalt()));
        member.setGender(Gender.valueOf(getSelectedGender(maleButton, femaleButton)));
        member.setEmail(enroll);
        member.setBirthDate(stringToDate(birth));
        member.setPhone(phone);


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
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            return row;
        });
    }

    @FXML
    private void goBack(ActionEvent event) throws IOException {
        movePageCenter(event, "/view/Trainer/helloTrainer");
    }
}
