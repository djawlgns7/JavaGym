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
import util.SoundUtil;

import java.util.ArrayList;
import java.util.List;

public class InactivityManager {
    private static Stage mainStage;
    public static Timeline inactivityTimer;
    private static List<Dialog> openDialogs = new ArrayList<>();

    public static void setMainStage(Stage stage) {
        mainStage = stage;
    }

    public static void registerDialog(Dialog dialog) {
        openDialogs.add(dialog);

        DialogPane dialogPane = dialog.getDialogPane();

        dialogPane.addEventFilter(MouseEvent.MOUSE_MOVED, e -> resetInactivityTimer());
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

        KeyFrame alertFrame = new KeyFrame(Duration.seconds(5), e -> SoundUtil.play("toMainPage"));
        KeyFrame endFrame = new KeyFrame(Duration.seconds(10), e -> moveToMainScreen());

        inactivityTimer = new Timeline(alertFrame, endFrame);
        inactivityTimer.setCycleCount(Timeline.INDEFINITE);

        scene.addEventFilter(MouseEvent.MOUSE_MOVED, e -> resetInactivityTimer());
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> resetInactivityTimer());
        scene.addEventFilter(InputMethodEvent.INPUT_METHOD_TEXT_CHANGED, e -> resetInactivityTimer());

        inactivityTimer.play();
    }

    public static void resetInactivityTimer() {
        if (inactivityTimer != null) {
            inactivityTimer.stop();
            inactivityTimer.play();
        }
    }

    private static void moveToMainScreen() {
        Platform.runLater(() -> {
            try {
                closeAllDialogs();
                inactivityTimer.stop();
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
}