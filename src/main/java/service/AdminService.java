package service;

import domain.Admin;
import domain.member.Member;
import domain.trainer.Trainer;
import javafx.event.ActionEvent;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.mindrot.jbcrypt.BCrypt;
import repository.AdminRepository;
import repository.MemberRepository;
import repository.TrainerRepository;

import java.io.IOException;

import static util.AlertUtil.showAlertLoginFail;
import static util.PageUtil.*;

public class AdminService {

    private final AdminRepository adminRepository;
    private final MemberRepository memberRepository = new MemberRepository();
    private final TrainerRepository trainerRepository = new TrainerRepository();

    public AdminService(AdminRepository repository) {
        this.adminRepository = repository;
    }

    public void login(TextField idField, PasswordField passwordField, ActionEvent event) throws IOException {
        String id = idField.getText().trim();
        String password = passwordField.getText().trim();

        if (id.isEmpty()) {
            showAlertLoginFail("emptyId");
            return;
        }

        if (password.isEmpty()) {
            showAlertLoginFail("emptyPw");
            return;
        }

        Admin admin = adminRepository.findById(id);

        if (admin != null && BCrypt.checkpw(password, admin.getPassword())) {
            movePage(event, "/view/admin/helloAdmin");
        } else {
            showAlertLoginFail("adminLoginFail");
        }
    }

    public void addMember(Member member) {
        memberRepository.save(member);
    }

    public void addTrainer(Trainer trainer) {
        trainerRepository.save(trainer);
    }

    public void updateMember() {

    }

    public void updateTrainer() {

    }

    public void cancelReservation() {

    }
}