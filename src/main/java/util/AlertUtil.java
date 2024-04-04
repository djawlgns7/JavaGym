package util;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class AlertUtil {

    private static final ResourceBundle messages = ResourceBundle.getBundle("message.error");

    public static void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
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

    public static void showAlertAndMove(String title, String message, Alert.AlertType type, String viewPath, ActionEvent event) throws IOException {
        Alert alert = new Alert(type);
        alert.setTitle(title);
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
}