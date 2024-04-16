package thread;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import util.ControllerUtil;
import util.SoundUtil;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static controller.payment.PaymentController.basket;

public class InactivityManager {
    private static Stage mainStage;
    public static Timeline inactivityTimer;
    private static List<Dialog> openDialogs = new ArrayList<>();
    private static Scene timerScene = null;

    public static void setMainStage(Stage stage) {
        mainStage = stage;
    }

    public static void registerDialog(Dialog dialog) {
        openDialogs.add(dialog);

        DialogPane dialogPane = dialog.getDialogPane();

        dialogPane.addEventFilter(MouseEvent.MOUSE_MOVED, e -> resetInactivityTimer());
        dialogPane.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> resetInactivityTimer());
        dialogPane.addEventFilter(KeyEvent.KEY_PRESSED, e -> resetInactivityTimer());
        dialogPane.addEventFilter(InputMethodEvent.INPUT_METHOD_TEXT_CHANGED, e -> resetInactivityTimer());

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
            inactivityTimer.stop();
        }

        if (timerScene != null){
            timerScene = null;
        }

        KeyFrame alertFrame = new KeyFrame(Duration.seconds(10), e -> SoundUtil.play("toMainPage"));
        KeyFrame DialogFrame = new KeyFrame(Duration.seconds(10), e -> openTimerDialog());
        KeyFrame endFrame = new KeyFrame(Duration.seconds(20), e -> moveToMainScreen());

        inactivityTimer = new Timeline(alertFrame, endFrame, DialogFrame);
        inactivityTimer.setCycleCount(Timeline.INDEFINITE);

        scene.addEventFilter(MouseEvent.MOUSE_MOVED, e -> resetInactivityTimer());
        scene.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> resetInactivityTimer());
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> resetInactivityTimer());
        scene.addEventFilter(InputMethodEvent.INPUT_METHOD_TEXT_CHANGED, e -> resetInactivityTimer());

        inactivityTimer.play();
    }

    public static void resetInactivityTimer() {
        if (inactivityTimer != null) {
            inactivityTimer.stop();
            inactivityTimer.play();
        }

        if(timerScene != null){
            closeDialog(timerScene);
            timerScene = null;
        }
    }

    private static void moveToMainScreen() {
        Platform.runLater(() -> {
            try {
                clearBasket();
                closeAllDialogs();
                inactivityTimer.stop();
                timerScene = null;
                Parent root = FXMLLoader.load(InactivityManager.class.getResource("/view/member/memberLogin.fxml"));

                Scene scene = new Scene(root);
                mainStage.setScene(scene);
                mainStage.sizeToScene();

                Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                mainStage.setX((screenBounds.getWidth() - mainStage.getWidth()) / 2);
                mainStage.setY((screenBounds.getHeight() - mainStage.getHeight()) / 2);

                mainStage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void clearBasket(){
        if (!basket.isEmpty()){
            basket.clear();
        }
    }

    public static void closeDialog(Scene scene) {
        Stage stage = (Stage) scene.getWindow();
        stage.close();
    }

    public static void openTimerDialog() {
        try {
            URL url = ControllerUtil.class.getResource("/view/dialog/timerDialog.fxml");
            // 컨트롤러 로드
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load(url);

            // 새로운 Stage 생성
            Stage dialogStage = new Stage();
            timerScene = new Scene(root);
            dialogStage.setScene(timerScene);
            dialogStage.setTitle("자동 로그아웃 알림");
            timerScene.setOnMouseMoved(e -> closeDialog(timerScene));
            timerScene.addEventFilter(KeyEvent.KEY_PRESSED, e -> closeDialog(timerScene));
            timerScene.addEventFilter(InputMethodEvent.INPUT_METHOD_TEXT_CHANGED, e -> closeDialog(timerScene));
            Platform.runLater(() -> {
                dialogStage.show();
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}