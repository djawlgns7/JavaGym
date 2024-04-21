package util;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

import static domain.member.SelectedMember.loginMember;
import static domain.trainer.SelectedTrainer.loginTrainer;
import static thread.InactivityManager.*;
import static util.AnimationUtil.applyFadeIn;

public class PageUtil {

    public static void movePage(Event event, String viewPath) throws IOException {

        URL url = PageUtil.class.getResource(viewPath + ".fxml");
        Parent newRoot = FXMLLoader.load(url);
        Scene scene = new Scene(newRoot);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("JavaGym");

        stage.setScene(scene);
        setupInactivityTimer(scene); // 추가

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);

        applyFadeIn(stage);
        stage.show();
    }

    public static void moveToMainPage(Event event) throws IOException {
        if (inactivityTimer != null) {
            inactivityTimer.stop();
        }

        loginMember = null;
        loginTrainer = null;

        URL url = ControllerUtil.class.getResource("/view/member/memberLogin.fxml");
        Parent newRoot = FXMLLoader.load(url);
        Scene scene = new Scene(newRoot);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        if (stage != null) {
            stage.setTitle("JavaGym");

            stage.setScene(scene);

            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

            stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
            stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);

            applyFadeIn(stage);
            stage.show();
        }
    }
}