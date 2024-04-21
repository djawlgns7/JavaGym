package util;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;
import java.util.ResourceBundle;

import static thread.InactivityManager.registerDialog;
import static util.AnimationUtil.applyFadeInDialog;
import static util.PageUtil.*;

public class DialogUtil {

    private static final ResourceBundle errorMessage = ResourceBundle.getBundle("message.error");
    private static final ResourceBundle basicMessage = ResourceBundle.getBundle("message.basic");

    public static void showDialogErrorMessage(String messageCode) {
        Dialog<String> dialog = new Dialog<>();
        registerDialog(dialog);
        dialog.setTitle(basicMessage.getString("alert"));

        Label label = new Label(errorMessage.getString(messageCode));
        label.getStyleClass().add("BasicLabel");

        VBox vbox = new VBox();
        vbox.getChildren().add(label);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(10);

        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getStylesheets().add(DialogUtil.class.getResource("/css/DialogPopUp.css").toExternalForm());

        applyFadeInDialog(vbox);

        Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new Image(DialogUtil.class.getResourceAsStream("/image/JavaGym_Logo.jpeg")));

        dialog.showAndWait();
    }

    public static void showDialogBasicMessage(String messageCode) {
        Dialog<String> dialog = new Dialog<>();
        registerDialog(dialog);
        dialog.setTitle(basicMessage.getString("alert"));

        Label label = new Label(basicMessage.getString(messageCode));
        label.getStyleClass().add("BasicLabel");

        VBox vbox = new VBox();
        vbox.getChildren().add(label);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(10);

        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getStylesheets().add(DialogUtil.class.getResource("/css/DialogPopUp.css").toExternalForm());

        applyFadeInDialog(vbox);

        Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new Image(DialogUtil.class.getResourceAsStream("/image/JavaGym_Logo.jpeg")));

        dialog.showAndWait();
    }

    public static void showDialogAndMovePage(String messageCode, String viewPath, ActionEvent event) throws IOException {
        Dialog<ButtonType> dialog = new Dialog<>();
        registerDialog(dialog);
        dialog.setTitle(basicMessage.getString("alert"));

        Label label = new Label(basicMessage.getString(messageCode));
        label.getStyleClass().add("BasicLabel");

        VBox vbox = new VBox();
        vbox.getChildren().add(label);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(10);

        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getStylesheets().add(DialogUtil.class.getResource("/css/DialogPopUp.css").toExternalForm());

        applyFadeInDialog(vbox);

        Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new Image(DialogUtil.class.getResourceAsStream("/image/JavaGym_Logo.jpeg")));

        dialog.showAndWait();
        movePage(event, viewPath);
    }

    public static void showDialogAndMoveMainPage(String message, ActionEvent event) throws IOException {

        Dialog<ButtonType> dialog = new Dialog<>();
        registerDialog(dialog);
        dialog.setTitle(basicMessage.getString("alert"));

        Label label = new Label(message);
        label.getStyleClass().add("BasicLabel");

        VBox vbox = new VBox();
        vbox.getChildren().add(label);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(10);

        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getStylesheets().add(DialogUtil.class.getResource("/css/DialogPopUp.css").toExternalForm());

        applyFadeInDialog(vbox);

        Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new Image(DialogUtil.class.getResourceAsStream("/image/JavaGym_Logo.jpeg")));

        dialog.showAndWait();
        moveToMainPage(event);
    }

    public static void showDialogAndMoveMainPageMessage(String messageCode, ActionEvent event) throws IOException {

        Dialog<ButtonType> dialog = new Dialog<>();
        registerDialog(dialog);
        dialog.setTitle(basicMessage.getString("alert"));

        Label label = new Label(basicMessage.getString(messageCode));
        label.getStyleClass().add("BasicLabel");

        VBox vbox = new VBox();
        vbox.getChildren().add(label);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(10);

        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getStylesheets().add(DialogUtil.class.getResource("/css/DialogPopUp.css").toExternalForm());

        applyFadeInDialog(vbox);

        Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new Image(DialogUtil.class.getResourceAsStream("/image/JavaGym_Logo.jpeg")));

        dialog.showAndWait();
        moveToMainPage(event);
    }

    public static Optional<ButtonType> showDialogChoose(String message) {
        Dialog<ButtonType> dialog = new Dialog<>();
        registerDialog(dialog);
        dialog.setTitle(basicMessage.getString("alert"));

        dialog.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setContent(new Label(message));

        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.NO);
        dialog.getDialogPane().getStylesheets().add(DialogUtil.class.getResource("/css/DialogPopUp_2Btn.css").toExternalForm());

        applyFadeInDialog(dialogPane);

        Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        Image icon = new Image(DialogUtil.class.getResourceAsStream("/image/JavaGym_Logo.jpeg"));
        dialogStage.getIcons().add(icon);

        return dialog.showAndWait();
    }

    public static Optional<ButtonType> showDialogChooseMessage(String messageCode) {
        Dialog<ButtonType> dialog = new Dialog<>();
        registerDialog(dialog);
        dialog.setTitle(basicMessage.getString("alert"));

        dialog.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setContent(new Label(basicMessage.getString(messageCode)));

        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.NO);
        dialog.getDialogPane().getStylesheets().add(DialogUtil.class.getResource("/css/DialogPopUp_2Btn.css").toExternalForm());

        applyFadeInDialog(dialogPane);

        Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        Image icon = new Image(DialogUtil.class.getResourceAsStream("/image/JavaGym_Logo.jpeg"));
        dialogStage.getIcons().add(icon);

        return dialog.showAndWait();
    }

    // 예약 확정 선택 메시지
    public static Optional<ButtonType> showReservationConfirmMessage(String trainerName, String reservationTime, int reservationNum) {
        Dialog<ButtonType> dialog = new Dialog<>();
        registerDialog(dialog);
        dialog.setTitle(basicMessage.getString("alert"));

        dialog.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        DialogPane dialogPane = dialog.getDialogPane();
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(10);
        dialogPane.setContent(vbox);

        Label label = new Label(basicMessage.getString("confirmReservation"));
        vbox.getChildren().add(label);

        label = new Label("");
        vbox.getChildren().add(label);

        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        vbox.getChildren().add(hbox);

        label = new Label("담당 트레이너: ");
        hbox.getChildren().add(label);
        label = new Label(trainerName);
        label.getStyleClass().add("reservation_trainerName");
        hbox.getChildren().add(label);
        label = new Label(" 트레이너");
        hbox.getChildren().add(label);

        hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        vbox.getChildren().add(hbox);

        label = new Label("일시: ");
        hbox.getChildren().add(label);
        label = new Label(reservationTime);
        label.getStyleClass().add("reservation_reservationDateTime");
        hbox.getChildren().add(label);
        label = new Label(" 외 ");
        hbox.getChildren().add(label);
        label = new Label(reservationNum + "");
        label.getStyleClass().add("reservation_reservationCount");
        hbox.getChildren().add(label);
        label = new Label("건");
        hbox.getChildren().add(label);


        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.NO);
        dialog.getDialogPane().getStylesheets().add(DialogUtil.class.getResource("/css/DialogPopUp_2Btn.css").toExternalForm());

        applyFadeInDialog(dialogPane);
        return dialog.showAndWait();
    }


    // 결제 선택 메시지
    public static Optional<ButtonType> showPaymentConfirmMessage(String gym, String gymPrice, String PT, String PTPrice, String lockerNumber, String lockerPeriod,
                                                                 String lockerPrice, String clothes, String clothesPrice, String totalPrice) {
        Dialog<ButtonType> dialog = new Dialog<>();
        registerDialog(dialog);
        dialog.setTitle(basicMessage.getString("alert"));

        dialog.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        DialogPane dialogPane = dialog.getDialogPane();
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(10);
        dialogPane.setContent(vbox);

        Label label = new Label(basicMessage.getString("confirmPayment"));
        vbox.getChildren().add(label);

        label = new Label("");
        vbox.getChildren().add(label);

        HBox hbox;

        // 이용권
        if(!gym.equals("")) {
            hbox = new HBox();
            hbox.setAlignment(Pos.CENTER);
            vbox.getChildren().add(hbox);

            label = new Label("이용권 ");
            hbox.getChildren().add(label);
            label = new Label(gym);
            label.getStyleClass().add("payment_gymTicket");
            hbox.getChildren().add(label);
            label = new Label("일 ");
            hbox.getChildren().add(label);
            label = new Label(gymPrice);
            label.getStyleClass().add("payment_gymTicket");
            hbox.getChildren().add(label);
            label = new Label("원");
            hbox.getChildren().add(label);
        }

        // PT 예약권
        if(!PT.equals("")) {
            hbox = new HBox();
            hbox.setAlignment(Pos.CENTER);
            vbox.getChildren().add(hbox);

            label = new Label("PT ");
            hbox.getChildren().add(label);
            label = new Label(PT);
            label.getStyleClass().add("payment_PTTicket");
            hbox.getChildren().add(label);
            label = new Label("회 ");
            hbox.getChildren().add(label);
            label = new Label(PTPrice);
            label.getStyleClass().add("payment_PTTicket");
            hbox.getChildren().add(label);
            label = new Label("원");
            hbox.getChildren().add(label);
        }

        // 사물함
        if(!lockerNumber.equals("")) {
            hbox = new HBox();
            hbox.setAlignment(Pos.CENTER);
            vbox.getChildren().add(hbox);

            label = new Label("사물함 No.");
            hbox.getChildren().add(label);
            label = new Label(lockerNumber + " ");
            label.getStyleClass().add("payment_Locker");
            hbox.getChildren().add(label);
            label = new Label(lockerPeriod);
            label.getStyleClass().add("payment_Locker");
            hbox.getChildren().add(label);
            label = new Label("일 ");
            hbox.getChildren().add(label);
            label = new Label(lockerPrice);
            label.getStyleClass().add("payment_Locker");
            hbox.getChildren().add(label);
            label = new Label("원");
            hbox.getChildren().add(label);
        }

        // 운동복
        if(!clothes.equals("")) {
            hbox = new HBox();
            hbox.setAlignment(Pos.CENTER);
            vbox.getChildren().add(hbox);

            label = new Label("운동복 ");
            hbox.getChildren().add(label);
            label = new Label(clothes);
            label.getStyleClass().add("payment_Clothes");
            hbox.getChildren().add(label);
            label = new Label("일 ");
            hbox.getChildren().add(label);
            label = new Label(clothesPrice);
            label.getStyleClass().add("payment_Clothes");
            hbox.getChildren().add(label);
            label = new Label("원");
            hbox.getChildren().add(label);
        }

        label = new Label("");
        vbox.getChildren().add(label);

        // 총 가격
        hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        vbox.getChildren().add(hbox);

        label = new Label("총 ");
        hbox.getChildren().add(label);
        label = new Label(totalPrice);
        label.getStyleClass().add("payment_TotalPrice");
        hbox.getChildren().add(label);
        label = new Label("원");
        hbox.getChildren().add(label);

        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.NO);
        dialog.getDialogPane().getStylesheets().add(DialogUtil.class.getResource("/css/DialogPopUp_2Btn.css").toExternalForm());

        applyFadeInDialog(dialogPane);

        Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new Image(DialogUtil.class.getResourceAsStream("/image/JavaGym_Logo.jpeg")));

        return dialog.showAndWait();
    }
}