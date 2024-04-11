package controller.admin;

import domain.*;
import domain.trainer.Trainer;
import domain.trainer.WorkingHour;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import org.mindrot.jbcrypt.BCrypt;
import repository.AdminRepository;
import repository.TrainerRepository;
import domain.service.AdminService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

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
    private TextField nameField, idField, phoneField, birthField, heightField, weightField, searchNameField;

    @FXML
    private ImageView imageView;
    private String selectedImagePath;

    @FXML
    private RadioButton maleButton, femaleButton, amButton, pmButton;

    @FXML
    private TableView<Trainer> trainerTable;

    @FXML
    private TableColumn<Trainer, String> numCol, nameCol, idCol, genderCol, workTimeCol, birthCol, phoneCol;

    @FXML
    private void addTrainer(ActionEvent event) throws IOException, ParseException {
        if (isEmptyAnyField(nameField, idField, birthField, phoneField, maleButton, femaleButton, amButton, pmButton, heightField, weightField)) {
            showAlertAddTrainerFail("emptyAnyField");
            return;
        }

        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        Gender gender = Gender.valueOf(getSelectedGender(maleButton, femaleButton));
        String birth = birthField.getText().trim();
        String id = idField.getText().trim();
        Double height = Double.valueOf(heightField.getText());
        Double weight = Double.valueOf(weightField.getText());

        if (addTrainerValidate(name, phone, id, birth, height, weight)) return;

        Trainer trainer = new Trainer();
        trainer.setName(name);
        trainer.setId(id);
        trainer.setPassword(BCrypt.hashpw(config.getString("initial.trainer.password"), BCrypt.gensalt()));
        trainer.setGender(gender);
        trainer.setBirthDate(stringToDate(birth));
        trainer.setPhone(phone);
        trainer.setWorkingHour(WorkingHour.valueOf(getSelectedWorkingTime(amButton, pmButton)));
        trainer.setHeight(height);
        trainer.setWeight(weight);

        service.addTrainer(trainer);

        // 사진 선택했을 경우
        if (selectedImagePath != null && !selectedImagePath.isEmpty()) {
            File photoFile = new File(selectedImagePath);
            Trainer findTrainer = trainerRepository.findByName(trainer.getName());
            trainerRepository.savePhoto(findTrainer.getNum(), photoFile);
        } else {
            // 사진 선택하지 않았을 경우
            if (gender.equals(Gender.M)) {
                String defaultImagePath = getClass().getResource("/image/maleTrainer.png").getFile();
                File defaultImageFile = new File(defaultImagePath);

                Trainer findTrainer = trainerRepository.findByName(trainer.getName());
                trainerRepository.savePhoto(findTrainer.getNum(), defaultImageFile);
            } else {
                String defaultImagePath = getClass().getResource("/image/femaleTrainer.png").getFile();
                File defaultImageFile = new File(defaultImagePath);

                Trainer findTrainer = trainerRepository.findByName(trainer.getName());
                trainerRepository.savePhoto(findTrainer.getNum(), defaultImageFile);
            }
        }

        showAlertAndMove("트레이너 등록 성공", Alert.AlertType.INFORMATION, "/view/admin/trainerInfo", event);
    }

    @FXML
    private void goBack(ActionEvent event) throws IOException {
        movePageCenter(event, "/view/admin/helloAdmin");
    }

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
        birthField.setTextFormatter(birthFormatter);
        phoneField.setTextFormatter(phoneFormatter);
    }

    @FXML
    private void trainerDetail(Trainer trainer, MouseEvent event) throws IOException {
        if (trainer != null && event.getClickCount() == 2) {
            currentTrainer = trainerRepository.findByNum(trainer.getNum());
            movePageCenter(event, "/view/admin/trainerDetail");
        }
    }

    @FXML
    private void selectPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("이미지 파일", "*.png", "*.jpg", "*.gif", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            selectedImagePath = selectedFile.getAbsolutePath();
            Image image = new Image(selectedFile.toURI().toString());
            imageView.setImage(image);
        }
    }

    @FXML
    private void searchTrainer() {
        String searchName = searchNameField.getText().trim();

        if (searchName.isEmpty()) {
            showAlert("이름을 입력해 주세요.", Alert.AlertType.INFORMATION);
            return;
        }

        Trainer trainer = trainerRepository.findByName(searchName);

        if (trainer == null) {
            showAlert("해당 이름의 트레이너가 없습니다.", Alert.AlertType.INFORMATION);
            return;
        }
        ObservableList<Trainer> observableList = FXCollections.observableArrayList(trainer);
        trainerTable.setItems(observableList);
    }

    @FXML
    private void resetPage(ActionEvent event) throws IOException {
        movePageCenter(event, "/view/admin/trainerInfo");
    }
}