package controller.trainer;

import javafx.fxml.FXML;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import repository.TrainerRepository;

import java.awt.*;
import java.io.File;

public class InputPhotoTestController {
    private final TrainerRepository trainerRepository = new TrainerRepository();

    @FXML
    private ImageView imageView;

    @FXML
    private void selectPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("이미지 파일 선택");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("이미지 파일", "*.png", "*.jpg", "*.gif", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(imageView.getScene().getWindow());
        if (file != null) {
            trainerRepository.savePhoto(9000, file);
        }
    }

    @FXML
    public void setPhoto(){
        Image image = trainerRepository.getImage(9000);
        imageView.setImage(image);
    }
}
