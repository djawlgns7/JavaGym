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

public class TrainerService {
/*
    private final TrainerRepository repository;

    public TrainerService(TrainerRepository repository) { this.repository = repository; }

    public void login(TextField idField, PasswordField passwordField, ActionEvent event) throws IOException {
        String id = idField.getText().trim();
        String password = passwordField.getText().trim();

        if(id.isEmpty()) {
            showAlertLoginFail("emptyId");
            return;
        }

        if(isWrongLengthPhone(id)) {
            showAlertLoginFail("wrongPhone");
            return;
        }

        if (password.isEmpty()) {
            showAlertLoginFail("emptyPw");
            return;
        }

        if (repository.findById(id) == null) {
            showAlertLoginFail("unregistered");
            return;
        }

        Trainer findTrainer = repository.findById(id);

        if(BCrypt.checkpw(password, findTrainer.getPassword())) {
            showAlertAndMove("로그인 성공", findTrainer.getName() + " 트레이너님 환영합니다^^", Alert.AlertType.INFORMATION, "/view/trainer/helloTrainer", event);
        } else {
            showAlertLoginFail("wrongPw");
            // 비밀번호 잘못 입력하면 비밀번호 필드 초기화
            passwordField.setText("");
        }
    }*/
}