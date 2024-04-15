package util;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class AlertUtil extends  Thread {

    private static final ResourceBundle errorMessage = ResourceBundle.getBundle("message.error");
    private static final ResourceBundle basicMessage = ResourceBundle.getBundle("message.basic");

    public static void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("알림");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showAlertUseMessage(String messageCode) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("알림");
        alert.setHeaderText(null);
        alert.setContentText(basicMessage.getString(messageCode));
        alert.showAndWait();
    }

    public static void showAlertLoginFail(String messageCode) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(errorMessage.getString("loginFail"));
        alert.setHeaderText(null);
        alert.setContentText(errorMessage.getString(messageCode));
        alert.showAndWait();
    }

    public static void showAlertSignUpFail(String messageCode) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(errorMessage.getString("signUpFail"));
        alert.setHeaderText(null);
        alert.setContentText(errorMessage.getString(messageCode));
        alert.showAndWait();
    }

    public static void showAlertAddMemberFail(String messageCode) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(errorMessage.getString("addMemberFail"));
        alert.setHeaderText(null);
        alert.setContentText(errorMessage.getString(messageCode));
        alert.showAndWait();
    }

    public static void showAlertAddTrainerFail(String messageCode) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(errorMessage.getString("addTrainerFail"));
        alert.setHeaderText(null);
        alert.setContentText(errorMessage.getString(messageCode));
        alert.showAndWait();
    }

    public static void showAlertUpdateMemberFail(String messageCode) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(errorMessage.getString("updateMemberFail"));
        alert.setHeaderText(null);
        alert.setContentText(errorMessage.getString(messageCode));
        alert.showAndWait();
    }

    public static void showAlertUpdateTrainerFail(String messageCode) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(errorMessage.getString("updateTrainerFail"));
        alert.setHeaderText(null);
        alert.setContentText(errorMessage.getString(messageCode));
        alert.showAndWait();
    }

    public static void showAlertAndMove(String message, Alert.AlertType type, String viewPath, ActionEvent event) throws IOException {
        Alert alert = new Alert(type);
        alert.setTitle("알림");
        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            URL url = ControllerUtil.class.getResource(viewPath + ".fxml");
            Parent newRoot = FXMLLoader.load(url);
            Scene newScene = new Scene(newRoot);
            Stage newStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            newStage.setScene(newScene);
            newStage.show();
        }
    }

    public static void showAlertAndMoveCenter(String message, Alert.AlertType type, String viewPath, ActionEvent event) throws IOException {
        Alert alert = new Alert(type);
        alert.setTitle("알림");
        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            URL url = ControllerUtil.class.getResource(viewPath + ".fxml");
            Parent newRoot = FXMLLoader.load(url);
            Scene newScene = new Scene(newRoot);
            Stage newStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            newStage.setScene(newScene);

            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

            newStage.setX((screenBounds.getWidth() - newStage.getWidth()) / 2);
            newStage.setY((screenBounds.getHeight() - newStage.getHeight()) / 2);
            newStage.show();
        }
    }

    public static Optional<ButtonType> showAlertChoose(String message) {
         Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("알림");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText(message);
        return confirmationAlert.showAndWait();
    }

    public static void showAlertAddReservationFail (String messageCode){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(errorMessage.getString("loginFail"));
        alert.setHeaderText(null);
        alert.setContentText(errorMessage.getString(messageCode));
        alert.showAndWait();
    }

    public static void showAlertAndAutoClose(String message, int seconds) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("알림");
        alert.setHeaderText(null);
        alert.setContentText(message);

        // 일정 시간 후에 자동으로 닫히도록 설정
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(seconds), event -> alert.close()));
        timeline.setCycleCount(1);
        timeline.play();

        // 알림 창 표시
        alert.showAndWait();
    }

    public static void openCustomDialog(String position) {
        try {
            URL url = AlertUtil.class.getResource(position);
            // 컨트롤러 로드
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            // 새로운 Stage 생성
            Stage dialogStage = new Stage();
            dialogStage.setScene(new Scene(root));
            dialogStage.setTitle("Custom Dialog");
            dialogStage.showAndWait(); // 다이얼로그를 표시하고 대기
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}