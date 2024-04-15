package util;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Optional;
import java.util.ResourceBundle;

import static util.PageUtil.*;

public class DialogUtil {

    private static final ResourceBundle errorMessage = ResourceBundle.getBundle("message.error");
    private static final ResourceBundle basicMessage = ResourceBundle.getBundle("message.basic");

    public static void showDialog(String message) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("알림");

        VBox vbox = new VBox();
        vbox.getChildren().add(new Label(message));
        dialog.getDialogPane().setContent(vbox);

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

        dialog.showAndWait();
    }

    public static void showDialogErrorMessage(String messageCode) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("알림");

        VBox vbox = new VBox();
        vbox.getChildren().add(new Label(errorMessage.getString(messageCode)));
        dialog.getDialogPane().setContent(vbox);

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

        dialog.showAndWait();
    }

    public static void showDialogBasicMessage(String messageCode) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("알림");

        VBox vbox = new VBox();
        vbox.getChildren().add(new Label(basicMessage.getString(messageCode)));
        dialog.getDialogPane().setContent(vbox);

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

        dialog.showAndWait();
    }

    public static void showDialogAndMovePage(String message, String viewPath, ActionEvent event) throws IOException {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("알림");

        VBox vbox = new VBox();
        vbox.getChildren().add(new Label(message));
        dialog.getDialogPane().setContent(vbox);

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

        dialog.showAndWait();

        movePage(event, viewPath);
    }

    public static Optional<ButtonType> showDialogChoose(String message) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("알림");

        dialog.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setContent(new Label(message));

        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.NO);

        return dialog.showAndWait();
    }
}