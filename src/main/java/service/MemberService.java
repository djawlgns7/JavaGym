package service;

import domain.member.Member;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.mindrot.jbcrypt.BCrypt;
import repository.EntryLogRepository;
import repository.MemberRepository;

import java.io.IOException;

import static util.AlertUtil.showAlertAndMove;
import static util.AlertUtil.showAlertLoginFail;
import static util.ValidateUtil.isWrongLengthPhone;

public class MemberService {

    private final MemberRepository repository;
    private final EntryLogRepository entryLogRepository = new EntryLogRepository();

    public MemberService(MemberRepository repository) {
        this.repository = repository;
    }

    public Member signUp(Member member) {
        return repository.save(member);
    }

    public void login(TextField phoneField, PasswordField passwordField, ActionEvent event) throws IOException {

        String phone = phoneField.getText().trim();
        String password = passwordField.getText().trim();

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

            // 비밀번호 잘못 입력 시 비밀번호 필드 초기화!
            passwordField.setText("");
        }
    }

    public void entry(TextField phoneField, PasswordField passwordField, ActionEvent event) throws IOException {
        String phone = phoneField.getText().trim();
        String password = passwordField.getText().trim();

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
            entryLogRepository.save(findMember.getNum());
            showAlertAndMove("알림", findMember.getName() + "님 오늘도 파이팅!", Alert.AlertType.INFORMATION, "/view/member/memberLogin", event);
        } else {
            showAlertLoginFail("wrongPw");
            passwordField.setText("");
        }
    }
}