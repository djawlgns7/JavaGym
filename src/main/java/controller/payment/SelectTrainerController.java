package controller.payment;

import domain.trainer.Trainer;
import domain.trainer.WorkingHour;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import repository.TrainerRepository;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static controller.payment.PaymentController.*;
import static util.DialogUtil.*;
import static util.ControllerUtil.createImageViewFromBytes;
import static util.PageUtil.*;

public class SelectTrainerController implements Initializable {

    private final TrainerRepository trainerRepository = new TrainerRepository();

    @FXML
    private ScrollPane scroll;

    @FXML
    private VBox trainerList;

    @FXML
    private ComboBox<String> timeComboBox;

    @FXML
    private void selectTrainer(Trainer trainer, Event event) throws IOException {
        Optional<ButtonType> result = showDialogChoose(trainer.getName() + " 트레이너를 선택하시겠습니까?");

        if (result.get() == ButtonType.OK) {
            selectTrainer = true;
            selectedTrainer = trainer;
            PaymentTab.getInstance().setSelectedTabIndex(1);
            movePage(event, "/view/member/payment");
        } else {
            selectTrainer = false;
            selectedTrainer = null;
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

        // 스크롤 속도 조정을 위한 스크롤 이벤트 리스너 추가 (승빈)
        scroll.addEventFilter(ScrollEvent.SCROLL, event -> {
            double deltaY = event.getDeltaY() * 3; // 스크롤 속도 조정, 값이 클수록 속도가 빨라짐
            double width = scroll.getContent().getBoundsInLocal().getWidth();
            double value = scroll.getVvalue();
            scroll.setVvalue(value + -deltaY / width); // 세로 스크롤일 경우
            event.consume();
        });

        // 오전, 오후를 선택할 때마다 스크롤을 맨 위로 이동 (성진)
        timeComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                scroll.setVvalue(0.0);
            }
        });
    }

    private void loadAmTrainers(List<Trainer> trainers) {
        trainerList.getChildren().clear(); // 기존 컨테이너의 자식을 모두 제거

        for (Trainer trainer : trainers) {
            ImageView imageView = createImageViewFromBytes(trainer.getPhoto());
            imageView.getStyleClass().add("selectTrainer_ImageView");

            Label nameLabel = new Label(trainer.getName());
            nameLabel.getStyleClass().add("selectTrainer_Name");

            Label infoLabel = new Label(trainer.getHeight() + "cm | " + trainer.getWeight() + "kg | " + trainerRepository.getAge(trainer) + "세");
            infoLabel.getStyleClass().add("selectTrainer_Info");

            Button selectButton = new Button("선택");
            selectButton.getStyleClass().add("selectTrainer_SelectBtn");
            selectButton.setOnAction(event -> {
                try {
                    selectTrainer(trainer, event);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            StackPane imageContainer = new StackPane(imageView);
            imageContainer.getStyleClass().add("selectTrainer_ImageContainer");

            VBox detailsBox = new VBox(nameLabel, infoLabel);
            detailsBox.getStyleClass().add("selectTrainer_TrainerDetailBox");

            HBox trainerBox = new HBox(10, imageContainer, detailsBox, selectButton);
            trainerBox.getStyleClass().add("selectTrainer_TrainerBox");

            trainerList.getChildren().add(trainerBox);
        }
    }



    private void loadPmTrainers(List<Trainer> trainers) {
        trainerList.getChildren().clear(); // 기존 컨테이너의 자식을 모두 제거

        for (Trainer trainer : trainers) {
            ImageView imageView = createImageViewFromBytes(trainer.getPhoto());
            imageView.getStyleClass().add("selectTrainer_ImageView");

            Label nameLabel = new Label(trainer.getName());
            nameLabel.getStyleClass().add("selectTrainer_Name");

            Label infoLabel = new Label(trainer.getHeight() + "cm | " + trainer.getWeight() + "kg | " + trainerRepository.getAge(trainer) + "세");
            infoLabel.getStyleClass().add("selectTrainer_Info");

            Button selectButton = new Button("선택");
            selectButton.getStyleClass().add("selectTrainer_SelectBtn");
            selectButton.setOnAction(event -> {
                try {
                    selectTrainer(trainer, event);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            StackPane imageContainer = new StackPane(imageView);
            imageContainer.getStyleClass().add("selectTrainer_ImageContainer");

            VBox detailsBox = new VBox(nameLabel, infoLabel);
            detailsBox.getStyleClass().add("selectTrainer_TrainerDetailBox");

            HBox trainerBox = new HBox(10, imageContainer, detailsBox, selectButton);
            trainerBox.getStyleClass().add("selectTrainer_TrainerBox");

            trainerList.getChildren().add(trainerBox);
        }
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