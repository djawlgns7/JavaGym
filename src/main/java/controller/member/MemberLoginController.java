package controller.member;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import repository.MemberRepository;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import service.MemberService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static util.AlertUtil.*;
import static util.PageUtil.*;

public class MemberLoginController implements Initializable {

    @FXML
    private TextField phoneField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ImageView profileImage;

    private final MemberRepository memberRepository = new MemberRepository();
    private final MemberService service = new MemberService(memberRepository);

    @FXML
    private void login(ActionEvent event) throws IOException {
        service.login(phoneField, passwordField, event);
    }

    @FXML
    private void showSignUp(ActionEvent event) throws IOException {
        movePageCenter(event, "/view/member/signUpForm");
    }

    @FXML
    public void showAdminLogin(ActionEvent event) throws IOException {
        movePageCenter(event, "/view/admin/adminLogin");
    }

    @FXML
    public void showTrainerLogin(ActionEvent event) throws IOException {
        movePageCenter(event, "/view/trainer/trainerLogin");
    }

    @FXML
    public void immediateEntry(ActionEvent event) throws IOException {
        service.immediateEntry(phoneField, passwordField, event);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        TextFormatter<String> phoneFormatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d{0,8}")) {
                return change;
            }
            return null;
        });

        TextFormatter<String> passwordFormatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d{0,4}")) {
                return change; // 변경을 허용
            }
            return null; // 변경을 무시
        });
        phoneField.setTextFormatter(phoneFormatter);
        passwordField.setTextFormatter(passwordFormatter);

        Image image = new Image("/image/JavaGym_Logo.jpeg");
        profileImage.setImage(image);

        Circle cilpCircle = new Circle(100, 100, 100);
        profileImage.setClip(cilpCircle);
    }
}