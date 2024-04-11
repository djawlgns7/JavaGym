package util;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class AlertUtil {

    private static final ResourceBundle messages = ResourceBundle.getBundle("message.error");

    public static void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("알림");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showAlertUseMessage(String titleCode, String messageCode, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(messages.getString(titleCode));
        alert.setHeaderText(null);
        alert.setContentText(messages.getString(messageCode));
        alert.showAndWait();
    }

    public static void showAlertLoginFail(String messageCode) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(messages.getString("loginFail"));
        alert.setHeaderText(null);
        alert.setContentText(messages.getString(messageCode));
        alert.showAndWait();
    }

    public static void showAlertSignUpFail(String messageCode) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(messages.getString("signUpFail"));
        alert.setHeaderText(null);
        alert.setContentText(messages.getString(messageCode));
        alert.showAndWait();
    }

    public static void showAlertAddMemberFail(String messageCode) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(messages.getString("addMemberFail"));
        alert.setHeaderText(null);
        alert.setContentText(messages.getString(messageCode));
        alert.showAndWait();
    }

    public static void showAlertAddTrainerFail(String messageCode) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(messages.getString("addTrainerFail"));
        alert.setHeaderText(null);
        alert.setContentText(messages.getString(messageCode));
        alert.showAndWait();
    }

    public static void showAlertUpdateMemberFail(String messageCode) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(messages.getString("updateMemberFail"));
        alert.setHeaderText(null);
        alert.setContentText(messages.getString(messageCode));
        alert.showAndWait();
    }

    public static void showAlertUpdateTrainerFail(String messageCode) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(messages.getString("updateTrainerFail"));
        alert.setHeaderText(null);
        alert.setContentText(messages.getString(messageCode));
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
    public static void showAlertAddScheduleFail(String messageCode) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(messages.getString("addScheduleFail"));
        alert.setHeaderText(null);
        alert.setContentText(messages.getString(messageCode));
        alert.showAndWait();
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
}