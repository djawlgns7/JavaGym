package controller.admin;

import domain.*;
import domain.trainer.Trainer;
import domain.trainer.WorkingHour;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.mindrot.jbcrypt.BCrypt;
import repository.AdminRepository;
import repository.TrainerRepository;
import service.AdminService;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.ResourceBundle;

import static converter.StringToDateConverter.stringToDate;
import static domain.trainer.SelectedTrainer.currentTrainer;
import static util.AlertUtil.*;
import static util.ControllerUtil.*;
import static util.PageUtil.movePageCenter;
import static util.ValidateUtil.addTrainerValidate;
import static util.ValidateUtil.isEmptyAnyField;

public class TrainerInfoController implements Initializable {

    private final TrainerRepository trainerRepository = new TrainerRepository();
    private final AdminRepository adminRepository = new AdminRepository();
    private final AdminService service = new AdminService(adminRepository);
    private final ResourceBundle config = ResourceBundle.getBundle("config.init");

    @FXML
    private TextField nameField, idField, phoneField, birthField;

    @FXML
    private RadioButton maleButton, femaleButton, amButton, pmButton;

    @FXML
    private TableView<Trainer> trainerTable;

    @FXML
    private TableColumn<Trainer, String> numCol, nameCol, idCol, genderCol, workTimeCol, birthCol, phoneCol;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        columnBindingTrainer(numCol, nameCol, idCol, genderCol, workTimeCol, birthCol, phoneCol);
        loadTrainerData(trainerTable, trainerRepository);

        trainerTable.setRowFactory(tv -> {
            TableRow<Trainer> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                try {
                    Trainer trainer = row.getItem();
                    trainerDetail(trainer, event);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            return row;
        });
    }

    @FXML
    private void addTrainer(ActionEvent event) throws IOException, ParseException {
        if (isEmptyAnyField(nameField, idField, birthField, phoneField, maleButton, femaleButton, amButton, pmButton)) {
            showAlertAddTrainerFail("emptyAnyField");
            return;
        }

        String phone = phoneField.getText().trim();
        String birth = birthField.getText().trim();
        String id = idField.getText().trim();

        if (addTrainerValidate(phone, id, birth)) return;

        Trainer trainer = new Trainer();
        trainer.setName(nameField.getText().trim());
        trainer.setId(id);
        trainer.setPassword(BCrypt.hashpw(config.getString("initial.trainer.password"), BCrypt.gensalt()));
        trainer.setGender(Gender.valueOf(getSelectedGender(maleButton, femaleButton)));
        trainer.setBirthDate(stringToDate(birth));
        trainer.setPhone(phone);
        trainer.setWorkingHour(WorkingHour.valueOf(getSelectedWorkingTime(amButton, pmButton)));

        service.addTrainer(trainer);
        showAlertAndMove("알림", "트레이너 등록 성공", Alert.AlertType.INFORMATION, "/view/admin/trainerInfo", event);
    }

    @FXML
    private void trainerDetail(Trainer trainer, MouseEvent event) throws IOException {
        if (trainer != null && event.getClickCount() == 2) {
            currentTrainer = trainer;
            movePageCenter(event, "/view/admin/trainerDetail");
        }
    }

    @FXML
    private void goBack(ActionEvent event) throws IOException {
        movePageCenter(event, "/view/admin/helloAdmin");
    }
}