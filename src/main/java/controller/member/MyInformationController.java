package controller.member;

import domain.member.Member;
import domain.trainer.Trainer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import repository.MemberRepository;
import repository.TrainerRepository;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ResourceBundle;

import static domain.member.SelectedMember.*;
import static util.MemberUtil.*;
import static util.PageUtil.movePage;

public class MyInformationController implements Initializable {
    private final TrainerRepository trainerRepository = new TrainerRepository();
    @FXML
    private Label memberName, trainerName, gymTicketRemain, PTTicketRemain, lockerNo, lockerRemain,
            clothesAvailability, clothesRemain, trainerPhone;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (currentMember != null) {
            Member member = currentMember;

            try {
                setMyInfo(member);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //내 정보 페이지의 정보를 바꿔주는 메소드
    @FXML
    public void setMyInfo(Member member) throws ParseException {
        int memberNum = member.getNum();
        int trainerNum = getTrainerNumForMember(memberNum);
        List<Integer> remain = getRemainAll(memberNum);
        LocalDate today = LocalDate.now();
        int gymTicket = remain.get(0);
        int PTTicket = remain.get(1);
        int clothes = remain.get(2);
        int locker = remain.get(3);

        memberName.setText(member.getName() + " ");

        if (trainerNum == 0) {
            trainerName.setText("");
            trainerPhone.setText("담당 트레이너가 없습니다");

        } else {
            Trainer trainer = trainerRepository.findByNum(trainerNum);
            LocalDate expireDate = today;
            int trainerAge = trainerRepository.getAge(trainer);
            Image trainerImage = trainerRepository.getImage(trainer.getNum());

            String phone = trainer.getPhone();
            String claculatedTrainerPhone = "010-" + phone.substring(0, 4) + "-" + phone.substring(4, 8);

            trainerName.setText(trainer.getName() + " 트레이너");
            trainerPhone.setText(claculatedTrainerPhone);
        }

        if (gymTicket == 0) {
            gymTicketRemain.setText("결제 내역이 없습니다");
        } else {
            LocalDate expireDate = today.plusDays(gymTicket);
            long daysUntilExpire = ChronoUnit.DAYS.between(today, expireDate);
            gymTicketRemain.setText(expireDate + " (D-" + daysUntilExpire + ")");
        }

        PTTicketRemain.setText(PTTicket + "개");

        if (locker == 0) {
            lockerNo.setText("이용할 수 없습니다");
            lockerRemain.setText("");
        } else {
            int lockerNum = getLockerNum(memberNum);

            LocalDate expireDate = today.plusDays(locker);
            long daysUntilExpire = ChronoUnit.DAYS.between(today, expireDate);
            lockerNo.setText("No." + lockerNum);
            lockerRemain.setText(expireDate + " (D-" + daysUntilExpire + ")");
        }

        if (clothes == 0) {
            clothesAvailability.setText("대여할 수 없습니다");
            clothesRemain.setText("");
        } else {
            LocalDate expireDate = today.plusDays(clothes);
            long daysUntilExpire = ChronoUnit.DAYS.between(today, expireDate);
            clothesAvailability.setText("대여 가능");
            clothesRemain.setText(expireDate + " (D-" + daysUntilExpire + ")");
        }
    }

    @FXML
    public void moveToMain(ActionEvent event) throws IOException {
        movePage(event, "/view/member/helloMember");
    }
}