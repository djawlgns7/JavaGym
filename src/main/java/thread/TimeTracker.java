package thread;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

public class TimeTracker {

    private Thread idleThread;
    private final long idleLimit = 10 * 1000;
    private Stage primaryStage;
    private Alert currentAlert = null;

    public TimeTracker(Stage stage) {
        primaryStage = stage;
        initializeIdleTimeThread();
    }

    public void setCurrentAlert(Alert alert) {
        currentAlert = alert;
    }

    private void closeCurrentAlert() {
        if (currentAlert != null) {
            currentAlert.close();
            currentAlert = null;
        }
    }

    private void initializeIdleTimeThread() {
        idleThread = new Thread(() -> {
            try {
                Thread.sleep(idleLimit);
                Platform.runLater(() -> {
                    closeCurrentAlert(); // 알림창 닫기
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/member/memberLogin.fxml"));
                        Parent root = loader.load();
                        Scene scene = new Scene(root);
                        Stage currentStage = (Stage) currentAlert.getDialogPane().getScene().getWindow();
                        currentStage.setScene(scene);
                        currentStage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } catch (InterruptedException e) {
                // 쓰레드가 인터럽트되면 재시작
                initializeIdleTimeThread();
            }
        });
        idleThread.setDaemon(true);
        idleThread.start();
    }

    public synchronized void resetIdleTime() {
        if (idleThread != null) {
            idleThread.interrupt();
        }
    }
}