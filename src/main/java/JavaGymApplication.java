import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import util.PageUtil;

public class JavaGymApplication extends Application {

//    @Override
//    public void start(Stage stage) throws Exception {
//        Parent loginRoot = FXMLLoader.load(getClass().getResource("/view/member/memberLogin.fxml"));
//        stage.setScene(new Scene(loginRoot));
//        stage.setTitle("JavaGym");
//        stage.show();
//    }

    @Override
    public void start(Stage stage) throws Exception {
        // 폰트 추가
        Font.loadFont(getClass().getResourceAsStream("/fonts/Anton-Regular.ttf"), 20);
        Parent loginRoot = FXMLLoader.load(getClass().getResource("/view/admin/memberInfo.fxml"));
        stage.setScene(new Scene(loginRoot));
        stage.setTitle("JavaGym");
        stage.show();
        // 창의 크기를 조절할 수 없도록 설정
        stage.setResizable(false);
    }

    public static void main(String[] args) {
        launch();
    }
}