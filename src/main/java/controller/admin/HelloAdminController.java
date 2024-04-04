package controller.admin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

import static util.PageUtil.*;

public class HelloAdminController {

    @FXML
    public void memberInfo(ActionEvent event) throws IOException {
        movePageCenter(event, "Admin", "/view/admin/memberInfo");
    }

    @FXML
    public void trainerInfo() {

    }

    @FXML
    private void logout(ActionEvent event) throws IOException {
        movePage(event, "Login", "/view/member/memberLogin");
    }
}