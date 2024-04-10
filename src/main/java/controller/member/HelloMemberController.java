package controller.member;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import repository.MemberRepository;
import service.MemberService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static util.PageUtil.movePage;

public class HelloMemberController implements Initializable {

    @FXML
    private ImageView profileImage;

    // 이미지 추가
    private final MemberRepository repository = new MemberRepository();
    private final MemberService service = new MemberService(repository);


    @FXML
    private void goBack(ActionEvent event) throws IOException {
        movePage(event, "/view/member/memberLogin");
    }

    @FXML
    private void myInfo(ActionEvent event) throws IOException {
        movePage(event, "/view/member/myInformation", "/css/password");
    }

    @FXML
    public void showAdminLogin(ActionEvent event) throws IOException {
        movePage(event, "/view/admin/adminLogin");
    }


    @FXML
    public void showTrainerLogin(ActionEvent event) throws IOException {
        movePage(event, "/view/admin/adminLogin");
    }
    // 바로입장 버튼에 대한 메소드 추가
    // @@@ 바로입장 버튼을 눌렀을 때 문이 열리는 기능 추가 할 것 @@@
    @FXML
    public void immediateEntry(ActionEvent event) throws IOException {
        movePage(event, "/view/admin/adminLogin");
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Image image = new Image("/image/JavaGym.jpeg");
        profileImage.setImage(image);

        Circle cilpCircle = new Circle(100, 100, 100);
        profileImage.setClip(cilpCircle);
    }
}
