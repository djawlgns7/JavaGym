package controller.dialog;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import util.DialogUtil;

import java.net.URL;
import java.util.ResourceBundle;

public class TimerDialogController implements Initializable {
    @FXML
    private Label timer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setTimer();
        setIcon();
    }

    public void setTimer(){
        Thread thread = new Thread(() -> {
            for(int i = 10; i > 0; i--) {
                int finalI = i;
                Platform.runLater(() -> {
                    timer.setText(finalI + "초 남음");
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            Platform.runLater(() -> {
                closeDialog();
            });
        });

        thread.start();
    }

    public void setIcon() {
        Platform.runLater(() -> {
            Stage stage = (Stage) timer.getScene().getWindow();
            // 이미지 파일의 경로는 프로젝트 구조에 따라 다를 수 있습니다.
            Image icon = new Image(getClass().getResourceAsStream("/image/JavaGym_Logo.jpeg"));
            stage.getIcons().add(icon);
        });
    }

    public void closeDialog() {
        Stage stage = (Stage) timer.getScene().getWindow();
        stage.close();
    }
}
