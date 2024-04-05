package service.admin;

import domain.Admin;
import domain.Member;
import javafx.event.ActionEvent;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.mindrot.jbcrypt.BCrypt;
import repository.AdminRepository;
import repository.MemberRepository;

import java.io.IOException;

import static util.AlertUtil.showAlertLoginFail;
import static util.PageUtil.*;

public class AdminService {

    private final AdminRepository adminRepository;

    public AdminService(AdminRepository repository) {
        this.adminRepository = repository;
    }

    private final MemberRepository memberRepository = new MemberRepository();

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
}