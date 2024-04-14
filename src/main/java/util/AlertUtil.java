package util;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class AlertUtil {

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

        /* Alert CSS 적용 */
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
                AlertUtil.class.getResource("/css/AlertPopUp.css").toExternalForm()
        );

        /* Content Text */
        for (Node node : dialogPane.lookupAll(".content")) {
            if (node instanceof Label) {
                node.getStyleClass().add("alert_ContentText");
            }
        }

        /* Button */
        ButtonType checkButtonType = new ButtonType("확인", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(checkButtonType);

        Button checkButton = (Button) alert.getDialogPane().lookupButton(checkButtonType);
        checkButton.getStyleClass().add("alert_Button");

        /* Execution Logo Icon */
        Stage dialogStage = (Stage) alert.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new Image(AlertUtil.class.getResourceAsStream("/image/JavaGym_Logo.jpeg")));

        alert.showAndWait();
    }

    public static void showAlertLoginFail(String messageCode) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(errorMessage.getString("loginFail"));
        alert.setHeaderText(null);
        alert.setContentText(errorMessage.getString(messageCode));

        /* Alert CSS 적용 */
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
                AlertUtil.class.getResource("/css/AlertPopUp.css").toExternalForm()
        );

        /* Content Text */
        for (Node node : dialogPane.lookupAll(".content")) {
            if (node instanceof Label) {
                node.getStyleClass().add("alert_ContentText");
            }
        }

        /* Button */
        ButtonType checkButtonType = new ButtonType("확인", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(checkButtonType);

        Button checkButton = (Button) alert.getDialogPane().lookupButton(checkButtonType);
        checkButton.getStyleClass().add("alert_Button");

        /* Execution Logo Icon */
        Stage dialogStage = (Stage) alert.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new Image(AlertUtil.class.getResourceAsStream("/image/JavaGym_Logo.jpeg")));

        alert.showAndWait();
    }

    public static void showAlertSignUpFail(String messageCode) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(errorMessage.getString("signUpFail"));
        alert.setHeaderText(null);
        alert.setContentText(errorMessage.getString(messageCode));

        /* Alert CSS 적용 */
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
                AlertUtil.class.getResource("/css/AlertPopUp.css").toExternalForm()
        );

        /* Content Text */
        for (Node node : dialogPane.lookupAll(".content")) {
            if (node instanceof Label) {
                node.getStyleClass().add("alert_ContentText");
            }
        }

        /* Button */
        ButtonType checkButtonType = new ButtonType("확인", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(checkButtonType);

        Button checkButton = (Button) alert.getDialogPane().lookupButton(checkButtonType);
        checkButton.getStyleClass().add("alert_Button");

        /* Execution Logo Icon */
        Stage dialogStage = (Stage) alert.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new Image(AlertUtil.class.getResourceAsStream("/image/JavaGym_Logo.jpeg")));

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
}