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

import static domain.trainer.SelectedTrainer.currentTrainer;
import static util.AlertUtil.*;
import static util.PageUtil.*;

public class TrainerService {

    public static int currentTrainerNum;
    private final TrainerRepository trainerRepository;
    private ReservationRepository reservationRepository = new ReservationRepository();

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

        Trainer findTrainer = trainerRepository.findById(id);

        if (findTrainer != null && BCrypt.checkpw(password, findTrainer.getPassword())) {
            currentTrainer = findTrainer;
            System.out.println(currentTrainer);
            movePageCenter(event, "/view/trainer/helloTrainer" );
        } else {
            showAlertLoginFail("wrongPw");
        }
    }


    public void addReservation(Reservation reservation) {
        reservationRepository.insertReservation(reservation);
    }
}