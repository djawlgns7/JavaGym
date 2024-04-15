import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import static thread.InactivityManager.*;

public class JavaGymApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // 폰트 로드
        Font.loadFont(getClass().getResourceAsStream("/fonts/Anton-Regular.ttf"), 20);
        Font.loadFont(getClass().getResourceAsStream("/fonts/Jua-Regular.ttf"), 20);

        // 메인 화면 로드
        Parent loginRoot = FXMLLoader.load(getClass().getResource("/view/member/memberLogin.fxml"));
        Scene scene = new Scene(loginRoot);
        stage.setScene(scene);
        stage.setTitle("JavaGym");
        stage.setResizable(false);
        stage.show();

        setMainStage(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}