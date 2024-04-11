package controller.trainer;

import domain.trainer.TrainerSchedule;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import repository.MemberRepository;
import repository.ReservationRepository;
import repository.TrainerRepository;
import domain.service.TrainerService;

import java.io.IOException;
import java.net.URL;
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
