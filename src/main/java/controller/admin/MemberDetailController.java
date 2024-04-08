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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import repository.*;
import util.MemberUtil;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static domain.member.SelectedMember.*;
import static util.AlertUtil.*;
import static util.ControllerUtil.*;
import static util.PageUtil.movePageCenter;
import static util.MemberUtil.*;

public class MemberDetailController implements Initializable {

    private final EntryLogRepository entryLogRepository = new EntryLogRepository();
    private final MemberRepository memberRepository = new MemberRepository();
    private final TrainerRepository trainerRepository = new TrainerRepository();
    private final PurchaseRepository purchaseRepository = new PurchaseRepository();

    @FXML
    private TextField nameField, phoneField, emailField, lockerNumField;

    @FXML
    private Spinner gymTicketSpinner, PTTicketSpinner, clothesSpinner, lockerSpinner;

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
    private final ReservationRepository reservationRepository = new ReservationRepository();

    private final ToggleGroup genderRadio = new ToggleGroup();

    // 관리자가 선택한 회원의 정보를 불러온다.
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
        Spinner[] spinners = {gymTicketSpinner, PTTicketSpinner, clothesSpinner, lockerSpinner};

        for (int i = 0; i < 4; i++) {
            Integer remain = remains.get(i);
//            Spinner<Integer> spinner = spinners[i];
            if (remain != 0) {
                spinners[i].getValueFactory().setValue(remain);
            }
//            SpinnerValueFactory<Integer> valueFactory =
//                    new SpinnerValueFactory.IntegerSpinnerValueFactory(0, remain, remain); // 최소값, 최대값, 기본값
//            spinner.setValueFactory(valueFactory);
        }

        // promptText를 사용하기 위해 값이 유효할 때만 값을 설정한다.
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
        for (MemberSchedule schedule : memberSchedule) {
            schedules.add(schedule);
        }

        ptTable.setItems(schedules);
    }

    @FXML
    private void updateMember(ActionEvent event) throws IOException {

        // 검증 로직 추후 구현

        // 정상 로직
        currentMember.setName(nameField.getText().trim());
        currentMember.setGender(Gender.valueOf(getSelectedGender(maleButton, femaleButton)));
        currentMember.setEmail(emailField.getText().trim());
        currentMember.setPhone(phoneField.getText().trim());
        currentMember.setBirthDate(Date.valueOf(birthPicker.getValue()));

        int inputGymTicket = Integer.parseInt(gymTicketSpinner.getValue().toString());
        int ptTicket = Integer.parseInt(PTTicketSpinner.getValue().toString());
        int clothesPeriod = Integer.parseInt(clothesSpinner.getValue().toString());
        int lockerPeriod = Integer.parseInt(lockerSpinner.getValue().toString());

        if (isFirstPurchase(currentMember.getNum(), Item.GYM_TICKET)) {
            purchaseRepository.setFirstGymTicket(currentMember.getNum(), inputGymTicket);
        } else {
            Integer currentRemain = getRemain(currentMember.getNum(), Item.GYM_TICKET);
            int ticket = currentRemain - inputGymTicket;
            if (currentRemain > ticket) {
                setRemain(currentMember.getNum(), Item.GYM_TICKET, ticket);
            } else if (ticket > currentRemain) {

            }
        }

        Optional<ButtonType> result = showAlertChoose("회원 정보를 수정하시겠습니까?");

        if (result.get() == ButtonType.OK){
            memberRepository.updateMember(currentMember);
            showAlertAndMove("알림", "회원이 수정되었습니다.", Alert.AlertType.INFORMATION, "/view/admin/memberInfo", event);
        }
    }

    @FXML
    private void deleteMember(ActionEvent event) throws IOException {
        Optional<ButtonType> result = showAlertChoose("정말로 " + currentMember.getName() + " 회원을 삭제하시겠습니까?");

        if (result.get() == ButtonType.OK){
            memberRepository.deleteMember(currentMember.getNum());
            showAlertAndMove("알림", "회원이 삭제되었습니다.", Alert.AlertType.INFORMATION, "/view/admin/memberInfo", event);
        }
    }

    @FXML
    private void showEntryLog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(currentMember.getName() + "님 출입 일지");

        ButtonType closeButtonType = new ButtonType("닫기");
        dialog.getDialogPane().getButtonTypes().addAll(closeButtonType);

        TableView<EntryLog> table = new TableView<>();
        loadEntryLog(currentMember.getNum(), table, entryLogRepository);

        VBox vbox = new VBox(table);
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setContent(vbox);

        dialog.showAndWait();
    }

    @FXML
    private void cancelReservation(ActionEvent event) throws IOException {

        // 선택한 예약 내역을 가져온다.
        List<MemberSchedule> selectedSchedules = ptTable.getItems().stream()
                .filter(MemberSchedule::isSelected)
                .collect(Collectors.toList());

        // 선택된 예약 정보가 있는지 확인
        if (selectedSchedules.isEmpty()) {
            showAlert("선택된 예약 없음", "취소할 예약을 선택해주세요.", Alert.AlertType.WARNING);
            return;
        }

        Optional<ButtonType> result = showAlertChoose("정말로 " + currentMember.getName() + " 회원의 PT 예약 정보를 삭제하시겠습니까?");

        if (result.get() == ButtonType.OK){

            // 선택한 예약 내역을 모두 삭제
            for (MemberSchedule schedule : selectedSchedules) {
                reservationRepository.delete(schedule.getReservationNum());
            }

            showAlertAndMove("알림", "예약 정보가 삭제되었습니다.", Alert.AlertType.INFORMATION, "/view/admin/memberDetail", event);
        }
    }

    @FXML
    private void goBack(ActionEvent event) throws IOException {
        movePageCenter(event, "/view/admin/memberInfo");
    }
}