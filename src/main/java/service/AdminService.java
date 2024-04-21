package service;

import domain.admin.Admin;
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

import static domain.admin.SelectedAdmin.loginAdmin;
import static domain.member.SelectedMember.loginMember;
import static util.DialogUtil.showDialogErrorMessage;
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
            showDialogErrorMessage("emptyId");
            return;
        }

        if (password.isEmpty()) {
            showDialogErrorMessage("emptyPw");
            return;
        }

        Admin admin = adminRepository.findById(id);

        if (admin != null && BCrypt.checkpw(password, admin.getPassword())) {
            loginMember = null;
            movePage(event, "/view/admin/helloAdminV2");
            loginAdmin = new Admin();
        } else {
            showDialogErrorMessage("loginFail");
        }
    }

    public void addMember(Member member) {
        memberRepository.save(member);
    }

    public void addTrainer(Trainer trainer) {
        trainerRepository.save(trainer);
    }
}