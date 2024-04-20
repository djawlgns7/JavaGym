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
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;
import repository.AdminRepository;
import repository.MemberRepository;
import repository.PurchaseRepository;
import repository.TrainerRepository;
import service.AdminService;
import service.SmsService;
import util.DialogUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static converter.StringToDateConverter.stringToDate;
import static domain.admin.SelectedAdmin.loginAdmin;
import static domain.trainer.SelectedTrainer.loginTrainer;
import static service.SmsService.getRandomPassword;
import static util.DialogUtil.*;
import static util.ControllerUtil.*;
import static util.ControllerUtil.loadLockerInfo;
import static util.PageUtil.*;
import static util.ValidateUtil.*;

public class HelloAdminControllerV2 implements Initializable {

    private final AdminRepository adminRepository = new AdminRepository();
    private final AdminService service = new AdminService(adminRepository);
    private final MemberRepository memberRepository = new MemberRepository();
    private final PurchaseRepository purchaseRepository = new PurchaseRepository();

    private final SmsService smsService = new SmsService();

    @FXML
    private TabPane tabPane;

    // 회원 영역
    @FXML
    private TextField memberNameField, memberBirthField, memberPhoneField, emailField, searchMemberNameField;

    @FXML
    private RadioButton memberMaleButton, memberFemaleButton;

    @FXML
    private HBox photoHBox;

    @FXML
    private Button addPhotoButton;

    @FXML
    private void addMember(ActionEvent event) throws ParseException, IOException {
        if (isEmptyAnyField(memberNameField, emailField, memberBirthField, memberPhoneField, memberMaleButton, memberFemaleButton)) {
            showDialogErrorMessage("emptyAnyField");
            return;
        }

        String name = memberNameField.getText().trim();
        String phone = memberPhoneField.getText().trim();
        String email = emailField.getText().trim();
        String birth = memberBirthField.getText().trim();

        if (addMemberValidate(name, phone, email, birth)) return;

        Member member = new Member();
        member.setName(name);

        int initPassword = getRandomPassword();
        member.setPassword(BCrypt.hashpw(String.valueOf(initPassword), BCrypt.gensalt()));
        smsService.sendMemberInitPassword(phone, initPassword);

        member.setGender(Gender.valueOf(getSelectedGender(memberMaleButton, memberFemaleButton)));
        member.setEmail(email);
        member.setBirthDate(stringToDate(birth));
        member.setPhone(phone);

        service.addMember(member);
        AdminTab.getInstance().setSelectedTabIndex(0);
        showDialogAndMovePageMessage("addMember", "/view/admin/helloAdminV2", event);
    }

    @FXML
    private void memberDetail(Member member, MouseEvent event) throws IOException {
        if (member != null && event.getClickCount() == 2) {
            SelectedMember.loginMember = member;
            movePage(event, "/view/admin/memberDetail");
        }
    }

    @FXML
    private TableView<Member> membersTable;

    @FXML
    private TableColumn<Member, Boolean> selectMemberCol;

    @FXML
    private TableColumn<Member, String> memberNumCol, memberNameCol, memberGenderCol, emailCol, memberBirthCol, memberPhoneCol, enrollCol;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        columnBindingMember(selectMemberCol, memberNumCol, memberNameCol, memberGenderCol, emailCol, memberBirthCol, memberPhoneCol, enrollCol);
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

        columnBindingTrainer(selectTrainerCol, trainerNumCol, trainerNameCol, idCol, trainerGenderCol, workTimeCol, trainerBirthCol, trainerPhoneCol);
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

        // 페이지나 탭이 로드되면 실행되는 초기화 메소드 내에서 선택된 탭의 스타일을 적용
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        if (selectedTab != null) {
            selectedTab.setStyle("-fx-background-color: #9747FF;"); // 선택된 탭의 색상
        }

        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            // 선택되지 않은 탭의 스타일을 초기화
            if (oldTab != null) {
                oldTab.setStyle(""); // 혹은 선택되지 않은 탭의 기본 스타일을 설정
            }
            // 새로 선택된 탭에 대한 스타일 적용
            if (newTab != null) {
                newTab.setStyle("-fx-background-color: #9747FF;"); // 선택된 탭의 색상
            }
        });
    } // initialize 끝

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
            showDialogErrorMessage("emptyName");
            return;
        }

        List<Member> searchedMembers = memberRepository.searchMembersByName(searchName);

        if (searchedMembers.isEmpty()) {
            showDialogErrorMessage("noFindByNameMember");
            return;
        }
        ObservableList<Member> observableList = FXCollections.observableArrayList(searchedMembers);
        membersTable.setItems(observableList);
    }

    @FXML
    private void resetPageMember(ActionEvent event) throws IOException {
        AdminTab.getInstance().setSelectedTabIndex(0);
        movePageTimerOff(event, "/view/admin/helloAdminV2");
    }

    @FXML
    private void logout(ActionEvent event) throws IOException {
        AdminTab.getInstance().setSelectedTabIndex(0); // 추가
        loginAdmin = null;
        moveToMainPage(event);
    }

    @FXML
    private void deleteMembers(ActionEvent event) throws IOException {
        boolean hasSelected = membersTable.getItems().stream().anyMatch(Member::isSelected);

        if (!hasSelected) {
            showDialogErrorMessage("noSelectMember");
            return;
        }

        List<Member> members = membersTable.getItems().stream()
                .filter(Member::isSelected)
                .toList();

        Optional<ButtonType> result = showDialogChooseMessage("reallyDeleteMember");
        if (result.get() == ButtonType.OK) {
            for (Member member : members) {
                memberRepository.deleteMember(member.getNum());
            }
            AdminTab.getInstance().setSelectedTabIndex(0);
            showDialogAndMovePageTimerOffMessage("deleteMember", "/view/admin/helloAdminV2", event);
        }

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
    private TableColumn<Trainer, Boolean> selectTrainerCol;

    @FXML
    private TableColumn<Trainer, String> trainerNumCol, trainerNameCol, idCol, trainerGenderCol, workTimeCol, trainerBirthCol, trainerPhoneCol;

    @FXML
    private void addTrainer(ActionEvent event) throws IOException, ParseException {
        if (isEmptyAnyField(trainerNameField, idField, trainerBirthField, trainerPhoneField, trainerMaleButton, trainerFemaleButton, amButton, pmButton, heightField, weightField)) {
            showDialogErrorMessage("emptyAnyField");
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

        int random = getRandomPassword();
        String initPassword = id + random;
        trainer.setPassword(BCrypt.hashpw(initPassword, BCrypt.gensalt()));
        smsService.sendTrainerInitPassword(phone, initPassword);

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

        AdminTab.getInstance().setSelectedTabIndex(1);
        showDialogAndMovePageMessage("addTrainer", "/view/admin/helloAdminV2", event);
    }

    @FXML
    private void trainerDetail(Trainer trainer, MouseEvent event) throws IOException {
        if (trainer != null && event.getClickCount() == 2) {
            loginTrainer = trainerRepository.findByNum(trainer.getNum());
            movePage(event, "/view/admin/trainerDetail");
        }
    }

    private boolean addPhoto = false;

    @FXML
    private void addPhoto() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("이미지 파일", "*.png", "*.jpg", "*.gif", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null && !addPhoto) {
            selectedImagePath = selectedFile.getAbsolutePath();
            Image image = new Image(selectedFile.toURI().toString());
            imageView.setImage(image);

            addPhotoButton.setText("사진 변경");

            Button deletePhotoButton = new Button("사진 삭제");
            photoHBox.getChildren().add(deletePhotoButton);
            deletePhotoButton.setOnAction(this::deletePhoto);
            addPhoto = true;
        } else if (selectedFile != null) {
            selectedImagePath = selectedFile.getAbsolutePath();
            Image image = new Image(selectedFile.toURI().toString());
            imageView.setImage(image);
        }
    }

    private void deletePhoto(ActionEvent event) {
        Button button = (Button) event.getSource();
        photoHBox.getChildren().remove(button);
        addPhotoButton.setText("사진 등록");
        imageView.setImage(null);
        addPhoto = false;
    }

    @FXML
    private void searchTrainer() {
        String searchName = searchTrainerNameField.getText().trim();

        if (searchName.isEmpty()) {
            showDialogErrorMessage("emptyName");
            return;
        }

        Trainer trainer = trainerRepository.findByName(searchName);

        if (trainer == null) {
            showDialogErrorMessage("noFindByNameTrainer");
            return;
        }
        ObservableList<Trainer> observableList = FXCollections.observableArrayList(trainer);
        trainerTable.setItems(observableList);
    }

    @FXML
    private void resetPageTrainer(ActionEvent event) throws IOException {
        AdminTab.getInstance().setSelectedTabIndex(1);
        movePageTimerOff(event, "/view/admin/helloAdminV2");
    }

    @FXML
    private void deleteTrainers(ActionEvent event) throws IOException {
        boolean hasSelected = trainerTable.getItems().stream().anyMatch(Trainer::isSelected);

        if (!hasSelected) {
            showDialogErrorMessage("noSelectTrainer");
            return;
        }

        List<Trainer> trainers = trainerTable.getItems().stream()
                .filter(Trainer::isSelected)
                .toList();

        Optional<ButtonType> result = showDialogChooseMessage("reallyDeleteTrainer");
        if (result.get() == ButtonType.OK) {
            for (Trainer trainer : trainers) {
                trainerRepository.deleteTrainer(trainer.getNum());
            }
            AdminTab.getInstance().setSelectedTabIndex(1);
            showDialogAndMovePageTimerOffMessage("deleteTrainer", "/view/admin/helloAdminV2", event);
        }
    }

    @FXML
    private void showAmTrainer() {
        List<Trainer> listTrainers = trainerRepository.findAllTrainer().stream()
                .filter(trainer -> WorkingHour.AM.equals(trainer.getWorkingHour()))
                .collect(Collectors.toList());

        ObservableList<Trainer> observableTrainerData = FXCollections.observableArrayList(listTrainers);
        trainerTable.setItems(observableTrainerData);
    }

    @FXML
    private void showPmTrainer() {
        List<Trainer> listTrainers = trainerRepository.findAllTrainer().stream()
                .filter(trainer -> WorkingHour.PM.equals(trainer.getWorkingHour()))
                .collect(Collectors.toList());

        ObservableList<Trainer> observableTrainerData = FXCollections.observableArrayList(listTrainers);
        trainerTable.setItems(observableTrainerData);
    }
}