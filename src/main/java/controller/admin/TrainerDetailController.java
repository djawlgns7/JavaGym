package controller.admin;

import domain.*;
import domain.trainer.Trainer;
import domain.trainer.TrainerSchedule;
import domain.trainer.WorkingHour;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import repository.ReservationRepository;
import repository.TrainerRepository;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static domain.trainer.SelectedTrainer.currentTrainer;
import static util.AlertUtil.showAlertAndMove;
import static util.AlertUtil.showAlertChoose;
import static util.ControllerUtil.*;
import static util.PageUtil.*;

public class TrainerDetailController implements Initializable {

    @FXML
    private TextField nameField, idField, phoneField, heightField, weightField;

    @FXML
    private DatePicker birthPicker;

    @FXML
    private RadioButton maleButton, femaleButton, amButton, pmButton;

    @FXML
    private TableView<TrainerSchedule> scheduleTable;

    @FXML
    private TableColumn<TrainerSchedule, String> countColumn, memberNameColumn, dateColumn, timeColumn;

    private final ToggleGroup genderRadio = new ToggleGroup();
    private final ToggleGroup workTimeRadio = new ToggleGroup();

    private final ReservationRepository reservationRepository = new ReservationRepository();
    private final TrainerRepository trainerRepository = new TrainerRepository();

    @FXML
    private void updateTrainer(ActionEvent event) throws IOException {

        // 검증 로직 추후 구현

        // 정상 로직
        currentTrainer.setId(idField.getText().trim());
        currentTrainer.setName(nameField.getText().trim());
        currentTrainer.setGender(Gender.valueOf(getSelectedGender(maleButton, femaleButton)));
        currentTrainer.setPhone(phoneField.getText().trim());
        currentTrainer.setBirthDate(Date.valueOf(birthPicker.getValue()));
        currentTrainer.setWorkingHour(WorkingHour.valueOf(getSelectedWorkingTime(amButton, pmButton)));
        currentTrainer.setHeight(Double.valueOf(heightField.getText().trim()));
        currentTrainer.setWeight(Double.valueOf(weightField.getText().trim()));
        Optional<ButtonType> result = showAlertChoose("트레이너 정보를 수정하시겠습니까?");

        if (result.get() == ButtonType.OK){
            trainerRepository.updateTrainer(currentTrainer);
            showAlertAndMove("트레이너가 수정되었습니다.", Alert.AlertType.INFORMATION, "/view/admin/trainerInfo", event);
        }
    }

    @FXML
    private void deleteTrainer(ActionEvent event) throws IOException {
        Optional<ButtonType> result = showAlertChoose("정말로 " + currentTrainer.getName() + " 트레이너를 삭제하시겠습니까?");

        if (result.get() == ButtonType.OK){
            trainerRepository.deleteTrainer(currentTrainer.getNum());
            showAlertAndMove("트레이너가 삭제되었습니다.", Alert.AlertType.INFORMATION, "/view/admin/trainerInfo", event);
        }
    }

    @FXML
    private void goBack(ActionEvent event) throws IOException {
        movePageCenter(event, "/view/admin/trainerInfo");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (currentTrainer != null) {
            setTrainerInfo(currentTrainer, birthPicker);
            columnBinding();
            loadTrainerSchedule();
        }
    }

    private void setTrainerInfo(Trainer currentTrainer, DatePicker birthPicker) {

        Trainer trainer = trainerRepository.findById(currentTrainer.getId());
        LocalDate birthDate = trainer.getBirthDate().toLocalDate();

        nameField.setText(trainer.getName());
        idField.setText(trainer.getId());
        phoneField.setText(trainer.getPhone());
        birthPicker.setValue(birthDate);
        heightField.setText(trainer.getHeight().toString());
        weightField.setText(trainer.getWeight().toString());

        maleButton.setToggleGroup(genderRadio);
        femaleButton.setToggleGroup(genderRadio);
        amButton.setToggleGroup(workTimeRadio);
        pmButton.setToggleGroup(workTimeRadio);

        if (trainer.getGender().equals(Gender.M)) {
            genderRadio.selectToggle(maleButton);
        } else {
            genderRadio.selectToggle(femaleButton);
        }

        if (trainer.getWorkingHour().equals(WorkingHour.AM)) {
            workTimeRadio.selectToggle(amButton);
        } else {
            workTimeRadio.selectToggle(pmButton);
        }
    }

    private void columnBinding() {
        countColumn.setCellValueFactory(new PropertyValueFactory<>("sequence"));
        memberNameColumn.setCellValueFactory(new PropertyValueFactory<>("memberName"));
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                new SimpleDateFormat("yyyy-MM-dd").format(cellData.getValue().getReservationDate())
        ));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("reservationTime"));
    }

    private void loadTrainerSchedule() {
        List<TrainerSchedule> schedule = reservationRepository.findTrainerSchedule(currentTrainer.getNum());
        ObservableList<TrainerSchedule> schedules = FXCollections.observableArrayList();

        for (TrainerSchedule trainerSchedule : schedule) {
            schedules.add(trainerSchedule);
        }

        scheduleTable.setItems(schedules);
    }
}