package controller.trainer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import repository.TrainerRepository;
import service.TrainerService;

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
    private ImageView profileImage;

    @FXML
    public void login(ActionEvent event) throws IOException {
        service.login(idField, passwordField, event);
    }

    @FXML
    private void goBack(ActionEvent event) throws IOException {
        moveToMainPage(event);
    }

    @FXML
    public void initialize() {
        // 이미지 로드 설정
        Image image = new Image("/image/JavaGym_Logo.jpeg");
        profileImage.setImage(image);

        // 원형 클리핑 설정
        Circle clipCircle = new Circle(100, 100, 100); // ImageView 중앙에 위치하고 반지름 100인 원
        profileImage.setClip(clipCircle);
    }
}
