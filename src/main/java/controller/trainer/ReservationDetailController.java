package controller.trainer;

import domain.trainer.Reservation;
import domain.trainer.Trainer;
import domain.trainer.WorkingHour;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
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
    private ComboBox<String> rTimeComboBox;

    @FXML
    private DatePicker ptDatePicker;

    @FXML
    private Label memberNameLabel, memberPhoneLabel, rDateLabel, rTimeLabel;

    @FXML
    private void updateReservation(ActionEvent event) throws IOException {
        //수정 내용이 없을 경우
        if (isSameReservationTime() && isSameReservationDate()) {
            showDialogErrorMessage("isSame");
            return;
        }

        //수정 내용이 있을 경우
        Optional<ButtonType> response = showDialogChoose("예약 정보를 수정하시겠습니까?");
        if (response.isEmpty() || response.get() != ButtonType.OK) {
            return;
        }

        if (response.get() == ButtonType.OK) {
            System.out.println("수정 내역이 있음");
            Date rDate = Date.valueOf(ptDatePicker.getValue());
            LocalDate localrDate = rDate.toLocalDate();
            String rTimeInput = rTimeComboBox.getSelectionModel().getSelectedItem();
            int rTime = Integer.parseInt(rTimeInput.split(":")[0]);

                if (rTime < 8 || rTime > 19) {
                    showDialogErrorMessage("invalidTime");
                    return;
                }

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
                    currentReservation.setReservationTime(Integer.parseInt(rTimeComboBox.getSelectionModel().getSelectedItem().split(":")[0]));
                    reservationRepository.updateReservation(currentReservation);
                }

                //PT 날짜, 시간 모두 변경
                if(!isSameReservationDate() && !isSameReservationTime()) {
                    currentReservation.setReservationDate(Date.valueOf(ptDatePicker.getValue()));
                    currentReservation.setReservationTime(Integer.parseInt(rTimeComboBox.getSelectionModel().getSelectedItem().split(":")[0]));
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
        String inputPtTime = rTimeComboBox.getSelectionModel().getSelectedItem().split(":")[0];
        if(!inputPtTime.matches("\\d+")) {
            return false;
        }
        Integer PTTime = Integer.parseInt(inputPtTime);
        Integer currentPtTime = currentReservation.getReservationTime();
        return PTTime.equals(currentPtTime);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if(currentReservation != null) {
            Reservation reservation = currentReservation;

            ptDatePicker.setValue(reservation.getReservationDate().toLocalDate());
            memberNameLabel.setText(reservation.getMemberName());
            memberPhoneLabel.setText(formatPhone(reservation.getMemberPhone()));
            rDateLabel.setText(new SimpleDateFormat("yyyy-MM-dd").format(reservation.getReservationDate()));
            rTimeLabel.setText(String.format("%02d:00", reservation.getReservationTime()));
            setupTimeComboBox(currentTrainer);
        }

    }

    @FXML
    private void goBack(ActionEvent event) throws IOException {
        if (currentTrainer != null && currentTrainer.getNum() != null) {
            movePageTimerOff(event, "/view/trainer/reservationInfo");
        }
    }

    private void setupTimeComboBox(Trainer trainer) {
        ObservableList<String> hours = FXCollections.observableArrayList();
        if(trainer.getWorkingHour() == WorkingHour.AM) {
            for(int i=8; i<14; i++) {
                hours.add(i+":00");
            }
        }
        else if(trainer.getWorkingHour() == WorkingHour.PM) {
            for(int i=14; i<20; i++) {
                hours.add(i+":00");
            }
        }
        rTimeComboBox.setItems(hours);
        rTimeComboBox.setValue(hours.get(0));
    }
}