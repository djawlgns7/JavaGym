package service;

import domain.Member;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import org.mindrot.jbcrypt.BCrypt;
import repository.MemberRepository;

import java.io.IOException;

import static util.AlertUtil.showAlertAndMove;
import static util.AlertUtil.showAlertLoginFail;
import static util.ControllerUtil.isWrongLengthPhone;

public class MemberService {

    private final MemberRepository repository;

    public MemberService(MemberRepository repository) {
        this.repository = repository;
    }

    public Member signUp(Member member) {
        return repository.save(member);
    }

    public void login(String phone, String password, ActionEvent event) throws IOException {

        if (phone.isEmpty()) {
            showAlertLoginFail("emptyPhone");
            return;
        }

        if (isWrongLengthPhone(phone)) {
            showAlertLoginFail("wrongPhone");
            return;
        }

        if (password.isEmpty()) {
            showAlertLoginFail("emptyPw");
            return;
        }

        if (repository.findByPhone(phone) == null) {
            showAlertLoginFail("unregistered");
            return;
        }

        Member findMember = repository.findByPhone(phone);

        if (BCrypt.checkpw(password, findMember.getPassword())) {
            showAlertAndMove("로그인 성공", findMember.getName() + "님 환영합니다^^", Alert.AlertType.INFORMATION, "/view/member/helloMember", event);
        } else {
            showAlertLoginFail("wrongPw");
        }
    }
}