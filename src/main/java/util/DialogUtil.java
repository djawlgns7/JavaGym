package util;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Optional;
import java.util.ResourceBundle;

import static thread.InactivityManager.registerDialog;
import static util.PageUtil.*;

public class DialogUtil {

    private static final ResourceBundle errorMessage = ResourceBundle.getBundle("message.error");
    private static final ResourceBundle basicMessage = ResourceBundle.getBundle("message.basic");

    public static void showDialog(String message) {
        Dialog<String> dialog = new Dialog<>();
        registerDialog(dialog);
        dialog.setTitle("알림");

        Label label = new Label(message);
        label.getStyleClass().add("label");

        VBox vbox = new VBox();
        vbox.getChildren().add(label);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(10);

        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getStylesheets().add(DialogUtil.class.getResource("/css/DialogPopUp.css").toExternalForm());

        dialog.showAndWait();
    }

    public static void showDialogErrorMessage(String messageCode) {
        Dialog<String> dialog = new Dialog<>();
        registerDialog(dialog);
        dialog.setTitle("알림");

        Label label = new Label(errorMessage.getString(messageCode));
        label.getStyleClass().add("label");

        VBox vbox = new VBox();
        vbox.getChildren().add(label);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(10);

        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getStylesheets().add(DialogUtil.class.getResource("/css/DialogPopUp.css").toExternalForm());

        dialog.showAndWait();
    }

    public static void showDialogBasicMessage(String messageCode) {
        Dialog<String> dialog = new Dialog<>();
        registerDialog(dialog);
        dialog.setTitle("알림");

        Label label = new Label(basicMessage.getString(messageCode));
        label.getStyleClass().add("label");

        VBox vbox = new VBox();
        vbox.getChildren().add(label);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(10);

        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getStylesheets().add(DialogUtil.class.getResource("/css/DialogPopUp.css").toExternalForm());

        dialog.showAndWait();
    }

    public static void showDialogAndMovePage(String message, String viewPath, ActionEvent event) throws IOException {
        Dialog<ButtonType> dialog = new Dialog<>();
        registerDialog(dialog);
        dialog.setTitle("알림");

        Label label = new Label(message);
        label.getStyleClass().add("label");

        VBox vbox = new VBox();
        vbox.getChildren().add(label);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(10);

        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getStylesheets().add(DialogUtil.class.getResource("/css/DialogPopUp.css").toExternalForm());

        dialog.showAndWait();
        movePage(event, viewPath);
    }

    public static void showDialogAndMovePageTimerOff(String message, String viewPath, ActionEvent event) throws IOException {
        Dialog<ButtonType> dialog = new Dialog<>();
        registerDialog(dialog);
        dialog.setTitle("알림");

        Label label = new Label(message);
        label.getStyleClass().add("label");

        VBox vbox = new VBox();
        vbox.getChildren().add(label);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(10);

        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getStylesheets().add(DialogUtil.class.getResource("/css/DialogPopUp.css").toExternalForm());

        dialog.showAndWait();
        movePageTimerOff(event, viewPath);
    }

    public static void showDialogAndMoveMainPage(String message, ActionEvent event) throws IOException {

        Dialog<ButtonType> dialog = new Dialog<>();
        registerDialog(dialog);
        dialog.setTitle("알림");

        Label label = new Label(message);
        label.getStyleClass().add("label");

        VBox vbox = new VBox();
        vbox.getChildren().add(label);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(10);

        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getStylesheets().add(DialogUtil.class.getResource("/css/DialogPopUp.css").toExternalForm());

        dialog.showAndWait();
        moveToMainPage(event);
    }

    public static Optional<ButtonType> showDialogChoose(String message) {
        Dialog<ButtonType> dialog = new Dialog<>();
        registerDialog(dialog);
        dialog.setTitle("알림");

        dialog.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setContent(new Label(message));

        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.NO);
        dialog.getDialogPane().getStylesheets().add(DialogUtil.class.getResource("/css/DialogPopUp_2Btn.css").toExternalForm());

        return dialog.showAndWait();
    }
}