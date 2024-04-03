package service;

import domain.Admin;
import javafx.event.ActionEvent;
import org.mindrot.jbcrypt.BCrypt;
import repository.AdminRepository;
import util.AlertUtil;
import util.ControllerUtil;
import util.PageUtil;

import java.io.IOException;

import static util.AlertUtil.showAlertLoginFail;
import static util.ControllerUtil.*;

public class AdminService {

    private final AdminRepository repository;

    public AdminService(AdminRepository repository) {
        this.repository = repository;
    }

    public void login(String id, String password, ActionEvent event) throws IOException {

        if (id.isEmpty()) {
            showAlertLoginFail("emptyId");
            return;
        }

        if (password.isEmpty()) {
            showAlertLoginFail("emptyPw");
            return;
        }

        Admin admin = repository.findById(id);

        if (admin != null && BCrypt.checkpw(password, admin.getPassword())) {
            PageUtil.movePage(event, "admin", "/view/admin/helloAdmin");
        } else {
            showAlertLoginFail("adminLoginFail");
        }
    }


}