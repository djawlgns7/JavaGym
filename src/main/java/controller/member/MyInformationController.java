package controller.member;

import domain.member.Member;
import domain.trainer.Trainer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import repository.MemberRepository;
import repository.TrainerRepository;

import java.net.URL;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import static domain.member.SelectedMember.*;
import static util.MemberUtil.*;

public class MyInformationController implements Initializable {
    private final TrainerRepository trainerRepository = new TrainerRepository();
    private final MemberRepository memberRepository = new MemberRepository();

    @FXML
    private Label memberName, trainerName, trainerInfo, gymTicketReamin, PTTicketRemain, lockerNo, lockerRemain,
            clothesAvailability, clothesRemain, trainerViewTitle;
    @FXML
    private HBox trainerView;

    @FXML
    private ImageView imageView;

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
    public void setMyInfo(Member member) throws ParseException {
        int memberNum = member.getNum();
        int trainerNum = getTrainerNumForMember(memberNum);
        List<Integer> remain = getRemainAll(memberNum);
        LocalDate today = LocalDate.now();
        int gymTicket = remain.get(0);
        int PTTicket = remain.get(1);
        int clothes = remain.get(2);
        int locker = remain.get(3);

        memberName.setText(member.getName());

        if(trainerNum == 0) {
            trainerView.setVisible(false);
            trainerViewTitle.setVisible(false);
        }else{
            Trainer trainer = trainerRepository.findByNum(trainerNum);
            LocalDate expireDate = today;
            int trainerAge = trainerRepository.getAge(trainer);
            Image trainerImage = trainerRepository.getImage(trainer.getNum());

            imageView.setImage(trainerImage);
            trainerName.setText(trainer.getName() + "트레이너");
            trainerInfo.setText(trainer.getHeight() + "cm|" + trainer.getWeight() + "kg|" + trainerAge + "살");
        }

        if(gymTicket == 0) {
            gymTicketReamin.setText("헬스장에 입장할 수 없습니다. 이용권을 구매해 주세요");
        }else {
            LocalDate expireDate = today.plusDays(gymTicket);
            gymTicketReamin.setText(expireDate + " (D-" + gymTicket + ")");
        }

        PTTicketRemain.setText(PTTicket + "개");

        if(locker == 0){
            lockerNo.setText("사물함을 이용할 수 없습니다");
            lockerRemain.setVisible(false);
        }else{
            int lockerNum = getLockerNum(memberNum);
            LocalDate expireDate = today.plusDays(locker);
            lockerNo.setText("No." + lockerNum);
            lockerRemain.setText(expireDate + " (D-" + locker + ")");
        }

        if(clothes == 0){
            clothesAvailability.setText("대여 불가능");
            clothesRemain.setVisible(false);
        }else{
            LocalDate expireDate = today.plusDays(clothes);
            clothesAvailability.setText("대여 가능");
            clothesRemain.setText(expireDate + " (D-" + clothes + ")");
        }
    }
}
