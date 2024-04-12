package controller.trainer;

import domain.member.Member;
import domain.trainer.TrainerSchedule;
import domain.trainer.Trainer;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import repository.MemberRepository;
import repository.ReservationRepository;
import repository.TrainerRepository;
import service.TrainerService;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ResourceBundle;


import static converter.StringToDateConverter.stringToDate;
import static util.AlertUtil.showAlertAddScheduleFail;
import static util.AlertUtil.showAlertAndMove;
import static util.ControllerUtil.columnBindingReservation;
import static util.ControllerUtil.loadReservationData;
import static util.PageUtil.movePageCenter;
import static util.ValidateUtil.isEmptyAnyField;


public class ReservationInfoController implements Initializable {

    private final ResourceBundle config = ResourceBundle.getBundle("config.init");
    private MemberRepository memberRepository = new MemberRepository();
    private final TrainerRepository trainerRepository = new TrainerRepository();

    @FXML
    private TableView<TrainerSchedule> scheduleTable;

    @FXML
    private TableColumn<TrainerSchedule, String> memberNameColumn, dateColumn, timeColumn;

    @Override
    public void initialize(URL url, ResourceBundle resources) {
        loadReservationData(scheduleTable);

        scheduleTable.setRowFactory(tv -> {
            TableRow<TrainerSchedule> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                try {
                    TrainerSchedule trainerSchedule = row.getItem();
                    trainerScheduleDetail(trainerSchedule, event);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            return row;
        });
    }

    @FXML
    private void addReservation(ActionEvent event) {
        // 예약 추가 로직 구현
    }

    @FXML
    private void trainerScheduleDetail(TrainerSchedule trainerSchedule, MouseEvent event) throws IOException {
        if(trainerSchedule != null && event.getClickCount() == 2) {
            movePageCenter(event, "/view/trainer/reservationDetail");
        }
    }
    private int getLoggedInTrainerId() {
        // 로그인한 트레이너의 ID를 반환하는 로직 구현
        return 0; // 임시 반환값
    }
    @FXML
    private void goBack(ActionEvent event) throws IOException {
        movePageCenter(event, "/view/trainer/helloTrainer");
    }
}
