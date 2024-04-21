package service;

import domain.trainer.Reservation;
import  domain.trainer.Trainer;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.mindrot.jbcrypt.BCrypt;
import repository.ReservationRepository;
import repository.TrainerRepository;


import javafx.event.ActionEvent;
import java.io.IOException;

import static domain.trainer.SelectedTrainer.loginTrainer;
import static util.DialogUtil.*;
import static util.PageUtil.*;

public class TrainerService {

    private final TrainerRepository trainerRepository;
    private final ReservationRepository reservationRepository = new ReservationRepository();

    public TrainerService(TrainerRepository repository) { this.trainerRepository = repository; }

    public void login(TextField idField, PasswordField passwordField, ActionEvent event) throws IOException {
        String id = idField.getText().trim();
        String password = passwordField.getText().trim();

        if (id.isEmpty() && password.isEmpty()) {
            showDialogErrorMessage("emptyIdAndPassword");
            return;
        }

        if(id.isEmpty()) {
            showDialogErrorMessage("emptyId");
            return;
        }

        if (password.isEmpty()) {
            showDialogErrorMessage("emptyPw");
            return;
        }

        Trainer findTrainer = trainerRepository.findById(id);
        if (findTrainer == null) {
            showDialogErrorMessage("loginFail");
            return;
        }
        if (findTrainer != null && BCrypt.checkpw(password, findTrainer.getPassword())) {
            loginTrainer = findTrainer;

            movePage(event, "/view/trainer/reservationInfo" );
        } else {
            showDialogErrorMessage("loginFail");
        }
    }


    public void addReservation(Reservation reservation) {
        reservationRepository.insertReservation(reservation);
    }
}