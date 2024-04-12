package controller.member;

import domain.Item;
import domain.member.Member;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.scene.control.Label;
import repository.MemberRepository;
import service.MemberService;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static domain.member.SelectedMember.currentMember;
import static util.AlertUtil.showAlertChoose;
import static util.MemberUtil.*;
import static util.PageUtil.movePage;

public class HelloMemberController implements Initializable {

    @FXML
    private ImageView profileImage;
    @FXML
    private Label PTTicketRemain, memberName, DDay;

    // 이미지 추가
    private final MemberRepository repository = new MemberRepository();
    private final MemberService service = new MemberService(repository);

    @FXML
    private void goBack(ActionEvent event) throws IOException {
        movePage(event, "/view/member/memberLogin");
    }

    @FXML
    private void reservation(ActionEvent event) throws IOException{
        int trainerNum = getTrainerNumForMember(currentMember.getNum());
        if(trainerNum == 0){
            showAlertChoose("배정된 트레이너가 존재하지 않습니다.");
        }else {
            movePage(event, "/view/member/reservation");
        }
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
        if(currentMember != null) {
            Member member = currentMember;
            List<Integer> remain = getRemainAll(member.getNum());
            int PTTicket = remain.get(1);
            int gymTicket = getRemain(member.getNum(), Item.GYM_TICKET);

            PTTicketRemain.setText("PT 이용권 " + PTTicket + "개");
            memberName.setText(member.getName() + "님,환영합니다!");
            if(gymTicket > 0) {
                DDay.setText("D-" + gymTicket);
            }else if(repository.hasReservationToday(member.getNum())){
                DDay.setText("입장 가능");
            }
            else{
                DDay.setText("입장 불가능");
            }
        }

        Image image = new Image("/image/JavaGym.jpeg");
        profileImage.setImage(image);

        Circle cilpCircle = new Circle(100, 100, 100);
        profileImage.setClip(cilpCircle);
    }
}
