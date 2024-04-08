package controller.trainer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

import static util.PageUtil.movePage;
import static util.PageUtil.movePageCenter;

public class HelloTrainerController {
    @FXML
    public void reservationInfo(ActionEvent event) throws IOException {
        movePageCenter(event, "/view/admin/reservationInfo");
    }

    @FXML
    private void logout(ActionEvent event) throws IOException {
        movePage(event, "/view/member/memberLogin");
    }
}
