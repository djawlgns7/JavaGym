import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.text.Font;

public class JavaGymApplication extends Application {
    //    @Override
    //        public void start(Stage stage) throws Exception {
    //            Parent loginRoot = FXMLLoader.load(getClass().getResource("/view/member/memberLogin.fxml"));
    //            stage.setScene(new Scene(loginRoot));
    //            stage.setTitle("JavaGym");
    //            stage.show();
    //    }

    @Override
    public void start(Stage stage) throws Exception {
        // 폰트 로드
        Font.loadFont(getClass().getResourceAsStream("/fonts/Anton-Regular.ttf"), 20);
        Font.loadFont(getClass().getResourceAsStream("/fonts/Jua-Regular.ttf"), 20);

        Parent loginRoot = FXMLLoader.load(getClass().getResource("/view/trainer/reservationInfo.fxml"));
        stage.setScene(new Scene(loginRoot));
        stage.setTitle("JavaGym");
        stage.show();
        stage.setResizable(false);
    }

    public static void main(String[] args) {
        launch();
    }
}