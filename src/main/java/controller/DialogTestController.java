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

    private Scene scene;

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

    public void setEvent(){
        scene.setOnMouseMoved(e -> closeDialog());
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> closeDialog());
        scene.addEventFilter(InputMethodEvent.INPUT_METHOD_TEXT_CHANGED, e -> closeDialog());
    }
}

//public static void openCustomDialog() {
//    try {
//        URL url = ControllerUtil.class.getResource("/view/dialogTest.fxml");
//        // 컨트롤러 로드
//        FXMLLoader loader = new FXMLLoader(url);
//        Parent root = loader.load(url);
//
//        // 새로운 Stage 생성
//        Stage dialogStage = new Stage();
//        Scene newScene = new Scene(root);
//        dialogStage.setScene(newScene);
//        dialogStage.setTitle("Custom Dialog");
//        SelectedMember.timerScene = newScene;
//        if(SelectedMember.timerScene == null)
//            System.out.println("널이다히히");
//        Platform.runLater(() -> {
//            dialogStage.show();
//        });
//
//
//    } catch (IOException e) {
//        e.printStackTrace();
//    }
//}
