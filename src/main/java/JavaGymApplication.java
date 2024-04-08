import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JavaGymApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent loginRoot = FXMLLoader.load(getClass().getResource("/view/member/memberLogin.fxml"));
        stage.setScene(new Scene(loginRoot));
        stage.setTitle("JavaGym");
        stage.show();
    }
    
    public static void main(String[] args) {
        launch();
    }
}