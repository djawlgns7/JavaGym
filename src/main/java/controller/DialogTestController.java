package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class DialogTestController implements Initializable {

    @FXML
    private Label timer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setTimer();
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

    public void closeDialog() {
        Stage stage = (Stage) timer.getScene().getWindow();
        stage.close();
    }
}
