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

import static thread.InactivityManager.setupInactivityTimer;

public class PageUtil {

    public static void movePage(Event event, String viewPath) throws IOException {
        URL url = ControllerUtil.class.getResource(viewPath + ".fxml");
        Parent newRoot = FXMLLoader.load(url);
        Scene scene = new Scene(newRoot);


        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        if(stage != null) {
            stage.setTitle("JavaGym");

            stage.setScene(scene);
            setupInactivityTimer(scene); // 추가

            // 현재 화면의 해상도를 가져옴
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

            // 창을 화면 정가운데에 위치시킴
            stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
            stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);

            stage.show();
        }
    }
}