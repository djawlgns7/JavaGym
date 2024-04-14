import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.text.Font;

public class JavaGymApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // 폰트 로드
        Font.loadFont(getClass().getResourceAsStream("/fonts/Anton-Regular.ttf"), 20);
        Font.loadFont(getClass().getResourceAsStream("/fonts/Jua-Regular.ttf"), 20);
        Font.loadFont(getClass().getResourceAsStream("/fonts/NanumGothic-Regular.ttf"), 20);

        Parent loginRoot = FXMLLoader.load(getClass().getResource("/view/member/reservation.fxml"));
        stage.setScene(new Scene(loginRoot));

        // 프로젝트 실행 아이콘 이미지 로드 (승빈)
        Image icon = new Image(getClass().getResourceAsStream("/image/JavaGym_Logo.jpeg"));
        stage.getIcons().add(icon);

        stage.setTitle("JavaGym");
        stage.show();
        stage.setResizable(false);
    }

//    @Override
//    public void start(Stage stage) throws Exception {
//        // 폰트 로드
//        Font.loadFont(getClass().getResourceAsStream("/fonts/Anton-Regular.ttf"), 20);
//        Font.loadFont(getClass().getResourceAsStream("/fonts/Jua-Regular.ttf"), 20);
//
//        Parent loginRoot = FXMLLoader.load(getClass().getResource("/view/member/payment.fxml"));
//        stage.setScene(new Scene(loginRoot));
//        stage.setTitle("JavaGym");
//        stage.show();
//        stage.setResizable(false);
//    }

    public static void main(String[] args) {
        launch();
    }
}