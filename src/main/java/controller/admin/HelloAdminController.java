package controller.admin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

import static util.PageUtil.*;

public class HelloAdminController {

    @FXML
    public void memberInfo(ActionEvent event) throws IOException {
        movePageCenter(event, "/view/admin/memberInfo");
    }

    @FXML
    public void trainerInfo(ActionEvent event) throws IOException {
        movePageCenter(event, "/view/admin/trainerInfo");
    }

    @FXML
    private void logout(ActionEvent event) throws IOException {
        movePage(event, "/view/member/memberLogin");
    }
}