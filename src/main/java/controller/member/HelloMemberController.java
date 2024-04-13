package controller.member;

import domain.Item;
import domain.member.Member;
import domain.member.MemberSchedule;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import repository.EntryLogRepository;
import repository.MemberRepository;
import repository.ReservationRepository;
import util.MemberUtil;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import static domain.member.SelectedMember.currentMember;
import static util.AlertUtil.*;
import static util.MemberUtil.*;
import static util.PageUtil.movePage;
import static util.PageUtil.movePageCenter;

public class HelloMemberController implements Initializable {

    private final ReservationRepository reservationRepository = new ReservationRepository();
    private final EntryLogRepository entryLogRepository = new EntryLogRepository();

    @FXML
    private ImageView profileImage;
    @FXML
    private Label PTTicketRemain, memberName, DDay;

    // 이미지 추가

    private final MemberRepository repository = new MemberRepository();

    @FXML
    private void goBack(ActionEvent event) throws IOException {
        movePage(event, "/view/member/memberLogin");
    }

    @FXML
    private void reservation(ActionEvent event) throws IOException{
        int trainerNum = getTrainerNumForMember(currentMember.getNum());
        List<MemberSchedule> memberSchedule;
        memberSchedule = reservationRepository.findMemberSchedule(currentMember.getNum());
        int memberReservationNum = memberSchedule.size();

        if(trainerNum == 0){
            showAlert("배정된 트레이너가 존재하지 않습니다.", Alert.AlertType.INFORMATION);
        }else if(memberReservationNum >= 4){
            showAlert("최대 예약 횟수만큼 예약을 했습니다", Alert.AlertType.INFORMATION);
        }else {
            movePage(event, "/view/member/reservation");
        }
    }

    @FXML
    private void myInfo(ActionEvent event) throws IOException {
        movePage(event, "/view/member/myInformation");
    }

    @FXML
    public void entry(ActionEvent event) throws IOException {
        // 코드 수정 (성진)
        Integer memberNum = currentMember.getNum();
        Integer gymTicket = MemberUtil.getRemain(memberNum, Item.GYM_TICKET);
        Date reservation = reservationRepository.getTodayReservationDate(memberNum);

        String today = LocalDate.now().toString();
        if (gymTicket >= 1 || (reservation != null && reservation.toString().equals(today))) {
            entryLogRepository.save(memberNum);
            showAlertAndMove(currentMember.getName() + "님 오늘도 파이팅!", Alert.AlertType.INFORMATION, "/view/member/memberLogin", event);
        } else {
            showAlertUseMessage("DeniedEntry");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if(currentMember != null) {
            Member member = currentMember;
            List<Integer> remain = getRemainAll(member.getNum());
            int PTTicket = remain.get(1);
            int gymTicket = getRemain(member.getNum(), Item.GYM_TICKET);

            PTTicketRemain.setText("PT 이용권 " + PTTicket + "개");
            memberName.setText(member.getName() + "님, 환영합니다!");
            if(gymTicket > 0) {
                DDay.setText("D - " + gymTicket);
            }else if(repository.hasReservationToday(member.getNum())){
                DDay.setText("입장 가능");
            }
            else{
                DDay.setText("입장 불가능");
            }
        }

        Image image = new Image("/image/JavaGym_Logo.jpeg");
        profileImage.setImage(image);

        Circle clipCircle = new Circle(100, 100, 100);
        profileImage.setClip(clipCircle);
    }

    @FXML
    private void moveToPaymentPage(ActionEvent event) throws IOException {
        movePageCenter(event, "/view/member/payment");
    }
}
