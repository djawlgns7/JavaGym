package service.trainer;

import  domain.Trainer;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import org.mindrot.jbcrypt.BCrypt;
import repository.TrainerRepository;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

import static util.AlertUtil.showAlertAndMove;
import static util.AlertUtil.showAlertLoginFail;
import static util.ControllerUtil.isWrongLengthPhone;
import static util.PageUtil.movePage;

public class TrainerService {

    private final TrainerRepository repository;

    public TrainerService(TrainerRepository repository) { this.repository = repository; }

    public void login(TextField idField, PasswordField passwordField, ActionEvent event) throws IOException {
        String id = idField.getText().trim();
        String password = passwordField.getText().trim();

        if(id.isEmpty()) {
            showAlertLoginFail("emptyId");
            return;
        }


        if (password.isEmpty()) {
            showAlertLoginFail("emptyPw");
            return;
        }

        Trainer trainer = repository.findById(id);

        if (trainer != null && BCrypt.checkpw(password, trainer.getPassword())) {
            movePage(event, "/view/admin/helloTrainer");
        } else {
            showAlertLoginFail("trainerLoginFail");
        }
    }
}