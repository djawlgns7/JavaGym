package service;

import domain.Item;
import domain.member.Member;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.mindrot.jbcrypt.BCrypt;
import repository.EntryLogRepository;
import repository.MemberRepository;
import repository.ReservationRepository;
import util.MemberUtil;
import util.PageUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;

import static domain.member.SelectedMember.*;
import static util.AlertUtil.*;
import static util.ValidateUtil.isWrongLengthPhone;

public class MemberService {

    private final MemberRepository repository;
    private final ReservationRepository reservationRepository = new ReservationRepository();
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
            currentMember = findMember;
            PageUtil.movePage(event, "/view/member/helloMember");
        } else {
            showAlertLoginFail("wrongPw");

            // 비밀번호 잘못 입력 시 비밀번호 필드 초기화!
            passwordField.setText("");
        }
    }

    public void immediateEntry(TextField phoneField, PasswordField passwordField, ActionEvent event) throws IOException {
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

            // 코드 수정 (성진)
            Integer gymTicket = MemberUtil.getRemain(findMember.getNum(), Item.GYM_TICKET);
            Date reservation = reservationRepository.getTodayReservationDate(findMember.getNum());

            String today = LocalDate.now().toString();
            if (gymTicket >= 1 || (reservation != null && reservation.toString().equals(today))) {
                entryLogRepository.save(findMember.getNum());
                showAlertAndMove(findMember.getName() + "님 오늘도 파이팅!", Alert.AlertType.INFORMATION, "/view/member/memberLogin", event);
            } else {
                showAlertUseMessage("DeniedEntry");
            }
        } else {
            showAlertLoginFail("wrongPw");
            passwordField.setText("");
        }
    }
}