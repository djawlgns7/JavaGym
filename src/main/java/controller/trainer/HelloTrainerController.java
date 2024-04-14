package controller.trainer;

import domain.trainer.SelectedTrainer;
import domain.trainer.Trainer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import repository.TrainerRepository;
import service.TrainerService;

import java.io.IOException;

import static domain.trainer.SelectedTrainer.*;
import static util.AlertUtil.showAlert;
import static util.PageUtil.movePage;
import static util.PageUtil.movePageCenter;


public class HelloTrainerController {
   private TrainerRepository trainerRepository = new TrainerRepository();
    @FXML
    public void reservationInfo(ActionEvent event) throws IOException {
        if (SelectedTrainer.currentTrainer != null && SelectedTrainer.currentTrainer.getId() != null) {
            Trainer updatedTrainer = trainerRepository.findById(SelectedTrainer.currentTrainer.getId());
            if (updatedTrainer != null) {
                SelectedTrainer.setCurrentTrainer(updatedTrainer); // 갱신된 트레이너 정보로 업데이트
                movePageCenter(event, "/view/trainer/reservationInfo");
            }
        }
    }

    @FXML
    private void logout(ActionEvent event) throws IOException {
        movePage(event, "/view/member/memberLogin");
    }
}
