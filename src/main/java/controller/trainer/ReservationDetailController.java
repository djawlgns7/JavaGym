package controller.trainer;

import domain.member.Member;
import domain.trainer.Reservation;
import domain.trainer.Trainer;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import repository.ReservationRepository;
import repository.TrainerRepository;
import util.AlertUtil;


import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static converter.StringToDateConverter.stringToDate;
import static domain.trainer.SelectedTrainer.currentTrainer;
import static util.AlertUtil.showAlertChoose;
import static util.AlertUtil.showAlertUpdateReservationFail;
import static util.PageUtil.*;

public class ReservationDetailController implements Initializable {

    private final ReservationRepository reservationRepository = new ReservationRepository();
    private final TrainerRepository trainerRepository = new TrainerRepository();

    @FXML
    private TextField nameField, rDateField, rTimeField;

    @FXML
    private DatePicker ptDatePicker;

    @FXML
    private TableView<Reservation> reservationTable;

    @FXML
    private TableColumn<Reservation, String>  memberNumCol, memberNameCol,memberPhoneCol, rDateCol, rTimeCol;

    @FXML
    private TableColumn<Reservation, Boolean> selectCol;

    private Reservation reservation;
    @FXML
    private void updateReservation(ActionEvent event) throws IOException, ParseException {
        System.out.println("updateReservation");
        LocalDate newDate = ptDatePicker.getValue();
        int newTime = Integer.parseInt(rTimeField.getText());
        //수정 내용이 없을 경우
        if(isSameBasicInfo() && isSameAdditionInfo()) {
            System.out.println("수정 내용 없음");
            showAlertUpdateReservationFail("isSame");
        }

        //수정 내용이 있을 경우
        Optional<ButtonType> response = showAlertChoose("예약 정보를 수정하시겠습니까?");
        if (response.get() == ButtonType.OK) {
            System.out.println("수정 내역이 있음");

            //PT 일정, 시간 모두 변경
            if(!isSamePTDate() && !isSamePTTime()) {
                reservation.setReservationDate(java.sql.Date.valueOf(newDate));
                reservation.setReservationTime(newTime);
                reservationRepository.updateReservation(reservation);
            }

            if(!isSamePTDate() && isSamePTTime()) {
                reservation.setReservationDate(java.sql.Date.valueOf(newDate));
                reservationRepository.updateReservation(reservation);
            }

            if(isSamePTDate() && !isSamePTTime()) {
                reservation.setReservationTime(newTime);
                reservationRepository.updateReservation(reservation);
            }
        }
    }

    @FXML
    private void cancelReservation(ActionEvent event) {
        Optional<ButtonType> response = AlertUtil.showAlertChoose("정말로 취소하시겠습니까?");
        if(response.isPresent() && response.get() == ButtonType.OK) {
            reservationRepository.deleteReservation(reservation.getReservationNum());
            AlertUtil.showAlertChoose("예약이 취소되었습니다.");
        }
    }

    private boolean isSameBasicInfo() {
        Date ptdate = Date.valueOf(ptDatePicker.getValue());
        Integer pttime = Integer.valueOf(rTimeField.getText().trim());

        return reservation.getReservationDate().equals(ptdate) && reservation.getReservationTime()==(pttime);
    }

    private boolean isSameAdditionInfo() throws ParseException {
        return isSamePTDate() && isSamePTTime();
    }

    private boolean isSamePTDate() throws ParseException {
        String newDateString = rDateField.getText().trim();
        Date newDate = stringToDate(newDateString);
        Date existDate = reservation.getReservationDate();
        return newDate.equals(existDate);
    }

    private boolean isSamePTTime() {
        int newTime = Integer.parseInt(rTimeField.getText().trim());
        int existTime = reservation.getReservationTime();
        return newTime == existTime;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println(currentTrainer);
        if(reservation != null && currentTrainer != null) {

            ptDatePicker.setValue(reservation.getReservationDate().toLocalDate());
            rTimeField.setText(String.valueOf(reservation.getReservationTime()));
            columnBinding();
            loadReservationDetails();
        }
    }

    private void columnBinding() {
        selectCol.setCellFactory(CheckBoxTableCell.forTableColumn(selectCol));
        selectCol.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        memberNumCol.setCellValueFactory(new PropertyValueFactory<>("memberNum"));
        memberNameCol.setCellValueFactory(new PropertyValueFactory<>("memberName"));
        memberPhoneCol.setCellValueFactory(new PropertyValueFactory<>("memberPhone"));
        rDateCol.setCellValueFactory(new PropertyValueFactory<>("reservationDate"));
        rTimeCol.setCellValueFactory(new PropertyValueFactory<>("reservationTime"));
    }
    private void loadReservationDetails() {
        List<Reservation> reservation = reservationRepository.findReservation(currentTrainer.getNum());
        ObservableList<Reservation> reservations = FXCollections.observableArrayList();
        reservations.addAll(reservation);

        reservationTable.setItems(reservations);
    }

    @FXML
    private void goBack(ActionEvent event) throws IOException {
        movePage(event, "/view/trainer/reservationInfo");
    }
}
