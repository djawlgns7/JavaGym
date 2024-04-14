package controller.trainer;

import converter.DateToStringConverter;
import domain.member.Member;
import domain.trainer.Reservation;
import domain.trainer.SelectedReservation;
import domain.trainer.SelectedTrainer;
import domain.trainer.Trainer;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import static domain.trainer.SelectedReservation.*;
import static domain.trainer.SelectedTrainer.*;
import static domain.trainer.SelectedTrainer.currentTrainer;
import static util.AlertUtil.*;
import static util.PageUtil.*;

public class ReservationDetailController implements Initializable {

    private final ReservationRepository reservationRepository = new ReservationRepository();
    private final TrainerRepository trainerRepository = new TrainerRepository();

    @FXML
    private TextField rTimeField;

    @FXML
    private DatePicker ptDatePicker;

    @FXML
    private TableView<Reservation> reservationTable;

    @FXML
    private TableColumn<Reservation, String> memberNumCol, memberNameCol,memberPhoneCol, rDateCol, rTimeCol;


    @FXML
    private void updateReservation(ActionEvent event) throws IOException {
        //수정 내용이 없을 경우
        if(isSameReservationTime() && isSameReservationDate()) {
            showAlertUpdateReservationFail("isSame");
            return;
        }

        //수정 내용이 있을 경우
        Optional<ButtonType> response = showAlertChoose("예약 정보를 수정하시겠습니까?");
        if (response.get() == ButtonType.OK) {
            System.out.println("수정 내역이 있음");

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

                showAlertAndMoveCenter("예약 정보가 수정되었습니다.", Alert.AlertType.INFORMATION, "/view/trainer/reservationDetail", event);
            }
        }
    }

    @FXML
    private void cancelReservation(ActionEvent event) throws IOException {
        Optional<ButtonType> response = AlertUtil.showAlertChoose("정말로 취소하시겠습니까?");
        if(response.isPresent() && response.get() == ButtonType.OK) {
            reservationRepository.deleteReservation(currentReservation.getReservationNum());
            showAlertAndMoveCenter("예약 정보가 삭제되었습니다.", Alert.AlertType.INFORMATION, "/view/trainer/reservationDetail", event);
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
            rTimeField.setTextFormatter(rTimeFormatter);
            columnBinding();
            loadReservationDetails();

        }

    }

    private void columnBinding() {
        memberNumCol.setCellValueFactory(new PropertyValueFactory<>("memberNum"));
        memberNameCol.setCellValueFactory(new PropertyValueFactory<>("memberName"));
        memberPhoneCol.setCellValueFactory(new PropertyValueFactory<>("memberPhone"));
        rDateCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                new SimpleDateFormat("yyyy-MM-dd").format(cellData.getValue().getReservationDate())));
        rTimeCol.setCellValueFactory(new PropertyValueFactory<>("reservationTime"));
    }
    private void loadReservationDetails() {
        ObservableList<Reservation> reservations = FXCollections.observableArrayList();
        reservations.add(currentReservation);
        reservationTable.setItems(reservations);
    }

    @FXML
    private void goBack(ActionEvent event) {
        try {
            if (currentTrainer != null && currentTrainer.getNum() != null) {
                movePageCenter(event, "/view/trainer/reservationInfo");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
