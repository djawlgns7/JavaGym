package controller.trainer;

import domain.trainer.TrainerSchedule;

import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import repository.MemberRepository;
import repository.ReservationRepository;
import repository.TrainerRepository;
import service.TrainerService;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
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
    private final ReservationRepository reservationRepository = new ReservationRepository();
    private final TrainerRepository trainerRepository = new TrainerRepository();
    private final TrainerService service = new TrainerService(trainerRepository);

    private final MemberRepository memberRepository = new MemberRepository();

    @FXML
    private TextField nameField;

    @FXML
    private TableView<TrainerSchedule> scheduleTable;

    @FXML
    private TableColumn<TrainerSchedule, String>  memberNameColumn, dateColumn, timeColumn;

    @Override
    public void initialize(URL url, ResourceBundle resources) {
        columnBindingReservation(memberNameColumn, dateColumn, timeColumn);
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
/*
    @FXML
    private void addSchedule(ActionEvent event) throws IOException, ParseException {
        if (isEmptyAnyField(nameField)) {
            showAlertAddScheduleFail("emptyAnyField");
            return;
        }

        String name = nameField.getText().trim();

        if(addSchedueValidate(name)) return;

        TrainerSchedule trainerSchedule = new TrainerSchedule();
        trainerSchedule.setSequence(trainerSchedule.getSequence());
        trainerSchedule.setMemberName(nameField.getText().trim());
        trainerSchedule.setReservationDate(stringToDate(String.valueOf(trainerSchedule.getReservationDate())));
        trainerSchedule.setReservationTime(trainerSchedule.getReservationTime());

        service.addSchedule(trainerSchedule);
        showAlertAndMove("알림", "예약 등록 성공", Alert.AlertType.INFORMATION, "/view/trainer/reservationInfo", event);
    }*/

    @FXML
    private void trainerScheduleDetail(TrainerSchedule trainerSchedule, MouseEvent event) throws IOException {
        if(trainerSchedule != null && event.getClickCount() == 2) {
            movePageCenter(event, "/view/trainer/reservationDetail");
        }
    }

    @FXML
    private void goBack(ActionEvent event) throws IOException {
        movePageCenter(event, "view/trainer/helloTrainer");
    }
}
