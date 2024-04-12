package controller.trainer;

import domain.trainer.SelectedTrainer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

import static domain.trainer.SelectedTrainer.*;
import static util.PageUtil.movePage;
import static util.PageUtil.movePageCenter;

public class HelloTrainerController {

    @FXML
    public void reservationInfo(ActionEvent event) throws IOException {
        System.out.println(currentTrainer);
        movePageCenter(event, "/view/trainer/reservationInfo");
    }

    @FXML
    private void logout(ActionEvent event) throws IOException {
        movePage(event, "/view/member/memberLogin");
    }
}
