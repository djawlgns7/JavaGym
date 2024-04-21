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
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import static domain.trainer.SelectedReservation.*;
import static domain.trainer.SelectedTrainer.loginTrainer;
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

            ptDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
                if(newValue != null) {
                    onDateChanged();
                }
            });

            if(currentTrainer != null && ptDatePicker.getValue() != null) {
                onDateChanged();
            }
        }

    }

    @FXML
    private void goBack(ActionEvent event) throws IOException {
        if (loginTrainer != null && loginTrainer.getNum() != null) {
            movePageTimerOff(event, "/view/trainer/reservationInfo");
        }
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
        LocalDate selectedDate = ptDatePicker.getValue();
        if (selectedDate != null) {
            Date sqlDate = Date.valueOf(selectedDate);
            List<Integer> reservationHour = reservationRepository.findReservationHours(currentTrainer.getNum(), sqlDate);
            setupTimeComboBox(currentTrainer.getWorkingHour(), reservationHour);
        }
    }
}