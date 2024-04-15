package controller.admin;

import domain.Gender;
import domain.member.Member;
import domain.member.SelectedMember;
import domain.member.UsingLocker;
import domain.trainer.Trainer;
import domain.trainer.WorkingHour;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;
import repository.AdminRepository;
import repository.MemberRepository;
import repository.PurchaseRepository;
import repository.TrainerRepository;
import service.AdminService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

import static converter.StringToDateConverter.stringToDate;
import static domain.trainer.SelectedTrainer.currentTrainer;
import static util.AlertUtil.*;
import static util.AlertUtil.showAlert;
import static util.ControllerUtil.*;
import static util.ControllerUtil.loadLockerInfo;
import static util.PageUtil.movePageCenter;
import static util.ValidateUtil.*;

public class HelloAdminControllerV2 implements Initializable {

    private final ResourceBundle config = ResourceBundle.getBundle("config.init");
    private final AdminRepository adminRepository = new AdminRepository();
    private final AdminService service = new AdminService(adminRepository);
    private final MemberRepository memberRepository = new MemberRepository();
    private final PurchaseRepository purchaseRepository = new PurchaseRepository();

    @FXML
    private TabPane tabPane;

    // 회원 영역
    @FXML
    private TextField memberNameField, memberBirthField, memberPhoneField, emailField, searchMemberNameField;

    @FXML
    private RadioButton memberMaleButton, memberFemaleButton;

    @FXML
    private void addMember(ActionEvent event) throws ParseException, IOException {
        if (isEmptyAnyField(memberNameField, emailField, memberBirthField, memberPhoneField, memberMaleButton, memberFemaleButton)) {
            showAlertAddMemberFail("emptyAnyField");
            return;
        }

        String name = memberNameField.getText().trim();
        String phone = memberPhoneField.getText().trim();
        String email = emailField.getText().trim();
        String birth = memberBirthField.getText().trim();

        if (addMemberValidate(name, phone, email, birth)) return;

        Member member = new Member();
        member.setName(name);
        member.setPassword(BCrypt.hashpw(config.getString("initial.member.password"), BCrypt.gensalt()));
        member.setGender(Gender.valueOf(getSelectedGender(memberMaleButton, memberFemaleButton)));
        member.setEmail(email);
        member.setBirthDate(stringToDate(birth));
        member.setPhone(phone);

        service.addMember(member);
        showAlertAndMoveCenter("회원 등록 성공", Alert.AlertType.INFORMATION, "/view/admin/helloAdmin2", event);
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
    private TableColumn<Member, String> memberNumCol, memberNameCol, memberGenderCol, emailCol, memberBirthCol, memberPhoneCol, enrollCol;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        columnBindingMember(memberNumCol, memberNameCol, memberGenderCol, emailCol, memberBirthCol, memberPhoneCol, enrollCol);
        loadMemberData(membersTable, memberRepository);

        TextFormatter<String> memberBirthFormatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d{0,6}")) {
                return change;
            }
            return null;
        });

        TextFormatter<String> memberPhoneFormatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d{0,8}")) {
                return change;
            }
            return null;
        });

        memberBirthField.setTextFormatter(memberBirthFormatter);
        memberPhoneField.setTextFormatter(memberPhoneFormatter);

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

        columnBindingTrainer(trainerNumCol, trainerNameCol, idCol, trainerGenderCol, workTimeCol, trainerBirthCol, trainerPhoneCol);
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


        TextFormatter<String> trainerBirthFormatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d{0,6}")) {
                return change;
            }
            return null;
        });

        TextFormatter<String> trainerPhoneFormatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d{0,8}")) {
                return change;
            }
            return null;
        });

        UnaryOperator<TextFormatter.Change> trainerFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("^\\d*\\.?\\d*$")) {
                return change;
            }
            return null;
        };

        TextFormatter<String> heightFormatter = new TextFormatter<>(trainerFilter);
        TextFormatter<String> weightFormatter = new TextFormatter<>(trainerFilter);

        heightField.setTextFormatter(heightFormatter);
        weightField.setTextFormatter(weightFormatter);
        trainerBirthField.setTextFormatter(trainerBirthFormatter);
        trainerPhoneField.setTextFormatter(trainerPhoneFormatter);

        int tabIndex = AdminTab.getInstance().getSelectedTabIndex();
        tabPane.getSelectionModel().select(tabIndex);
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

        // Dialog의 Stage에 접근하여 아이콘 설정 (승빈)
        Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new Image(getClass().getResourceAsStream("/image/JavaGym_Logo.jpeg")));

        dialog.showAndWait();
    }

    @FXML
    private void searchMember() {
        String searchName = searchMemberNameField.getText().trim();

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
    private void resetPageMember(ActionEvent event) throws IOException {
        AdminTab.getInstance().setSelectedTabIndex(0);
        movePageCenter(event, "/view/admin/helloAdminV2");
    }

    @FXML
    private void logout(ActionEvent event) throws IOException {
        movePageCenter(event, "/view/member/memberLogin");
    }

    // 트레이너 영역

    private final TrainerRepository trainerRepository = new TrainerRepository();

    @FXML
    private TextField trainerNameField, idField, trainerPhoneField, trainerBirthField, heightField, weightField, searchTrainerNameField;

    @FXML
    private ImageView imageView;
    private String selectedImagePath;

    @FXML
    private RadioButton trainerMaleButton, trainerFemaleButton, amButton, pmButton;

    @FXML
    private TableView<Trainer> trainerTable;

    @FXML
    private TableColumn<Trainer, String> trainerNumCol, trainerNameCol, idCol, trainerGenderCol, workTimeCol, trainerBirthCol, trainerPhoneCol;

    @FXML
    private void addTrainer(ActionEvent event) throws IOException, ParseException {
        if (isEmptyAnyField(trainerNameField, idField, trainerBirthField, trainerPhoneField, trainerMaleButton, trainerFemaleButton, amButton, pmButton, heightField, weightField)) {
            showAlertAddTrainerFail("emptyAnyField");
            return;
        }

        String name = trainerNameField.getText().trim();
        String phone = trainerPhoneField.getText().trim();
        Gender gender = Gender.valueOf(getSelectedGender(trainerMaleButton, trainerFemaleButton));
        String birth = trainerBirthField.getText().trim();
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

        showAlertAndMoveCenter("트레이너 등록 성공", Alert.AlertType.INFORMATION, "/view/admin/helloAdmin2", event);
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
        String searchName = searchTrainerNameField.getText().trim();

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
    private void resetPageTrainer(ActionEvent event) throws IOException {
        AdminTab.getInstance().setSelectedTabIndex(1);
        movePageCenter(event, "/view/admin/helloAdminV2");
    }
}