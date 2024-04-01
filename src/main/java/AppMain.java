import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AppMain extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent loginRoot = FXMLLoader.load(getClass().getResource("/view/memberLogin.fxml"));
        stage.setScene(new Scene(loginRoot));
        stage.setTitle("Login");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}