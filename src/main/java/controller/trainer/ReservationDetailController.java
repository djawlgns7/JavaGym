package controller.trainer;

import domain.trainer.Reservation;
import domain.trainer.Trainer;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import repository.ReservationRepository;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;
import static domain.trainer.SelectedReservation.*;
import static domain.trainer.SelectedTrainer.currentTrainer;
import static util.DialogUtil.*;
import static util.DialogUtil.showDialogChoose;
import static util.ControllerUtil.formatPhone;
import static util.PageUtil.*;
import static util.ValidateUtil.*;

public class ReservationDetailController implements Initializable {

    private final ReservationRepository reservationRepository = new ReservationRepository();

    @FXML
    private TextField rTimeField;

    @FXML
    private DatePicker ptDatePicker;

    @FXML
    private Label memberNameLabel, memberPhoneLabel, rDateLabel, rTimeLabel;


    @FXML
    private void updateReservation(ActionEvent event) throws IOException {
        //수정 내용이 없을 경우
        if(isSameReservationTime() && isSameReservationDate()) {
            showDialogErrorMessage("isSame");
            return;
        }

        //수정 내용이 있을 경우
        Optional<ButtonType> response = showDialogChoose("예약 정보를 수정하시겠습니까?");
        if (response.get() == ButtonType.OK) {
            System.out.println("수정 내역이 있음");
            Date rDate = Date.valueOf(ptDatePicker.getValue());
            LocalDate localrDate = rDate.toLocalDate();
            Integer rTime = Integer.valueOf(rTimeField.getText().trim());

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

            if (!isSameReservationDate() || !isSameReservationTime()) {
                //PT 날짜만 변경, 시간 동일
                if(!isSameReservationDate() && isSameReservationTime()) {
                    currentReservation.setReservationDate(Date.valueOf(ptDatePicker.getValue()));
                    reservationRepository.updateReservation(currentReservation);

                }

                //PT 시간만 변경, 날짜 동일
                if(isSameReservationDate() && !isSameReservationTime()) {
                    currentReservation.setReservationTime(Integer.parseInt(rTimeField.getText().trim()));
                    reservationRepository.updateReservation(currentReservation);
                }

                //PT 날짜, 시간 모두 변경
                if(!isSameReservationDate() && !isSameReservationTime()) {
                    currentReservation.setReservationDate(Date.valueOf(ptDatePicker.getValue()));
                    currentReservation.setReservationTime(Integer.parseInt(rTimeField.getText().trim()));
                    reservationRepository.updateReservation(currentReservation);


                }

                showDialogAndMovePageTimerOff("예약 정보가 수정되었습니다.", "/view/trainer/reservationDetail", event);
            }
        }
    }

    private boolean isSameReservationDate() {
        Date inputPTdate = Date.valueOf(ptDatePicker.getValue());
        Date currentPTdate = currentReservation.getReservationDate();

        return inputPTdate.equals(currentPTdate);
    }

    private boolean isSameReservationTime() {
        Integer inputPTtime = Integer.valueOf(rTimeField.getText());
        Integer currentPTtime = currentReservation.getReservationTime();
        return inputPTtime.equals(currentPTtime);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if(currentReservation != null) {
            Reservation reservation = currentReservation;
            Trainer trainer = currentTrainer;

            TextFormatter<String> rTimeFormatter = new TextFormatter<>(change -> {
                String newText = change.getControlNewText();
                if (newText.matches("([01]?[0-9]|2[0-3])")) {
                    return change;
                }
                return null;
            });

            ptDatePicker.setValue(reservation.getReservationDate().toLocalDate());
            rTimeField.setText(String.format("%02d:00", reservation.getReservationTime()));

            memberNameLabel.setText(reservation.getMemberName());
            memberPhoneLabel.setText(formatPhone(reservation.getMemberPhone()));
            rDateLabel.setText(new SimpleDateFormat("yyyy-MM-dd").format(reservation.getReservationDate()));
            rTimeLabel.setText(String.format("%02d:00", reservation.getReservationTime()));

        }

    }

    @FXML
    private void goBack(ActionEvent event) {
        try {
            if (currentTrainer != null && currentTrainer.getNum() != null) {
                movePageTimerOff(event, "/view/trainer/reservationInfo");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}