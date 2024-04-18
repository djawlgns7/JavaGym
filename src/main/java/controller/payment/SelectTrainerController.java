package controller.payment;

import domain.trainer.Trainer;
import domain.trainer.WorkingHour;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import repository.TrainerRepository;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static controller.payment.PaymentController.*;
import static domain.trainer.SelectedTrainer.*;
import static util.DialogUtil.*;
import static util.ControllerUtil.createImageViewFromBytes;
import static util.PageUtil.*;

public class SelectTrainerController implements Initializable {

    private final TrainerRepository trainerRepository = new TrainerRepository();

    @FXML
    private VBox trainerList;

    @FXML
    private ComboBox<String> timeComboBox;

    @FXML
    private void selectTrainer(Trainer trainer, Event event) throws IOException {
        Optional<ButtonType> result = showDialogChoose(trainer.getName() + " 트레이너를 선택하시겠습니까?");

        if (result.get() == ButtonType.OK) {
            selectTrainer = true;
            currentTrainer = trainer;
            PaymentTab.getInstance().setSelectedTabIndex(1);
            movePage(event, "/view/member/payment");
        } else {
            selectTrainer = false;
            currentTrainer = null;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<Trainer> amTrainers = trainerRepository.findAllTrainer().stream()
                .filter(trainer -> trainer.getWorkingHour().equals(WorkingHour.AM))
                .collect(Collectors.toList());

        List<Trainer> pmTrainers = trainerRepository.findAllTrainer().stream()
                .filter(trainer -> trainer.getWorkingHour().equals(WorkingHour.PM))
                .collect(Collectors.toList());

        loadAmTrainers(amTrainers);
        timeComboBox.getSelectionModel().select("AM");
        timeComboBox.setOnAction(e -> filterTrainers(amTrainers, pmTrainers));
    }

    private void loadAmTrainers(List<Trainer> trainers) {
        trainerList.getChildren().clear(); // 기존 컨테이너의 자식을 모두 제거

        GridPane grid = new GridPane();
        grid.setHgap(10); // 열 간격 설정
        grid.setVgap(10); // 행 간격 설정

        int row = 0;
        int column = 0;

        for (Trainer trainer : trainers) {
            ImageView imageView = createImageViewFromBytes(trainer.getPhoto());
            Label nameLabel = new Label(trainer.getName());
            Label infoLabel = new Label(trainer.getHeight() + "cm | " + trainer.getWeight() + "kg | " + trainerRepository.getAge(trainer) + "세");
            Button selectButton = new Button("선택");
            selectButton.setOnAction(event -> {
                try {
                    selectTrainer(trainer, event);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            VBox detailsBox = new VBox(nameLabel, infoLabel);
            HBox trainerBox = new HBox(10, imageView, detailsBox, selectButton);

            grid.add(trainerBox, column, row); // 그리드에 트레이너 박스 추가

            column++;
            if (column == 2) { // 열이 2개가 되면 다음 행으로 넘어감
                column = 0;
                row++;
            }
        }

        trainerList.getChildren().add(grid); // GridPane을 trainerList에 추가
    }

    private void loadPmTrainers(List<Trainer> trainers) {
        trainerList.getChildren().clear(); // 기존 컨테이너의 자식을 모두 제거

        GridPane grid = new GridPane();
        grid.setHgap(10); // 열 간격 설정
        grid.setVgap(10); // 행 간격 설정

        int row = 0;
        int column = 0;

        for (Trainer trainer : trainers) {
            ImageView imageView = createImageViewFromBytes(trainer.getPhoto());
            Label nameLabel = new Label(trainer.getName());
            Label infoLabel = new Label(trainer.getHeight() + "cm | " + trainer.getWeight() + "kg | " + trainerRepository.getAge(trainer) + "세");
            Button selectButton = new Button("선택");
            selectButton.setOnAction(event -> {
                try {
                    selectTrainer(trainer, event);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            VBox detailsBox = new VBox(nameLabel, infoLabel);
            HBox trainerBox = new HBox(10, imageView, detailsBox, selectButton);

            grid.add(trainerBox, column, row); // 그리드에 트레이너 박스 추가

            column++;
            if (column == 2) { // 열이 2개가 되면 다음 행으로 넘어감
                column = 0;
                row++;
            }
        }

        trainerList.getChildren().add(grid); // GridPane을 trainerList에 추가
    }


    @FXML
    private void filterTrainers(List<Trainer> amTrainers, List<Trainer> pmTrainers) {
        String selectedTime = timeComboBox.getValue();
        if ("AM".equals(selectedTime)) {
            loadAmTrainers(amTrainers);
        } else {
            loadPmTrainers(pmTrainers);
        }
    }

    @FXML
    private void goBack(ActionEvent event) throws IOException {
        PaymentTab.getInstance().setSelectedTabIndex(1);
        movePage(event, "/view/member/payment");
    }
}