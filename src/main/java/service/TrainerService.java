package service;


import  domain.trainer.Trainer;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.mindrot.jbcrypt.BCrypt;
import repository.TrainerRepository;


import javafx.event.ActionEvent;
import java.io.IOException;

import static util.AlertUtil.*;
import static util.PageUtil.*;

public class TrainerService {


    private final TrainerRepository trainerRepository;

    public TrainerService(TrainerRepository repository) { this.trainerRepository = repository; }

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

        Trainer trainer = trainerRepository.findById(id);

        if (trainer != null && BCrypt.checkpw(password, trainer.getPassword())) {
            movePage(event, "/view/trainer/helloTrainer");
        } else {
            showAlertLoginFail("adminLoginFail");
        }
    }
}