package controller.member;

import domain.member.Member;
import domain.trainer.Trainer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import repository.MemberRepository;
import repository.ReservationRepository;
import repository.TrainerRepository;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static domain.member.SelectedMember.currentMember;
import static util.AlertUtil.showAlertAndMove;
import static util.AlertUtil.showAlertChoose;
import static util.MemberUtil.getTrainerNumForMember;
import static util.TrainerUtil.getTrainerSchedule;

public class ReservationController implements Initializable {

    private final TrainerRepository trainerRepository = new TrainerRepository();
    private final ReservationRepository reservationRepository = new ReservationRepository();

    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField memberNo, trainerNo;
    @FXML
    private ComboBox<Integer> timeComboBox;

    List<Boolean>[] reservations;
    Member member;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (currentMember != null) {
            member = currentMember;
            int trainerNum = getTrainerNumForMember(member.getNum());
            Trainer trainer = trainerRepository.findByNum(trainerNum);

            reservations = getTrainerSchedule(trainer, 30);

        }

    }

    // 선택한 날짜와 트레이너를 가지고 그 트레이너의 예약 가능한 시간을 표시한다
    @FXML
    public void makeReservations(){
        LocalDate selectedDate = datePicker.getValue();
        LocalDate today = LocalDate.now();
        int trainerNum = Integer.parseInt(trainerNo.getText());
        Trainer trainer = trainerRepository.findByNum(trainerNum);
        int adder = trainerRepository.getWorkingHourAdder(trainer);

        timeComboBox.getItems().clear();

        reservations = getTrainerSchedule(trainer, 30);

        int DDay = selectedDate.getDayOfYear() - today.getDayOfYear();

        for(int i = 0; i < reservations[DDay].size(); i++){
            if(reservations[DDay].get(i)){
                timeComboBox.getItems().add(i + adder);
            }
        }
    }

    @FXML
    public void saveReservation(ActionEvent event) throws IOException {
        int memberNum = Integer.parseInt(memberNo.getText());
        int trainerNum = Integer.parseInt(trainerNo.getText());
        int selectedTime = timeComboBox.getValue();
        LocalDate selectedDate = datePicker.getValue();

        Optional<ButtonType> result = showAlertChoose("예약을 확정하시겠습니까?");

        if (result.get() == ButtonType.OK){
            reservationRepository.saveReservation(memberNum, trainerNum, selectedDate, selectedTime);
            showAlertAndMove("알림", "예약이 확정되었습니다.", Alert.AlertType.INFORMATION, "/view/member/memberLogin", event);
        }

    }
}
