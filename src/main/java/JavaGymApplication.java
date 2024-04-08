import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class JavaGymApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // 폰트 로드
        Font.loadFont(getClass().getResourceAsStream("/fonts/Anton-Regular.ttf"), 20);
        Font.loadFont(getClass().getResourceAsStream("/fonts/Jua-Regular.ttf"), 20);

        Parent loginRoot = FXMLLoader.load(getClass().getResource("/view/admin/memberInfo.fxml"));
        stage.setScene(new Scene(loginRoot));
        stage.setTitle("JavaGym");
        stage.show();
        stage.setResizable(false);
    }

    public static void main(String[] args) {
        launch();
    }
}