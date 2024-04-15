package thread;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import util.SoundUtil;

import java.util.ArrayList;
import java.util.List;

public class InactivityManager {
    private static Stage mainStage;
    private static Timeline inactivityTimer;
    private static List<Dialog> openDialogs = new ArrayList<>();  // 다이얼로그 리스트

    private static Stage dialogStage = null;

    public static void setMainStage(Stage stage) {
        mainStage = stage;
    }

    public static void registerDialog(Dialog dialog) {
        openDialogs.add(dialog);
        dialog.setOnCloseRequest(e -> openDialogs.remove(dialog));
    }

    public static void closeAllDialogs() {
        for (Dialog dialog : new ArrayList<>(openDialogs)) {
            dialog.close();
        }
        openDialogs.clear();
    }

    public static void setupInactivityTimer(Scene scene) {
        if (inactivityTimer != null) {
            // 이미 실행 중인 타이머가 있다면 중지
            inactivityTimer.stop();
        }

        KeyFrame alertFrame = new KeyFrame(Duration.seconds(5), e -> SoundUtil.play("toMainPage"));
        KeyFrame endFrame = new KeyFrame(Duration.seconds(10), e -> moveToMainScreen());

        inactivityTimer = new Timeline(alertFrame, endFrame);
        inactivityTimer.setCycleCount(Timeline.INDEFINITE);

        // 마우스 움직임과 키 이벤트를 감지하도록 이벤트 리스너를 추가
        scene.setOnMouseMoved(e -> resetInactivityTimer());
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> resetInactivityTimer());
        scene.addEventFilter(InputMethodEvent.INPUT_METHOD_TEXT_CHANGED, e -> resetInactivityTimer());

        inactivityTimer.play();
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
                closeAllDialogs();
                inactivityTimer.stop();
                Parent root = FXMLLoader.load(InactivityManager.class.getResource("/view/member/memberLogin.fxml"));
                mainStage.setScene(new Scene(root));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}