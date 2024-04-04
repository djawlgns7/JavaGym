package util;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class PageUtil {

    public static void movePage(ActionEvent event, String title, String viewPath) throws IOException {
        URL url = ControllerUtil.class.getResource(viewPath + ".fxml");
        Parent newRoot = FXMLLoader.load(url);
        Scene scene = new Scene(newRoot);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);

        stage.setScene(scene);
        stage.show();
    }

    public static void movePageCenter(ActionEvent event, String title, String viewPath) throws IOException {
        URL url = ControllerUtil.class.getResource(viewPath + ".fxml");
        Parent newRoot = FXMLLoader.load(url);
        Scene scene = new Scene(newRoot);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);

        stage.setScene(scene);

        // 현재 화면의 해상도를 가져옴
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        // 창을 화면 정가운데에 위치시킴
        stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);

        stage.show();
    }

    public static void movePageCenter(ActionEvent event, String title, String viewPath, String cssPath) throws IOException {
        URL url = ControllerUtil.class.getResource(viewPath + ".fxml");
        Parent newRoot = FXMLLoader.load(url);
        Scene scene = new Scene(newRoot);

        String css = ControllerUtil.class.getResource(cssPath + ".css").toExternalForm();
        scene.getStylesheets().add(css);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);

        stage.setScene(scene);

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);

        stage.show();
    }

    public static void movePageAddCss(ActionEvent event, String title, String viewPath, String cssPath) throws IOException {
        URL url = ControllerUtil.class.getResource(viewPath + ".fxml");
        Parent newRoot = FXMLLoader.load(url);
        Scene scene = new Scene(newRoot);

        String css = ControllerUtil.class.getResource(cssPath + ".css").toExternalForm();
        scene.getStylesheets().add(css);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);

        stage.setScene(scene);
        stage.show();
    }
}