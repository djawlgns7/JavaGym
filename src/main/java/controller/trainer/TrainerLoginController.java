package controller.trainer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import repository.TrainerRepository;
import domain.service.TrainerService;

import java.io.IOException;

import static util.PageUtil.*;

public class TrainerLoginController {

    private final TrainerRepository repository = new TrainerRepository();
    private final TrainerService service = new TrainerService(repository);

    @FXML
    private TextField idField;

    @FXML
    private PasswordField passwordField;

    @FXML
    public void login(ActionEvent event) throws IOException {
        service.login(idField, passwordField, event);
    }

    @FXML
    private void goBack(ActionEvent event) throws IOException {
        movePage(event, "/view/member/memberLogin");
    }
}
