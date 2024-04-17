package controller.admin;

import domain.*;
import domain.member.EntryLog;
import domain.member.Member;
import domain.member.MemberSchedule;
import domain.trainer.Trainer;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import repository.*;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import static domain.Item.*;
import static domain.member.SelectedMember.*;
import static util.DialogUtil.*;
import static util.ControllerUtil.*;
import static util.MemberUtil.*;
import static util.PageUtil.movePage;
import static util.PageUtil.movePageTimerOff;

public class MemberDetailController implements Initializable {
    private final EntryLogRepository entryLogRepository = new EntryLogRepository();
    private final MemberRepository memberRepository = new MemberRepository();
    private final TrainerRepository trainerRepository = new TrainerRepository();
    private final PurchaseRepository purchaseRepository = new PurchaseRepository();
    private final ReservationRepository reservationRepository = new ReservationRepository();

    @FXML
    private TextField nameField, phoneField, emailField, lockerNumField;

    @FXML
    private Spinner<Integer> gymTicketSpinner, ptTicketSpinner, clothesSpinner, lockerSpinner;

    @FXML
    private DatePicker birthPicker;

    @FXML
    private RadioButton maleButton, femaleButton;

    @FXML
    private ComboBox<String> trainerComboBox;

    @FXML
    private TableView<MemberSchedule> ptTable;

    @FXML
    private TableColumn<MemberSchedule, Boolean> selectCol;

    @FXML
    private TableColumn<MemberSchedule, String> ptCountCol, ptNumCol, ptDateCol, ptTimeCol, trainerNumCol, trainerNameCol;

    @FXML
    private final ToggleGroup genderRadio = new ToggleGroup();

    @FXML
    private void updateMember(ActionEvent event) throws IOException {
        System.out.println("updateMember");

        if (lockerNumField.getText().isEmpty()) {
            lockerNumField.setText("0");
        }

        if (lockerSpinner.getEditor().getText().trim().isEmpty()) {
            lockerSpinner.getValueFactory().setValue(0);
        }

        if (ptTicketSpinner.getEditor().getText().trim().isEmpty()) {
            ptTicketSpinner.getValueFactory().setValue(0);
        }

        // 수정 내역이 없는 경우
        if (isSameBasicInfo() && isSameAdditionalInfo()) {
            System.out.println("수정 내용 없음");
            showDialogErrorMessage("isSame");
            return;
        }

        // 수정 내역이 있는 경우
        Optional<ButtonType> response = showDialogChoose("회원 정보를 수정하시겠습니까?");
        if (response.get() == ButtonType.OK) {
            System.out.println("수정 내역이 있음");

            Integer memberNum = currentMember.getNum();
            int currentGymTicket = getRemain(memberNum, GYM_TICKET);
            int currentPtTicket = getRemain(memberNum, PT_TICKET);
            int currentClothedPeriod = getRemain(memberNum, CLOTHES);
            int currentLockerPeriod = getRemain(memberNum, LOCKER);

            // 기본 정보 업데이트
            if (!isSameBasicInfo()) {
                System.out.println("기본 정보 업데이트");
                currentMember.setName(nameField.getText().trim());
                currentMember.setGender(Gender.valueOf(getSelectedGender(maleButton, femaleButton)));
                currentMember.setEmail(emailField.getText().trim());
                currentMember.setPhone(phoneField.getText().trim());
                currentMember.setBirthDate(Date.valueOf(birthPicker.getValue()));

                memberRepository.updateMember(currentMember);
            }

            // 헬스장 이용권 업데이트 (updateGymTicket)
            if (!isSameGymTicket()) {
                System.out.println("헬스장 이용권 업데이트");

                int inputGymTicket = Integer.parseInt(gymTicketSpinner.getValue().toString().trim());

                if (isFirstPurchase(memberNum, GYM_TICKET)) {
                    purchaseRepository.setFirstGymTicket(memberNum, inputGymTicket);
                } else {
                    int result = Math.abs(currentGymTicket - inputGymTicket);

                    if (currentGymTicket > inputGymTicket) {
                        setRemain(memberNum, GYM_TICKET, -result);
                    } else if (inputGymTicket > currentGymTicket) {
                        setRemain(memberNum, GYM_TICKET, result);
                    }
                }
            }

            // PT 이용권 또는 트레이너 업데이트
            if (!isSamePtTicket() || !isSameTrainer()) {
                System.out.println("PT 이용권 또는 트레이너 변경");

                int inputPtTicket = Integer.parseInt(ptTicketSpinner.getValue().toString().trim());
                String inputTrainerName = trainerComboBox.getValue().trim();

                // PT 이용권 변경, 트레이너 동일
                if (!isSamePtTicket() && isSameTrainer()) {
                    System.out.println("PT 이용권 변경");

                    if (inputTrainerName.isEmpty()) {
                        showDialogErrorMessage("emptyTrainer");
                        return;
                    }

                    if (inputPtTicket == 0) {
                        showDialogErrorMessage("notDeleteTrainer");
                        return;
                    }

                    int result = Math.abs(currentPtTicket - inputPtTicket);

                    if (currentPtTicket > inputPtTicket) {
                        setRemain(memberNum, PT_TICKET, -result);
                    } else if (inputPtTicket > currentPtTicket) {
                        setRemain(memberNum, PT_TICKET, result);
                    }
                }

                // 트레이너 변경, PT 이용권 동일
                if (!isSameTrainer() && isSamePtTicket()) {
                    System.out.println("트레이너 변경");

                    if (inputTrainerName.isEmpty()) {
                        showDialogErrorMessage("emptyTrainer");
                        return;
                    }

                    if (trainerRepository.findByName(inputTrainerName) == null) {
                        showDialogErrorMessage("wrongNameTrainer");
                        return;
                    }

                    if (inputPtTicket == 0) {
                        showDialogErrorMessage("emptyPtTicket");
                        return;
                    }

                    Integer trainerNum = trainerRepository.findByName(inputTrainerName).getNum();
                    changeTrainerOfMember(memberNum, trainerNum);
                }

                // PT 이용권, 트레이너 모두 변경
                if (!isSamePtTicket() && !isSameTrainer()) {
                    System.out.println("PT 이용권, 트레이너 모두 변경");

                    if (inputPtTicket == 0 && inputTrainerName.isEmpty()) {
                        purchaseRepository.deletePtTicketAndTrainer(memberNum);
                    }

                    if (trainerRepository.findByName(inputTrainerName) == null && !inputTrainerName.isEmpty()) {
                        showDialogErrorMessage("wrongNameTrainer");
                        return;
                    }

                    if (!inputTrainerName.isEmpty()) {
                        Integer trainerNum = trainerRepository.findByName(inputTrainerName).getNum();

                        if (isFirstPurchase(memberNum, PT_TICKET)) {
                            purchaseRepository.setFirstPtTicket(memberNum, trainerNum, inputPtTicket);
                        } else {
                            int result = Math.abs(currentPtTicket - inputPtTicket);

                            if (currentPtTicket > inputPtTicket) {
                                setRemain(memberNum, PT_TICKET, -result);
                                changeTrainerOfMember(memberNum, trainerNum);
                            } else if (inputPtTicket > currentPtTicket) {
                                setRemain(memberNum, PT_TICKET, result);
                                changeTrainerOfMember(memberNum, trainerNum);
                            }
                        }
                    }
                }
            }

            // 운동복 기간 업데이트
            if (!isSameClothesPeriod()) {
                System.out.println("운동복 기간 변경");
                int inputClothesPeriod = Integer.parseInt(clothesSpinner.getValue().toString().trim());

                if (isFirstPurchase(memberNum, CLOTHES)) {
                    purchaseRepository.setFirstClothes(memberNum, inputClothesPeriod);
                } else {
                    int result = Math.abs(currentClothedPeriod - inputClothesPeriod);

                    if (currentClothedPeriod > inputClothesPeriod) {
                        setRemain(memberNum, CLOTHES, -result);
                    } else if (inputClothesPeriod > currentClothedPeriod) {
                        setRemain(memberNum, CLOTHES, result);
                    }
                }
            }

            // 사물함 번호 또는 사물함 기간 업데이트
            if (!isSameLockerNum() || !isSameLockerPeriod()) {
                System.out.println("사물함 번호 또는 사물함 기간 변경");
                int inputLockerNum = Integer.parseInt(lockerNumField.getText());
                int inputLockerPeriod = Integer.parseInt(lockerSpinner.getValue().toString().trim());

                // 사물함 번호 변경, 사물함 기간 동일
                if (!isSameLockerNum() && isSameLockerPeriod()) {
                    System.out.println("사물함 번호 변경");

                    if (inputLockerPeriod == 0) {
                        showDialogErrorMessage("emptyLockerPeriod");
                        return;
                    }

                    if (inputLockerNum == 0) {
                        showDialogErrorMessage("emptyLockerNum");
                        movePage(event, "/view/admin/memberDetail");
                        return;
                    }

                    if (purchaseRepository.isUsingLockerNum(inputLockerNum)) {
                        showDialogErrorMessage("isUsingLockerNum");
                        return;
                    }

                    if (inputLockerNum > 200) {
                        showDialogErrorMessage("maxLockerNum");
                        return;
                    }
                    setLockerNum(memberNum, inputLockerNum);
                }

                // 사물함 기간 변경, 사물함 번호 동일
                if (!isSameLockerPeriod() && isSameLockerNum()) {
                    System.out.println("사물함 기간 변경");

                    if (inputLockerNum == 0) {
                        showDialogErrorMessage("emptyLockerNum");
                        return;
                    }

                    if (inputLockerPeriod == 0) {
                        showDialogErrorMessage("emptyLockerPeriod");
                        movePage(event, "/view/admin/memberDetail");
                        return;
                    }

                    int result = Math.abs(currentLockerPeriod - inputLockerPeriod);

                    if (currentLockerPeriod > inputLockerPeriod) {
                        setRemain(memberNum, LOCKER, -result);
                    } else if (inputLockerPeriod > currentLockerPeriod) {
                        setRemain(memberNum, LOCKER, result);
                    }
                }

                // 사물함 번호, 사물함 기간 모두 변경
                if (!isSameLockerPeriod() && !isSameLockerNum()) {
                    System.out.println("사물함 번호, 사물함 기간 모두 변경");

                    if (purchaseRepository.isUsingLockerNum(inputLockerNum)) {
                        showDialogErrorMessage("isUsingLockerNum");
                        return;
                    }

                    if (inputLockerNum > 200) {
                        showDialogErrorMessage("maxLockerNum");
                        return;
                    }

                    if (inputLockerNum == 0 && inputLockerPeriod == 0) {
                        purchaseRepository.deleteLocker(memberNum);
                    } else {
                        if (isFirstPurchase(memberNum, LOCKER)) {
                            purchaseRepository.setFirstLocker(memberNum, inputLockerNum, inputLockerPeriod);
                        } else {
                            setLockerNum(memberNum, inputLockerNum);
                            int result = Math.abs(currentLockerPeriod - inputLockerPeriod);

                            if (currentLockerPeriod > inputLockerPeriod) {
                                setRemain(memberNum, LOCKER, -result);
                            } else if (inputLockerPeriod > currentLockerPeriod) {
                                setRemain(memberNum, LOCKER, result);
                            }
                        }
                    }
                }
                showDialogAndMovePageTimerOff("회원이 수정되었습니다.", "/view/admin/memberDetail", event);
            }
        }
    }

    private boolean isSameBasicInfo() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        Gender gender = Gender.valueOf(getSelectedGender(maleButton, femaleButton));
        String email = emailField.getText().trim();
        Date birth = Date.valueOf(birthPicker.getValue());

        return currentMember.getName().equals(name) && currentMember.getPhone().equals(phone) && currentMember.getGender().equals(gender) && currentMember.getEmail().equals(email) && currentMember.getBirthDate().equals(birth);
    }

    private boolean isSameAdditionalInfo() {
        return isSameGymTicket() && isSamePtTicket() && isSameTrainer() && isSameClothesPeriod() && isSameLockerNum() && isSameLockerPeriod();
    }

    private boolean isSameGymTicket() {
        Integer inputGymTicket = Integer.valueOf(gymTicketSpinner.getValue().toString());
        Integer currentGymTicket = getRemain(currentMember.getNum(), GYM_TICKET);
        return inputGymTicket.equals(currentGymTicket);
    }

    private boolean isSamePtTicket() {
        Integer inputPtTicket = Integer.valueOf(ptTicketSpinner.getValue().toString());
        Integer currentPtTicket = getRemain(currentMember.getNum(), PT_TICKET);
        return inputPtTicket.equals(currentPtTicket);
    }

    private boolean isSameTrainer() {
        Integer currentTrainerNum = getTrainerNumForMember(currentMember.getNum());

        if (trainerRepository.findByNum(currentTrainerNum) != null && trainerComboBox.getValue().isEmpty()) {
            return false;
        }

        if (trainerComboBox.getValue().isEmpty()) {
            return true;
        }

        if (trainerRepository.findByNum(currentTrainerNum) == null && !trainerComboBox.getValue().isEmpty()) {
            return false;
        }

        String currentTrainerName = trainerRepository.findByNum(currentTrainerNum).getName();
        String inputTrainerName = trainerComboBox.getValue();

        if (inputTrainerName != null && !inputTrainerName.isEmpty()) {
            return inputTrainerName.equals(currentTrainerName);
        }
        return false;
    }

    private boolean isSameClothesPeriod() {
        Integer inputClothesPeriod = Integer.valueOf(clothesSpinner.getValue().toString());
        Integer currentClothesPeriod = getRemain(currentMember.getNum(), CLOTHES);
        return inputClothesPeriod.equals(currentClothesPeriod);
    }

    private boolean isSameLockerNum() {
        Integer inputLockerNum = Integer.valueOf(lockerNumField.getText());
        Integer currentLockerNum = getLockerNum(currentMember.getNum());
        return inputLockerNum.equals(currentLockerNum);
    }

    private boolean isSameLockerPeriod() {
        Integer currentLockerPeriod = getRemain(currentMember.getNum(), LOCKER);
        Integer inputLockerPeriod = Integer.valueOf(lockerSpinner.getValue().toString());
        return inputLockerPeriod.equals(currentLockerPeriod);
    }



    @FXML
    private void deleteMember(ActionEvent event) throws IOException {
        Optional<ButtonType> response = showDialogChoose("정말로 " + currentMember.getName() + " 회원을 삭제하시겠습니까?");

        if (response.get() == ButtonType.OK){
            memberRepository.deleteMember(currentMember.getNum());
            showDialogAndMovePageTimerOff("회원이 삭제되었습니다.", "/view/admin/helloAdminV2", event);
        }
    }

    @FXML
    private void showEntryLog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(currentMember.getName() + "님 출입 일지");

        ButtonType closeButtonType = new ButtonType("닫기", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(closeButtonType);
        Button closeButton = (Button) dialog.getDialogPane().lookupButton(closeButtonType);
        closeButton.getStyleClass().add("closeBtn");

        TableView<EntryLog> table = new TableView<>();
        table.getStyleClass().add("tableView");
        loadEntryLog(currentMember.getNum(), table, entryLogRepository);

        VBox vbox = new VBox(table);
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setContent(vbox);
        dialogPane.getStylesheets().add(getClass().getResource("/css/Entrylog.css").toExternalForm());

        // Dialog의 Stage에 접근하여 아이콘 설정 (승빈)
        Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new Image(getClass().getResourceAsStream("/image/JavaGym_Logo.jpeg")));

        dialog.showAndWait();
    }

    @FXML
    private void cancelReservation(ActionEvent event) throws IOException {

        if (ptTable.getItems().isEmpty()) {
            showDialog("PT 예약 정보가 없습니다.");
            return;
        }

        // 선택한 예약 내역을 가져온다.
        List<MemberSchedule> selectedSchedules = ptTable.getItems().stream()
                .filter(MemberSchedule::isSelected)
                .toList();

        // 선택된 예약 정보가 있는지 확인
        if (selectedSchedules.isEmpty()) {
            showDialog("취소할 예약을 선택해주세요.");
            return;
        }

        Optional<ButtonType> result = showDialogChoose("정말로 " + currentMember.getName() + " 회원의 PT 예약 정보를 삭제하시겠습니까?");

        if (result.get() == ButtonType.OK){
            int count = 0;
            // 선택한 예약 내역을 모두 삭제
            for (MemberSchedule schedule : selectedSchedules) {
                reservationRepository.deleteReservation(schedule.getReservationNum());
                count++;
            }
            // 삭제한 예약 내역만큼 PT 이용권 돌려주기
            setRemain(currentMember.getNum(), PT_TICKET, count);
            showDialogAndMovePageTimerOff("예약 정보가 삭제되었습니다.", "/view/admin/memberDetail", event);
        }
    }

    @FXML
    private void goBack(ActionEvent event) throws IOException {
        AdminTab.getInstance().setSelectedTabIndex(0);
        movePageTimerOff(event, "/view/admin/helloAdminV2");
    }

    /**
     * 관리자가 선택한 회원에 대한 정보를 가져온다.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (currentMember != null) {
            Member member = currentMember;
            Integer memberNum = member.getNum();

            // 회원의 일반 정보와 부가 정보 설정
            setBasicInfo(member, birthPicker);
            setAdditionalInfo(memberNum);

            // PT 예약 정보 설정
            columnBinding();
            loadMemberSchedule();

            TextFormatter<String> phoneFormatter = new TextFormatter<>(change -> {
                String newText = change.getControlNewText();
                if (newText.matches("\\d{0,8}")) {
                    return change;
                }
                return null;
            });

            TextFormatter<String> lockerFormatter = new TextFormatter<>(change -> {
                String newText = change.getControlNewText();
                if (newText.matches("\\d{0,3}")) {
                    return change;
                }
                return null;
            });

            phoneField.setTextFormatter(phoneFormatter);
            lockerNumField.setTextFormatter(lockerFormatter);

            if (getLockerNum(currentMember.getNum()).equals(0)) {
                lockerNumField.setText("0");
            }

            gymTicketSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
                if (newValue == null) {
                    gymTicketSpinner.getValueFactory().setValue(0);
                }
            });

            ptTicketSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
                if (newValue == null) {
                    ptTicketSpinner.getValueFactory().setValue(0);
                }
            });

            lockerSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
                if (newValue == null) {
                    lockerSpinner.getValueFactory().setValue(0);
                }
            });

            clothesSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
                if (newValue == null) {
                    clothesSpinner.getValueFactory().setValue(0);
                }
            });
        }
    }

    private void setBasicInfo(Member member, DatePicker birthPicker) {
        // DatePicker를 사용하기 위해 SQL Date를 Local Date로 변환한다.
        LocalDate birthDate = member.getBirthDate().toLocalDate();

        nameField.setText(member.getName());
        phoneField.setText(member.getPhone());
        emailField.setText(member.getEmail());
        birthPicker.setValue(birthDate);

        maleButton.setToggleGroup(genderRadio);
        femaleButton.setToggleGroup(genderRadio);

        if (member.getGender().equals(Gender.M)) {
            genderRadio.selectToggle(maleButton);
        } else {
            genderRadio.selectToggle(femaleButton);
        }
    }

    private void setAdditionalInfo(Integer memberNum) {
        List<Integer> remains = getRemainAll(memberNum);
        Spinner[] spinners = {gymTicketSpinner, ptTicketSpinner, clothesSpinner, lockerSpinner};

        for (int i = 0; i < 4; i++) {
            Integer remain = remains.get(i);
            if (remain != 0) {
                spinners[i].getValueFactory().setValue(remain);
            }
        }

        String lockerNum = getLockerNum(memberNum).toString();
        if (!lockerNum.equals("0")) {
            lockerNumField.setText(lockerNum);
        }

        List<Trainer> trainers = trainerRepository.findAllTrainer();
        ObservableList<String> trainerNames = FXCollections.observableArrayList();

        trainers.forEach(trainer -> trainerNames.add(trainer.getName()));
        trainerComboBox.setItems(trainerNames);

        // 회원의 현재 트레이너를 콤보 박스의 기본값으로 설정
        Integer trainerNum = getTrainerNumForMember(currentMember.getNum());
        if (trainerNum != 0) {
            trainerComboBox.getSelectionModel().select(trainerRepository.findByNum(trainerNum).getName());
        } else {
            trainerComboBox.setValue("");
        }
    }

    private void columnBinding() {
        selectCol.setCellFactory(CheckBoxTableCell.forTableColumn(selectCol));
        selectCol.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        ptCountCol.setCellValueFactory(new PropertyValueFactory<>("sequence"));
        ptNumCol.setCellValueFactory(new PropertyValueFactory<>("reservationNum"));
        ptDateCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getReservationDate().toString()));
        ptTimeCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getReservationTime())));
        trainerNumCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getTrainerNum())));
        trainerNameCol.setCellValueFactory(new PropertyValueFactory<>("trainerName"));
    }

    private void loadMemberSchedule() {
        List<MemberSchedule> memberSchedule = reservationRepository.findMemberSchedule(currentMember.getNum());
        ObservableList<MemberSchedule> schedules = FXCollections.observableArrayList();
        schedules.addAll(memberSchedule);

        ptTable.setItems(schedules);
    }
}