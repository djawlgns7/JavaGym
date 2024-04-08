package controller.trainer;

import domain.member.Member;
import domain.trainer.TrainerSchedule;
import domain.trainer.Trainer;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import repository.MemberRepository;
import repository.ReservationRepository;
import repository.TrainerRepository;
import service.TrainerService;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ResourceBundle;


import static util.PageUtil.movePageCenter;


public class ReservationInfoController implements Initializable {

    private final ResourceBundle config = ResourceBundle.getBundle("config.init");
    private final ReservationRepository reservationRepository = new ReservationRepository();
    private final TrainerRepository trainerRepository = new TrainerRepository();
    private final TrainerService service = new TrainerService(trainerRepository);

    private final MemberRepository memberRepository = new MemberRepository();
    @FXML
    private TextField nameField, birthField, phoneField, enrollField;

    @FXML
    private TableView<TrainerSchedule> scheduleTable;

    @FXML
    private TableColumn<TrainerSchedule, String> countColumn, memberNameColumn, dateColumn, timeColumn;

    @Override
    public void initialize(URL url, ResourceBundle resources) {

    }


    String phone = phoneField.getText().trim();


    @FXML
    private void goBack(ActionEvent event) throws IOException {
        movePageCenter(event, "view/trainer/helloTrainer");
    }
}
