package controller.admin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import repository.AdminRepository;
import service.admin.AdminService;

import java.io.IOException;

import static util.PageUtil.*;

public class AdminLoginController {

    private final AdminRepository repository = new AdminRepository();
    private final AdminService service = new AdminService(repository);

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
        movePage(event, "Login", "/view/member/memberLogin");
    }
}