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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.FileChooser;
import org.mindrot.jbcrypt.BCrypt;
import repository.ReservationRepository;
import repository.TrainerRepository;
import service.SmsService;

import java.io.*;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

import static domain.trainer.SelectedTrainer.loginTrainer;
import static service.SmsService.getRandomPassword;
import static util.DialogUtil.*;
import static util.ControllerUtil.*;
import static util.PageUtil.*;
import static util.ValidateUtil.updateTrainerValidate;

public class TrainerDetailController implements Initializable {

    @FXML
    private TextField nameField, idField, phoneField, heightField, weightField;

    @FXML
    private ImageView imageView;
    private String updatedImagePath;

    @FXML
    private GridPane trainerDetailGrid;

    @FXML
    private DatePicker birthPicker;

    @FXML
    private RadioButton maleButton, femaleButton, amButton, pmButton;

    @FXML
    private TableView<TrainerSchedule> scheduleTable;

    @FXML
    private TableColumn<TrainerSchedule, String> countColumn, memberNameColumn, dateColumn, timeColumn;

    @FXML
    private final ToggleGroup genderRadio = new ToggleGroup();
    @FXML
    private final ToggleGroup workTimeRadio = new ToggleGroup();

    private final ReservationRepository reservationRepository = new ReservationRepository();
    private final TrainerRepository trainerRepository = new TrainerRepository();
    private final SmsService smsService = new SmsService();
    private final ResourceBundle basicMessage = ResourceBundle.getBundle("message.basic");

    @FXML
    private void updateTrainer(ActionEvent event) throws IOException {

        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        Gender gender = Gender.valueOf(getSelectedGender(maleButton, femaleButton));
        String phone = phoneField.getText().trim();
        Date birth = Date.valueOf(birthPicker.getValue());
        WorkingHour workingHour = WorkingHour.valueOf(getSelectedWorkingTime(amButton, pmButton));
        Double height = Double.valueOf(heightField.getText().trim());
        Double weight = Double.valueOf(weightField.getText().trim());

        if (isSame(id, name, gender, phone, birth, workingHour, height, weight) && isSamePhoto()) {
            showDialogErrorMessage("isSame");
            return;
        }

        Optional<ButtonType> result = showDialogChooseMessage("reallyUpdateTrainer");
        if (result.get() == ButtonType.OK) {

            if (updateTrainerValidate(name, phone, id, height, weight)) return;

            if (!isSamePhoto()) {
                File photoFile = new File(updatedImagePath);
                trainerRepository.savePhoto(loginTrainer.getNum(), photoFile);
            }

            loginTrainer.setId(id);
            loginTrainer.setName(name);
            loginTrainer.setGender(gender);
            loginTrainer.setPhone(phone);
            loginTrainer.setBirthDate(birth);
            loginTrainer.setWorkingHour(workingHour);
            loginTrainer.setHeight(height);
            loginTrainer.setWeight(weight);

            trainerRepository.updateTrainer(loginTrainer);
            showDialogAndMovePageMessage("updateTrainer", "/view/admin/trainerDetail", event);
        }
    }

    private boolean isSame(String id, String name, Gender gender, String phone, Date birth, WorkingHour workingHour, Double height, Double weight) {
        return loginTrainer.getId().equals(id) && loginTrainer.getName().equals(name) && loginTrainer.getGender().equals(gender) && loginTrainer.getPhone().equals(phone) && loginTrainer.getBirthDate().equals(birth) && loginTrainer.getWorkingHour().equals(workingHour) && loginTrainer.getHeight().equals(height) && loginTrainer.getWeight().equals(weight);
    }

    private boolean isSamePhoto() {
        byte[] currentTrainerPhoto = loginTrainer.getPhoto();

        if (currentTrainerPhoto != null && updatedImagePath != null) {
            return false;
        }

        if (currentTrainerPhoto == null && updatedImagePath != null) {
            return false;
        }

        return true;
    }

    @FXML
    private void deleteTrainer(ActionEvent event) throws IOException {
        Optional<ButtonType> result = showDialogChoose(loginTrainer.getName() + " " + basicMessage.getString("reallyDeleteTrainer"));

        if (result.get() == ButtonType.OK){
            trainerRepository.deleteTrainer(loginTrainer.getNum());

            AdminTab.getInstance().setSelectedTabIndex(1);
            showDialogAndMovePageMessage("deleteTrainer", "/view/admin/helloAdminV2", event);
        }
    }

    @FXML
    private void goBack(ActionEvent event) throws IOException {
        AdminTab.getInstance().setSelectedTabIndex(1);
        movePage(event, "/view/admin/helloAdminV2");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (loginTrainer != null) {
            setTrainerInfo(loginTrainer, birthPicker);
            columnBinding();
            loadTrainerSchedule();

            TextFormatter<String> phoneFormatter = new TextFormatter<>(change -> {
                String newText = change.getControlNewText();
                if (newText.matches("\\d{0,8}")) {
                    return change;
                }
                return null;
            });

            UnaryOperator<TextFormatter.Change> filter = change -> {
                String newText = change.getControlNewText();
                if (newText.matches("^\\d*\\.?\\d*$")) {
                    return change;
                }
                return null;
            };

            TextFormatter<String> heightFormatter = new TextFormatter<>(filter);
            TextFormatter<String> weightFormatter = new TextFormatter<>(filter);

            heightField.setTextFormatter(heightFormatter);
            weightField.setTextFormatter(weightFormatter);
            phoneField.setTextFormatter(phoneFormatter);

            // GridPane 행 설정
            RowConstraints[] rows = new RowConstraints[7];
            for (int i = 0; i < rows.length; i++) {
                rows[i] = new RowConstraints();
                rows[i].setPercentHeight(50);
                trainerDetailGrid.getRowConstraints().add(rows[i]);
            }
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

        byte[] photoBytes = trainer.getPhoto();
        if (photoBytes != null) {
            ByteArrayInputStream bis = new ByteArrayInputStream(photoBytes);
            Image image = new Image(bis);
            imageView.setImage(image);
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
        List<TrainerSchedule> schedule = reservationRepository.findTrainerSchedule(loginTrainer.getNum());
        ObservableList<TrainerSchedule> schedules = FXCollections.observableArrayList();

        for (TrainerSchedule trainerSchedule : schedule) {
            schedules.add(trainerSchedule);
        }

        scheduleTable.setItems(schedules);
    }

    @FXML
    private void updatePhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("이미지 파일", "*.png", "*.jpg", "*.gif", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            updatedImagePath = selectedFile.getAbsolutePath();
            Image image = new Image(selectedFile.toURI().toString());
            imageView.setImage(image);
        }
    }

    @FXML
    private void resetPhoto() {
        String gender = getSelectedGender(maleButton, femaleButton);
        if (gender.equals("M")) {
            String defaultImagePath = getClass().getResource("/image/maleTrainer.png").toExternalForm();
            imageView.setImage(new Image(defaultImagePath));

            updatedImagePath = getClass().getResource("/image/maleTrainer.png").getFile();
        } else {
            String defaultImagePath = getClass().getResource("/image/femaleTrainer.png").toExternalForm();
            imageView.setImage(new Image(defaultImagePath));

            updatedImagePath = getClass().getResource("/image/femaleTrainer.png").getFile();
        }
    }

    @FXML
    private void resetPassword() {
        Optional<ButtonType> result = showDialogChooseMessage("reallyResetPassword");

        if (result.get() == ButtonType.OK) {
            String resetPassword = loginTrainer.getId() + getRandomPassword();
            String hashPw = BCrypt.hashpw(resetPassword, BCrypt.gensalt());

            smsService.sendTrainerInitPassword(loginTrainer.getPhone(), resetPassword);
            trainerRepository.resetPassword(hashPw, loginTrainer.getNum());

            showDialogBasicMessage("resetPassword");
        }
    }
}