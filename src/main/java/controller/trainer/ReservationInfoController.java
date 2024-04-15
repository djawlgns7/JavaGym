package controller.trainer;

import domain.Item;
import domain.member.Member;
import domain.member.UsingLocker;
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
import javafx.stage.Stage;
import repository.MemberRepository;
import repository.ReservationRepository;
import repository.TrainerRepository;
import service.TrainerService;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

import static domain.Item.PT_TICKET;
import static domain.member.SelectedMember.currentMember;
import static domain.trainer.SelectedTrainer.currentTrainer;
import static domain.trainer.SelectedReservation.currentReservation;

import static util.ControllerUtil.*;
import static util.DialogUtil.*;
import static util.MemberUtil.setRemain;
import static util.PageUtil.movePage;
import static util.ValidateUtil.*;

public class ReservationInfoController implements Initializable {

    private TrainerRepository trainerRepository = new TrainerRepository();
    private ReservationRepository reservationRepository = new ReservationRepository();
    private MemberRepository memberRepository = new MemberRepository();
    private final TrainerService service = new TrainerService(trainerRepository);
    @FXML
    private TextField numField, nameField, phoneField, rtimeField, searchMemberNameField;
    @FXML
    private DatePicker rDatePicker;

    @FXML
    private TableView<Reservation> reservationTable;

    @FXML
    private TableColumn<Reservation, Boolean> selectCol;

    @FXML
    private TableColumn<Reservation, String> memberNumCol, memberNameCol, memberPhoneCol, rDateCol, rTimeCol;


    @FXML
    private void addReservationInfo(ActionEvent event) throws ParseException, IOException {
        if (isEmptyAnyField(numField, nameField, phoneField, rtimeField)) {
            showDialogErrorMessage("emptyAnyField");
            return;
        }

        Reservation reservation = new Reservation();

        // 예약 추가 로직 구현
        Integer memberNum = Integer.valueOf(numField.getText().trim());
        String memberName = nameField.getText().trim();
        String memberPhone = phoneField.getText().trim();
        Date rDate = Date.valueOf(rDatePicker.getValue());
        Integer rTime = Integer.valueOf(rtimeField.getText().trim());
        LocalDate localrDate = rDate.toLocalDate();

        if (!isValidTimeForTrainer(currentTrainer, rTime)) {
            showDialogErrorMessage("wrongTimeForTrainer");
            return;
        }

        if (!isDateAndTimeValid(localrDate, rTime)) {
            showDialogErrorMessage("wrongTime");
            return;
        }

        if (isReservationExist(currentTrainer.getNum(), localrDate, rTime)) {
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

        if(addReservationValidate(memberName, memberPhone)) return;
        reservation.setTrainerNum(SelectedTrainer.currentTrainer.getNum());
        reservation.setMemberNum(memberNum);
        reservation.setMemberName(memberName);
        reservation.setMemberPhone(memberPhone);
        reservation.setReservationDate(rDate);
        reservation.setReservationTime(rTime);

        //예약 저장
        service.addReservation(reservation);
        showDialogAndMovePageTimerOff("예약 등록 성공", "/view/trainer/reservationInfo", event);

    }

    @Override
    public void initialize(URL url, ResourceBundle resources) {
        columnBindingReservation(memberNumCol, memberNameCol, memberPhoneCol, rDateCol, rTimeCol);
        selectCol.setCellFactory(CheckBoxTableCell.forTableColumn(selectCol));
        selectCol.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        loadReservationData(reservationTable, reservationRepository);
        trainer = currentTrainer;
        TextFormatter<String> phoneFormatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d{0,8}")) {
                return change;
            }
            return null;
        });

        TextFormatter<String> rtimeFormatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if(newText.matches("([01]?[0-9]|2[0-3])")) {
                return change;
            }
            return null;
        });

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
        phoneField.setTextFormatter(phoneFormatter);
        rDatePicker.setValue(LocalDate.now());
        rtimeField.setTextFormatter(rtimeFormatter);

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
            currentReservation = reservation;
            currentTrainer = trainer;
            movePageTimerOff(event, "/view/trainer/reservationDetail");
        }
    }

    @FXML
    private void searchMember() {
        String searchName = searchMemberNameField.getText().trim();

        if (searchName.isEmpty()) {
            showDialog("이름을 입력해 주세요.");
            return;
        }

        List<Member> searchedMembers = memberRepository.searchMembersByName(searchName);

        if(searchedMembers.isEmpty()) {
            showDialog("해당 이름의 회원이 없습니다.");
            return;
        }

        List<Reservation> reservations = reservationRepository.findByMemberName(searchName);
        ObservableList<Reservation> observableList = FXCollections.observableArrayList(reservations);
        reservationTable.setItems(observableList);
    }

    @FXML
    private void logout(ActionEvent event) throws IOException {
        movePage(event, "/view/member/memberLogin");
    }

    @FXML
    public void cancelReservation(ActionEvent event) throws IOException {
        if(reservationTable.getItems().isEmpty()) {
            showDialog("예약 정보가 없습니다.");
            return;
        }

        List<Reservation> selectedReservations = reservationTable.getItems().stream()
                .filter(Reservation::isSelected)
                .toList();

        if(selectedReservations.isEmpty()) {
            showDialog("취소할 예약을 선택해주세요.");
            return;
        }

        Optional<ButtonType> response = showDialogChoose("정말 선택하신 예약을 취소하시겠습니까?");

        if(response.isPresent() && response.get() == ButtonType.OK) {
            //선택한 예약 내역 모두 삭제
            for (Reservation reservation : selectedReservations) {
                reservationRepository.deleteReservation(reservation.getReservationNum());
                // 삭제한 예약 내역만큼 회원에게 PT 이용권 되돌려주기
                setRemain(reservation.getMemberNum(), PT_TICKET, 1);
            }
            showDialogAndMovePage("예약 정보가 삭제되었습니다.", "/view/trainer/reservationInfo", event);
        }
    }

    @FXML
    public void showUserNum() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("회원 정보");

        ButtonType closeButtonType = new ButtonType("닫기", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(closeButtonType);
        Button closeButton = (Button) dialog.getDialogPane().lookupButton(closeButtonType);
        closeButton.getStyleClass().add("closeBtn");

        TableView<UsingLocker> table = new TableView<>();
        table.getStyleClass().add("tableView");
        loadmemberInfo(table, memberRepository);

        VBox vbox = new VBox(table);
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setContent(vbox);
        dialogPane.getStylesheets().add(getClass().getResource("/css/MemberInfo.css").toExternalForm());

        // Dialog의 Stage에 접근하여 아이콘 설정 (승빈)
        Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new Image(getClass().getResourceAsStream("/image/JavaGym_Logo.jpeg")));

        dialog.showAndWait();
    }
}