package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class DialogTestController implements Initializable {

    @FXML
    private Label asdf;
    @FXML
    private AnchorPane window;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Thread thread = new Thread(() -> {
            for(int i = 5; i > 0; i--) {
                int finalI = i;
                Platform.runLater(() -> {
                    asdf.setText(finalI + "초 남음");
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1), event -> {
                closeDialog(); // 다이얼로그를 닫는 메소드 호출
            }));
            timeline.setCycleCount(1); // 타이머를 한 번만 실행하도록 설정
            timeline.play();
        });

        thread.start();
    }

    public void closeDialog() {
        Stage stage = (Stage) window.getScene().getWindow();
        // Stage를 닫음
        stage.close();
    }
}
