package controller.trainer;

import domain.trainer.SelectedTrainer;
import domain.trainer.Trainer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import repository.TrainerRepository;

import java.io.IOException;

import static util.PageUtil.*;


public class HelloTrainerController {

    @FXML
    private ImageView profileImage;

    private TrainerRepository trainerRepository = new TrainerRepository();
    @FXML
    public void reservationInfo(ActionEvent event) throws IOException {
        if (SelectedTrainer.currentTrainer != null && SelectedTrainer.currentTrainer.getId() != null) {
            Trainer updatedTrainer = trainerRepository.findById(SelectedTrainer.currentTrainer.getId());
            if (updatedTrainer != null) {
                SelectedTrainer.setCurrentTrainer(updatedTrainer); // 갱신된 트레이너 정보로 업데이트
                movePageTimerOff(event, "/view/trainer/reservationInfo");
            }
        }
    }

    @FXML
    private void logout(ActionEvent event) throws IOException {
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
