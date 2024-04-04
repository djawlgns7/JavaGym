package controller.admin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import repository.AdminRepository;
import service.AdminService;

import java.io.IOException;

import static util.PageUtil.*;

public class AdminLoginController {

    private final AdminRepository repository = new AdminRepository();
    private final AdminService service = new AdminService(repository);

    @FXML
    private TextField id;

    @FXML
    private PasswordField password;

    @FXML
    public void login(ActionEvent event) throws IOException {
        String id = this.id.getText();
        String password = this.password.getText();

        service.login(id, password, event);
    }

    @FXML
    private void goBack(ActionEvent event) throws IOException {
        movePage(event, "Login", "/view/member/memberLogin");
    }
}