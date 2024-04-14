package controller.member;

import domain.member.Member;
import domain.member.UsingLocker;
import domain.trainer.Trainer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import repository.MemberRepository;
import repository.PurchaseRepository;
import repository.TrainerRepository;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static domain.member.SelectedMember.currentMember;
import static util.AlertUtil.showAlert;
import static util.MemberUtil.getTrainerNumForMember;

public class LockerSelectionController implements Initializable {
    private final TrainerRepository trainerRepository = new TrainerRepository();
    private final PurchaseRepository purchaseRepository = new PurchaseRepository();
    private final MemberRepository memberRepository = new MemberRepository();

    @FXML
    private HBox lockerArea;
    @FXML
    Label selectedLockerNum, lockerAreaHeader, nextButton, previousButton;
    @FXML
    RadioButton locker30Days, locker90Days, locker180Days, locker360Days;

    Member member;
    Trainer trainer;
    List<Label> lockers;
    List<UsingLocker> usingLockers;
    ToggleGroup lockerPeriodGroup;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (currentMember != null) {
            member = currentMember;
            trainer = trainerRepository.findByNum(getTrainerNumForMember(member.getNum()));
            lockers = new ArrayList<>();
            usingLockers = purchaseRepository.findAllUsingLocker();

            lockerPeriodGroup = new ToggleGroup();
            locker30Days.setToggleGroup(lockerPeriodGroup);
            locker90Days.setToggleGroup(lockerPeriodGroup);
            locker180Days.setToggleGroup(lockerPeriodGroup);
            locker360Days.setToggleGroup(lockerPeriodGroup);

            makeLockerList();
            makeLockerArea(1);
            makeOnclickListner();

            makeLockerArea(1);
        }else{
            int memberNum = 1000;
            member = memberRepository.findByNum(memberNum);
            trainer = trainerRepository.findByNum(getTrainerNumForMember(memberNum));
            lockers = new ArrayList<>();
            usingLockers = purchaseRepository.findAllUsingLocker();

            lockerPeriodGroup = new ToggleGroup();
            locker30Days.setToggleGroup(lockerPeriodGroup);
            locker90Days.setToggleGroup(lockerPeriodGroup);
            locker180Days.setToggleGroup(lockerPeriodGroup);
            locker360Days.setToggleGroup(lockerPeriodGroup);

            makeLockerList();
            makeLockerArea(1);
            makeOnclickListner();
        }
    }

    public void makeLockerList(){
        for(int i = 0; i < 200; i++){
            int lockerNum = lockers.size() + 1;
            Label newLocker = new Label(String.valueOf(lockerNum));
            lockers.add(newLocker);
            newLocker.getStyleClass().add("locker");
            newLocker.getStyleClass().add("unselectedLocker");
        }

        for(int i = 0; i < usingLockers.size(); i++){
            int lockerNum = usingLockers.get(i).getLockerNum() - 1;

            if(member.getNum() == usingLockers.get(i).getMemberNum()){
                lockers.get(lockerNum).getStyleClass().remove("unselectedLocker");
                lockers.get(lockerNum).getStyleClass().add("selectedLocker");
                selectedLockerNum.setText(String.valueOf(usingLockers.get(i).getLockerNum()));
            }else{
                lockers.get(lockerNum).getStyleClass().remove("unselectedLocker");
                lockers.get(lockerNum).getStyleClass().add("occupiedLocker");
            }
        }
    }

    public void makeLockerArea(int startNum) {
        startNum--;

        for(int i = 0; i < 14; i++) {
            VBox newRow = new VBox();
            lockerArea.getChildren().add(newRow);

            if(i % 3 == 2){
                for(int j = 0; j < 10; j++) {
                    Label newLocker = new Label();
                    newLocker.getStyleClass().add("lockerAreaPath");
                    newRow.getChildren().add(newLocker);
                }
            }else{
                for(int j = 0; j < 10; j++) {
                    Label newLocker = lockers.get(startNum);
                    newRow.getChildren().add(newLocker);
                    startNum++;
                }
            }
        }
    }

    public void makeOnclickListner(){
        for(int i = 0; i < lockers.size(); i++){
            Label currentLocker = lockers.get(i);
            if(currentLocker.getStyleClass().contains("unselectedLocker")){
                currentLocker.setOnMouseClicked(mouseEvent -> {
                    String selectedLockerNumText = selectedLockerNum.getText();

                    if(selectedLockerNumText.equals("")){
                        currentLocker.getStyleClass().remove("unselectedLocker");
                        currentLocker.getStyleClass().add("selectedLocker");
                        selectedLockerNum.setText(currentLocker.getText());
                    }else {
                        int currentLockerNum = Integer.parseInt(selectedLockerNumText) - 1;
                        lockers.get(currentLockerNum).getStyleClass().remove("selectedLocker");
                        currentLocker.getStyleClass().remove("unselectedLocker");
                        currentLocker.getStyleClass().add("selectedLocker");
                        selectedLockerNum.setText(currentLocker.getText());
                    }
                });
            }
        }
    }

    public void moveToNextPage(){
        lockerArea.getChildren().clear();
        makeLockerArea(101);
        lockerAreaHeader.setText("2층");
        nextButton.setOnMouseClicked(mouseEvent -> {});
        nextButton.getStyleClass().remove("clickable");
        nextButton.getStyleClass().add("unclickable");
        previousButton.setOnMouseClicked(mouseEvent -> moveToPreviousPage());
        previousButton.getStyleClass().remove("unclickable");
        previousButton.getStyleClass().add("clickable");
    }

    public void moveToPreviousPage(){
        lockerArea.getChildren().clear();
        makeLockerArea(1);
        lockerAreaHeader.setText("1층");
        nextButton.setOnMouseClicked(mouseEvent -> moveToNextPage());
        nextButton.getStyleClass().remove("unclickable");
        nextButton.getStyleClass().add("clickable");
        previousButton.setOnMouseClicked(mouseEvent -> {});
        previousButton.getStyleClass().remove("clickable");
        previousButton.getStyleClass().add("unclickable");
    }

    public void comfirmButtonClicked(){
        String selectedLockerNumberText = selectedLockerNum.getText();

        if(selectedLockerNumberText.equals("")){
            showAlert("락커 번호를 선택하지 않았습니다", Alert.AlertType.ERROR);
            return;
        }

        int selectedLockerNumber = Integer.parseInt(selectedLockerNumberText);
        RadioButton selectedRadio = (RadioButton)lockerPeriodGroup.getSelectedToggle();

        if(selectedRadio == null){
            showAlert("기간을 선택하지 않았습니다", Alert.AlertType.ERROR);
            return;
        }

        String selectedLockerPeriodText = selectedRadio.getText();
        String[] lockerPeriodTexts = selectedLockerPeriodText.split("일");
        int selectedLockerPeriod = Integer.parseInt(lockerPeriodTexts[0]);

        System.out.println(selectedLockerNumber + "번 " + selectedLockerPeriod);
    }

    public void goBackButtonClicked(){

    }
}
