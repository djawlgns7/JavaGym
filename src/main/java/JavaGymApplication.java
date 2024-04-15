import domain.member.SelectedMember;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import repository.MemberRepository;

public class JavaGymApplication extends Application {

    MemberRepository repository = new MemberRepository();

//    @Override
//    public void start(Stage stage) throws Exception {
//        // 폰트 로드
//        Font.loadFont(getClass().getResourceAsStream("/fonts/Anton-Regular.ttf"), 20);
//        Font.loadFont(getClass().getResourceAsStream("/fonts/Jua-Regular.ttf"), 20);
//
//        Parent loginRoot = FXMLLoader.load(getClass().getResource("/view/member/memberLogin.fxml"));
//        stage.setScene(new Scene(loginRoot));
//        stage.setTitle("JavaGym");
//        stage.show();
//        stage.setResizable(false);
//    }

    @Override
    public void start(Stage stage) throws Exception {
        // 폰트 로드
        Font.loadFont(getClass().getResourceAsStream("/fonts/Anton-Regular.ttf"), 20);
        Font.loadFont(getClass().getResourceAsStream("/fonts/Jua-Regular.ttf"), 20);
        SelectedMember.currentMember = repository.findByNum(1068);

        Parent loginRoot = FXMLLoader.load(getClass().getResource("/view/member/memberLogin.fxml"));
        stage.setScene(new Scene(loginRoot));
        stage.setTitle("JavaGym");
        stage.show();
        stage.setResizable(false);
    }

    public static void main(String[] args) {
        launch();
    }
}