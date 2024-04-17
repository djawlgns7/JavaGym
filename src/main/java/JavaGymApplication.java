import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import static thread.InactivityManager.*;
import static util.AnimationUtil.*;

public class JavaGymApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // 폰트 로드
        Font.loadFont(getClass().getResourceAsStream("/fonts/Anton-Regular.ttf"), 20);
        Font.loadFont(getClass().getResourceAsStream("/fonts/Jua-Regular.ttf"), 20);
        Font.loadFont(getClass().getResourceAsStream("/fonts/NanumGothic-Regular.ttf"), 20);

        // 메인 화면 로드
        Parent loginRoot = FXMLLoader.load(getClass().getResource("/view/member/hidden.fxml"));
        Scene scene = new Scene(loginRoot);
        stage.setScene(scene);

        // 프로젝트 실행 아이콘 이미지 로드 (승빈)
        Image icon = new Image(getClass().getResourceAsStream("/image/JavaGym_Logo.jpeg"));
        stage.getIcons().add(icon);

        stage.setTitle("JavaGym");
        stage.setResizable(false);

        applyFadeIn(stage);
        stage.show();

        setMainStage(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}