package controller.trainer;


import controller.admin.AdminTab;
import domain.member.Member;
import domain.member.MemberSchedule;
import domain.trainer.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import repository.MemberRepository;
import repository.ReservationRepository;
import repository.TrainerRepository;
import service.TrainerService;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

import static domain.Item.PT_TICKET;
import static domain.trainer.SelectedTrainer.loginTrainer;
import static domain.trainer.SelectedReservation.currentReservation;
import static util.ControllerUtil.*;
import static util.DialogUtil.*;
import static util.MemberUtil.getTrainerNumForMember;
import static util.MemberUtil.setRemain;
import static util.PageUtil.*;
import static util.ValidateUtil.*;

public class ReservationInfoController implements Initializable {

    private final TrainerRepository trainerRepository = new TrainerRepository();
    private final ReservationRepository reservationRepository = new ReservationRepository();
    private final MemberRepository memberRepository = new MemberRepository();
    private final TrainerService service = new TrainerService(trainerRepository);

    @FXML
    private TextField numField, nameField, searchMemberNameField;

    @FXML
    private ComboBox<String> rTimeComboBox;

    @FXML
    private DatePicker rDatePicker;

    @FXML
    private TableView<Reservation> reservationTable;

    @FXML
    private TableColumn<Reservation, Boolean> selectCol;

    @FXML
    private TableColumn<Reservation, String> memberNumCol, memberNameCol, memberPhoneCol, rDateCol, rTimeCol;

    @FXML
    private void addReservationInfo(ActionEvent event) throws IOException {
        if (isEmptyAnyField(numField, nameField, rDatePicker, rTimeComboBox)) {
            showDialogErrorMessage("emptyAnyField");
            return;
        }

        Reservation reservation = new Reservation();

        // 예약 추가 로직 구현
        int memberNum = Integer.parseInt(numField.getText().trim());
        String memberName = nameField.getText().trim();

        if(!checkMember(memberNum, memberName)) {
            showDialogErrorMessage("wrongMember");
            return;
        }
        Date rDate = Date.valueOf(rDatePicker.getValue());
        String rTimeInput = rTimeComboBox.getSelectionModel().getSelectedItem();
        Integer rTime = Integer.parseInt(rTimeInput.split(":")[0]);
        LocalDate localrDate = rDate.toLocalDate();
        List<MemberSchedule> memberSchedule = reservationRepository.findMemberSchedule(memberNum);
        int memberReservationNum = memberSchedule.size();

        if (memberReservationNum >= 4) {
            showDialogErrorMessage("maxReservationNum");
            return;
        }

        if(rTime < 8 || rTime > 19) {
            showDialogErrorMessage("invalidTime");
            return;
        }

        if (!isValidTimeForTrainer(loginTrainer, rTime)) {
            showDialogErrorMessage("wrongTimeForTrainer");
            return;
        }

        if (!isDateAndTimeValid(localrDate, rTime)) {
            showDialogErrorMessage("wrongTime");
            return;
        }

        if (isReservationExist(loginTrainer.getNum(), localrDate, rTime)) {
            showDialogErrorMessage("reservationHasExist");
            return;
        }

        if (!isValidPtTicket(memberNum)) {
            showDialogErrorMessage("noPTTicket");
            return;
        }

        if(isNotYourMember(memberNum)) {
            showDialogErrorMessage("notYourMember");
            return;
        }

        if(addReservationValidate(memberName)) return;
        reservation.setTrainerNum(SelectedTrainer.loginTrainer.getNum());
        reservation.setMemberNum(memberNum);
        reservation.setMemberName(memberName);
        reservation.setReservationDate(rDate);
        reservation.setReservationTime(rTime);

        //예약 저장
        service.addReservation(reservation);
        showDialogAndMovePage("addReservation", "/view/trainer/reservationInfo", event);

    }

    @Override
    public void initialize(URL url, ResourceBundle resources) {

        columnBindingReservation(memberNumCol, memberNameCol, memberPhoneCol, rDateCol, rTimeCol);
        selectCol.setCellFactory(CheckBoxTableCell.forTableColumn(selectCol));
        selectCol.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        loadReservationData(reservationTable, reservationRepository);
        trainer = loginTrainer;

        rDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null) {
                onDateChanged();
            }
        });

        if(loginTrainer != null && rDatePicker.getValue() != null) {
            onDateChanged();
        }

        UnaryOperator<TextFormatter.Change> filter2 = change -> {
            String newText = change.getControlNewText();
            // 숫자만 허용합니다.
            if (newText.matches("\\d*")) {
                return change;
            }
            // 숫자가 아닌 입력은 무시합니다.
            return null;
        };

        TextFormatter<String> memberNumFormatter = new TextFormatter<>(filter2);

        numField.setTextFormatter(memberNumFormatter);

        reservationTable.setRowFactory(tv -> {
            TableRow<Reservation> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 2) {
                    Reservation selectedReservation = row.getItem();
                    if (selectedReservation != null) {
                        SelectedReservation.currentReservation = selectedReservation; // 현재 예약을 선택된 예약으로 설정
                        try {
                            reservationDetail(selectedReservation, event);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
            return row;
        });
    }

    Trainer trainer = new Trainer();

    @FXML
    private void reservationDetail(Reservation reservation, MouseEvent event) throws IOException {
        if(reservation != null && event.getClickCount() == 2) {
            if (memberInfoDialog != null && memberInfoDialog.isShowing()) {
                memberInfoDialog.close();
            }

            currentReservation = reservation;
            loginTrainer = trainer;
            movePage(event, "/view/trainer/ReservationDetail");
        }
    }

    @FXML
    private void searchMember() {
        String searchName = searchMemberNameField.getText().trim();

        if (searchName.isEmpty()) {
            showDialogErrorMessage("emptyName");
            return;
        }

        List<Member> searchedMembers = memberRepository.searchMembersByName(searchName);

        if(searchedMembers.isEmpty()) {
            showDialogErrorMessage("noFindByNameMember");
            return;
        }

        List<Reservation> reservations = reservationRepository.findByMemberName(searchName);
        ObservableList<Reservation> observableList = FXCollections.observableArrayList(reservations);
        reservationTable.setItems(observableList);
    }

    @FXML
    private void resetPageReservedMember(ActionEvent event) throws IOException {
        AdminTab.getInstance().setSelectedTabIndex(0);
        movePage(event, "/view/trainer/reservationInfo");
    }

    @FXML
    private void logout(ActionEvent event) throws IOException {
        if (memberInfoDialog != null && memberInfoDialog.isShowing()) {
            memberInfoDialog.close();
        }

        moveToMainPage(event);
    }

    @FXML
    public void cancelReservation(ActionEvent event) throws IOException {
        if (reservationTable.getItems().isEmpty()) {
            showDialogErrorMessage("noReservation");
            return;
        }

        List<Reservation> selectedReservations = reservationTable.getItems().stream()
                .filter(Reservation::isSelected)
                .toList();

        if(selectedReservations.isEmpty()) {
            showDialogErrorMessage("noSelectDeleteReservation");
            return;
        }

        Optional<ButtonType> response = showDialogChooseMessage("reallyDeleteReservation");

        if(response.isPresent() && response.get() == ButtonType.OK) {
            //선택한 예약 내역 모두 삭제
            for (Reservation reservation : selectedReservations) {
                reservationRepository.deleteReservation(reservation.getReservationNum());
                // 삭제한 예약 내역만큼 회원에게 PT 이용권 되돌려주기
                setRemain(reservation.getMemberNum(), PT_TICKET, 1);
            }
            showDialogAndMovePage("deleteReservation", "/view/trainer/reservationInfo", event);
        }
    }

    private static Dialog<Void> memberInfoDialog;

    // 메서드명 변경 (성진)
    @FXML
    public void showMemberInfo() {

        // 다이얼로그가 이미 열려 있으면 추가적으로 열지 않음
        if (memberInfoDialog != null && memberInfoDialog.isShowing()) {
            return;
        }

        memberInfoDialog = new Dialog<>();;
        memberInfoDialog.setTitle("회원 정보");

        ButtonType closeButtonType = new ButtonType("닫기", ButtonBar.ButtonData.CANCEL_CLOSE);
        memberInfoDialog.getDialogPane().getButtonTypes().addAll(closeButtonType);
        Button closeButton = (Button) memberInfoDialog.getDialogPane().lookupButton(closeButtonType);
        closeButton.getStyleClass().add("closeBtn");

        TableView<Member> table = new TableView<>();
        table.getStyleClass().add("tableView");

        List<Member> members = memberRepository.findAllMembers();
        ObservableList<Member> filteredMembers = FXCollections.observableArrayList();

        for (Member member : members) {
            int trainerNum = getTrainerNumForMember(member.getNum());
            if(trainerNum != 0 && trainerNum == loginTrainer.getNum()) {
                filteredMembers.add(member);
            }
        }

        loadMemberInfo(table, filteredMembers);

        VBox vbox = new VBox(table);
        DialogPane dialogPane = memberInfoDialog.getDialogPane();
        dialogPane.setContent(vbox);
        dialogPane.getStylesheets().add(getClass().getResource("/css/ReservedMemberInfo.css").toExternalForm());

        // Dialog의 Stage에 접근하여 아이콘 설정 (승빈)
        Stage dialogStage = (Stage) memberInfoDialog.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new Image(getClass().getResourceAsStream("/image/JavaGym_Logo.jpeg")));

        // 회원 정보를 확인하며 예약을 등록할 수 있도록 수정 (성진)
        dialogStage.initModality(Modality.NONE);
        memberInfoDialog.show();
    }

    public boolean checkMember(int memberNum, String memberName) {
        Member member = memberRepository.findByNum(memberNum);
        if(member == null) {
            return false;
        }
        return member.getName().equals(memberName);
    }

    private void setupTimeComboBox(WorkingHour workingHour, List<Integer> reservedHours) {
        ObservableList<String> hours = FXCollections.observableArrayList();
        int startHour = (workingHour == WorkingHour.AM) ? 8 : 14;
        int endHour = (workingHour == WorkingHour.AM) ? 13 : 19;

        for (int hour = startHour; hour <= endHour; hour++) {
            if (!reservedHours.contains(hour)) {
                hours.add(String.format("%02d:00", hour));
            }
        }
        rTimeComboBox.setItems(hours);
        if (!hours.isEmpty()) {
            rTimeComboBox.setValue(hours.get(0));
        }
    }

    @FXML
    private void onDateChanged() {
        LocalDate selectedDate = rDatePicker.getValue();
        if (selectedDate != null) {
            Date sqlDate = Date.valueOf(selectedDate);
            List<Integer> reservationHour = reservationRepository.findReservationHours(loginTrainer.getNum(), sqlDate);
            setupTimeComboBox(loginTrainer.getWorkingHour(), reservationHour);
        }
    }

}
