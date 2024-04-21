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
import javafx.scene.input.ScrollEvent;
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
import static domain.admin.SelectedAdmin.loginAdmin;
import static domain.member.SelectedMember.loginMember;
import static domain.trainer.SelectedTrainer.loginTrainer;

public class InactivityManager {
    private static Stage mainStage;
    public static Timeline inactivityTimer;
    private static List<Dialog> openDialogs = new ArrayList<>();
    private static Scene timerScene = null;
    private static int closeDialogTimer = 0;

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

        KeyFrame alertFrame = new KeyFrame(Duration.seconds(10), e -> setUpInactivitySound());
        KeyFrame DialogFrame = new KeyFrame(Duration.seconds(10), e -> openTimerDialog());
        KeyFrame endFrame = new KeyFrame(Duration.seconds(20), e -> moveToMainScreen());

        inactivityTimer = new Timeline(alertFrame, endFrame, DialogFrame);
        inactivityTimer.setCycleCount(Timeline.INDEFINITE);

        scene.addEventFilter(MouseEvent.MOUSE_MOVED, e -> resetInactivityTimer());
        scene.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> resetInactivityTimer());
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> resetInactivityTimer());
        scene.addEventFilter(InputMethodEvent.INPUT_METHOD_TEXT_CHANGED, e -> resetInactivityTimer());
        scene.addEventFilter(ScrollEvent.SCROLL, e -> resetInactivityTimer()); // 마우스 스크롤 이벤트 추가 (성진)

        inactivityTimer.play();
    }

    public static void resetInactivityTimer() {
        if (inactivityTimer != null) {
            inactivityTimer.stop();
            inactivityTimer.play();
        }

        if(timerScene != null){
            closeDialog(timerScene, closeDialogTimer);
            timerScene = null;
        }
    }

    private static void moveToMainScreen() {

        if (loginAdmin != null || loginTrainer != null || loginMember == null) {
            return;
        }

        Platform.runLater(() -> {
            try {
                clearBasket();
                closeAllDialogs();
                loginMember = null;
                loginTrainer = null;

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

    public static void closeDialog(Scene scene, int index) {
        if(index == 0){
            return;
        }
        Stage stage = (Stage) scene.getWindow();
        SoundUtil.stop();
        stage.close();
    }

    public static void openTimerDialog() {

        if (loginAdmin != null || loginTrainer != null || loginMember == null) {
            return;
        }

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

            Platform.runLater(() -> {
                dialogStage.show();
            });

            setTimerSceneEvent();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setTimerSceneEvent(){
        closeDialogTimer = 0;
        timerScene.setOnMouseMoved(e -> closeDialog(timerScene, closeDialogTimer));
        timerScene.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> closeDialog(timerScene, closeDialogTimer));
        timerScene.addEventFilter(KeyEvent.KEY_PRESSED, e -> closeDialog(timerScene, closeDialogTimer));
        timerScene.addEventFilter(InputMethodEvent.INPUT_METHOD_TEXT_CHANGED, e -> closeDialog(timerScene, closeDialogTimer));
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            closeDialogTimer = 1;
        });
        thread.start();
    }

    private static void setUpInactivitySound() {
        if (loginAdmin != null || loginTrainer != null || loginMember == null) {
            return;
        }

        SoundUtil.play("toMainPage");
    }
}