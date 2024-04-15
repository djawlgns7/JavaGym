package controller.trainer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

import java.io.IOException;

import static util.PageUtil.movePageCenter;

public class HelloTrainerController {

    @FXML
    private ImageView profileImage;

    @FXML
    public void reservationInfo(ActionEvent event) throws IOException {
        movePageCenter(event, "/view/trainer/reservationInfo");
    }

    @FXML
    private void logout(ActionEvent event) throws IOException {
        movePageCenter(event, "/view/member/memberLogin");
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
