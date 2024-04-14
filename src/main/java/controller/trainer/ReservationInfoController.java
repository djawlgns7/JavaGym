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
import static domain.trainer.SelectedReservation.currentReservation;
import static domain.trainer.SelectedTrainer.currentTrainer;
import static util.AlertUtil.showAlertAddScheduleFail;
import static util.AlertUtil.showAlertAndMove;
import static util.ControllerUtil.columnBindingReservation;
import static util.ControllerUtil.loadReservationData;
import static util.PageUtil.movePageCenter;
import static util.ValidateUtil.*;

public class ReservationInfoController implements Initializable {


    private TrainerRepository trainerRepository = new TrainerRepository();
    private ReservationRepository reservationRepository = new ReservationRepository();
    private final TrainerService service = new TrainerService(trainerRepository);
    @FXML
    private TextField numField, nameField, phoneField, rdateField, rtimeField;

    @FXML
    private TableView<Reservation> reservationTable;

    @FXML
    private TableColumn<Reservation, String> memberNumCol, memberNameCol, memberPhoneCol, rdateCol, rtimeCol;


    @FXML
    private void addReservationInfo(ActionEvent event) throws ParseException, IOException {
        if (isEmptyAnyField(nameField, rdateField, rtimeField)) {
            showAlertAddScheduleFail("emptyAnyField");
            return;
        }
        Reservation reservation = new Reservation();

        // 예약 추가 로직 구현
        Integer memberNum = Integer.valueOf(numField.getText().trim());
        String memberName = nameField.getText().trim();
        String memberPhone = phoneField.getText().trim();
        String rdate = rdateField.getText().trim();
        Integer rtime = Integer.valueOf(rtimeField.getText().trim());

        if(addReservationValidate(memberName, memberPhone,rdate, rtime, trainer)) return;
        reservation.setTrainerNum(SelectedTrainer.currentTrainer.getNum());
        reservation.setMemberNum(memberNum);
        reservation.setMemberName(memberName);
        reservation.setMemberPhone(memberPhone);
        reservation.setReservationDate(stringToDate(rdate));
        reservation.setReservationTime(rtime);

        //예약 저장
        service.addReservation(reservation);
        showAlertAndMove("예약 등록 성공", Alert.AlertType.INFORMATION, "/view/trainer/reservationInfo", event);

    }

    @Override
    public void initialize(URL url, ResourceBundle resources) {
        columnBindingReservation(memberNumCol, memberNameCol, memberPhoneCol, rdateCol, rtimeCol);
        loadReservationData(reservationTable, reservationRepository);
        trainer = currentTrainer;
        TextFormatter<String> rdateFormatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if(newText.matches("\\d{0,6}")) {
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
        rdateField.setTextFormatter(rdateFormatter);
        rtimeField.setTextFormatter(rtimeFormatter);

        reservationTable.setRowFactory(tv -> {
            TableRow<Reservation> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 2) {
                    Reservation selectedReservation = row.getItem();
                    if (selectedReservation != null) {
                        System.out.println("Clicked reservation number: " + selectedReservation.getReservationNum());
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
            System.out.println(currentReservation.getReservationNum());
            System.out.println("Reservation details: " + reservation.toString());
            System.out.println(trainer);
            movePageCenter(event, "/view/trainer/reservationDetail");
        }
    }

    @FXML
    private void goBack(ActionEvent event) throws IOException {
        movePageCenter(event, "/view/trainer/helloTrainer");
    }
}