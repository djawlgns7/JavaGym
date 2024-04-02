package controller.admin;

import domain.Admin;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import repository.AdminRepository;
import service.AdminService;

import static service.MyUtils.*;

import java.io.IOException;

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

        if (id.isEmpty()) {
            showAlert("로그인 실패", "아이디를 입력하세요.", Alert.AlertType.WARNING);
            return;
        }

        if (password.isEmpty()) {
            showAlert("로그인 실패", "비밀번호를 입력하세요.", Alert.AlertType.WARNING);
            return;
        }

        Admin admin = service.login(id, password);

        if (admin != null) {
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/view/helloAdmin.fxml"));
            Scene scene = new Scene(loginRoot);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Admin");

            stage.setScene(scene);
            stage.show();
        } else {
            showAlert("로그인 실패", "아이디 또는 비밀번호가 맞지 않습니다.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void goBack(ActionEvent event) throws IOException {
        Parent loginRoot = FXMLLoader.load(getClass().getResource("/view/memberLogin.fxml"));
        Scene scene = new Scene(loginRoot);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Login");

        stage.setScene(scene);
        stage.show();
    }
}