package controller.member;

import domain.Item;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
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
import java.util.ResourceBundle;

import static domain.member.SelectedMember.*;
import static util.AlertUtil.showAlertAndMove;
import static util.AlertUtil.showAlertUseMessage;
import static util.PageUtil.movePage;

public class HelloMemberController implements Initializable {

    private final ReservationRepository reservationRepository = new ReservationRepository();
    private final EntryLogRepository entryLogRepository = new EntryLogRepository();

    @FXML
    private ImageView profileImage;

    // 이미지 추가
    private final MemberRepository repository = new MemberRepository();

    @FXML
    private void goBack(ActionEvent event) throws IOException {
        movePage(event, "/view/member/memberLogin");
    }

    @FXML
    private void myInfo(ActionEvent event) throws IOException {
        movePage(event, "/view/member/myInformation", "/css/password");
    }

    @FXML
    public void entry(ActionEvent event) throws IOException {

        Integer gymTicket = MemberUtil.getRemain(currentMember.getNum(), Item.GYM_TICKET);
        Date reservation = reservationRepository.getTodayReservationDate(currentMember.getNum());

        String today = LocalDate.now().toString();
        if (gymTicket.equals(0) && reservation == null) {
            showAlertUseMessage("DeniedEntry");
            return;
        }

        if (!reservation.toString().equals(today)) {
            showAlertUseMessage("DeniedEntry");
            return;
        }

        entryLogRepository.save(currentMember.getNum());
        showAlertAndMove(currentMember.getName() + "님 오늘도 파이팅!", Alert.AlertType.INFORMATION, "/view/member/memberLogin", event);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Image image = new Image("/image/JavaGym.jpeg");
        profileImage.setImage(image);

        Circle cilpCircle = new Circle(100, 100, 100);
        profileImage.setClip(cilpCircle);
    }
}
