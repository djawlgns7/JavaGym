package thread;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import util.AlertUtil;
import util.ControllerUtil;
import util.SoundUtil;

import java.io.IOException;
import java.net.URL;

import static util.AlertUtil.openCustomDialog;

public class InactivityManager {
    private static Stage mainStage;
    private static Timeline inactivityTimer;

    private static Stage dialogStage = null;

    public static void setMainStage(Stage stage) {
        mainStage = stage;
    }

    public static void setupInactivityTimer(Scene scene) {
        if (inactivityTimer != null) {
            // 이미 실행 중인 타이머가 있다면 중지
            inactivityTimer.stop();
        }

        KeyFrame alertFrame = new KeyFrame(Duration.seconds(10), e -> SoundUtil.play("toMainPage"));
        KeyFrame alertDialogFrame = new KeyFrame(Duration.seconds(10), e -> openCustomDialog());
        KeyFrame endFrame = new KeyFrame(Duration.seconds(20), e -> moveToMainScreen());

        inactivityTimer = new Timeline(alertFrame, alertDialogFrame, endFrame);
        inactivityTimer.setCycleCount(Timeline.INDEFINITE);

        // 마우스 움직임과 키 이벤트를 감지하도록 이벤트 리스너를 추가
        scene.setOnMouseMoved(e -> resetInactivityTimer());
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> resetInactivityTimer());
        scene.addEventFilter(InputMethodEvent.INPUT_METHOD_TEXT_CHANGED, e -> resetInactivityTimer());

        inactivityTimer.play();
    }

    public static void openCustomDialog() {
        try {
            URL url = ControllerUtil.class.getResource("/view/dialogTest.fxml");
            // 컨트롤러 로드
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            // 새로운 Stage 생성
            dialogStage = new Stage();
            Scene newScene = new Scene(root);
            dialogStage.setScene(newScene);
            dialogStage.setTitle("Custom Dialog");
            Platform.runLater(() -> {
                dialogStage.showAndWait();
            });

//            newScene.setOnMouseMoved(e -> resetInactivityTimer());
//            newScene.addEventFilter(KeyEvent.KEY_PRESSED, e -> resetInactivityTimer());
//            newScene.addEventFilter(InputMethodEvent.INPUT_METHOD_TEXT_CHANGED, e -> resetInactivityTimer());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void resetInactivityTimer() {
        if (inactivityTimer != null) {
            inactivityTimer.stop();
            inactivityTimer.play();
        }

        if(dialogStage != null){
            dialogStage.close();
            dialogStage = null;
        }
    }

    private static void moveToMainScreen() {
        Platform.runLater(() -> {
            try {
                Parent root = FXMLLoader.load(InactivityManager.class.getResource("/view/member/memberLogin.fxml"));
                mainStage.setScene(new Scene(root));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}