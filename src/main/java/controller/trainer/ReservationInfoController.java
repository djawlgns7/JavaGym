package controller.trainer;


import domain.member.Member;
import domain.trainer.*;

import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.w3c.dom.Text;
import repository.AdminRepository;
import repository.MemberRepository;
import repository.ReservationRepository;
import repository.TrainerRepository;
import service.TrainerService;
import static domain.trainer.SelectedReservation.currentReservation;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

import static converter.StringToDateConverter.stringToDate;
import static domain.trainer.SelectedTrainer.currentTrainer;

import static util.ControllerUtil.columnBindingReservation;
import static util.ControllerUtil.loadReservationData;
import static util.DialogUtil.*;
import static util.PageUtil.movePage;
import static util.ValidateUtil.*;

public class ReservationInfoController implements Initializable {

    private TrainerRepository trainerRepository = new TrainerRepository();
    private ReservationRepository reservationRepository = new ReservationRepository();
    private final TrainerService service = new TrainerService(trainerRepository);
    @FXML
    private TextField numField, nameField, phoneField, rtimeField;
    @FXML
    private DatePicker rDatePicker;

    @FXML
    private TableView<Reservation> reservationTable;

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
        showDialogAndMovePage("예약 등록 성공", "/view/trainer/reservationInfo", event);

    }

    @Override
    public void initialize(URL url, ResourceBundle resources) {
        columnBindingReservation(memberNumCol, memberNameCol, memberPhoneCol, rDateCol, rTimeCol);
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

        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getText();
            if (text.matches("[^0-9]*")) {
                return change;
            }
            return null;
        };

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
        TextFormatter<String> memberNameFormatter = new TextFormatter<>(filter);

        numField.setTextFormatter(memberNumFormatter);
        nameField.setTextFormatter(memberNameFormatter);
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
            movePage(event, "/view/trainer/reservationDetail");
        }
    }

    @FXML
    private void goBack(ActionEvent event) throws IOException {
        movePage(event, "/view/trainer/helloTrainer");
    }
}