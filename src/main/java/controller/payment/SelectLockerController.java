package controller.payment;

import domain.member.Member;
import domain.member.UsingLocker;
import domain.payment.Available;
import domain.payment.Locker;
import domain.trainer.Trainer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import repository.PurchaseRepository;
import repository.TrainerRepository;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static controller.payment.PaymentController.*;
import static domain.member.SelectedMember.currentMember;
import static util.DialogUtil.showDialog;
import static util.MemberUtil.getTrainerNumForMember;
import static util.PageUtil.movePage;

public class SelectLockerController implements Initializable {
    private final TrainerRepository trainerRepository = new TrainerRepository();
    private final PurchaseRepository purchaseRepository = new PurchaseRepository();

    @FXML
    private HBox lockerArea;
    @FXML
    Label selectedLockerNum, lockerAreaHeader;
    @FXML
    Button nextButton, previousButton;
    @FXML
    RadioButton noSelectLockerButton, locker30Days, locker90Days, locker180Days, locker360Days;

    Member member;
    Trainer trainer;
    List<Button> lockers;
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
            noSelectLockerButton.setToggleGroup(lockerPeriodGroup);
            locker30Days.setToggleGroup(lockerPeriodGroup);
            locker90Days.setToggleGroup(lockerPeriodGroup);
            locker180Days.setToggleGroup(lockerPeriodGroup);
            locker360Days.setToggleGroup(lockerPeriodGroup);

            makeLockerList();
            makeLockerArea(1);
            makeOnclickListener();

            makeLockerArea(1);

            moveToNextPage();
            if(Integer.parseInt(selectedLockerNum.getText()) <= 100){
                moveToPreviousPage();
            }
        }
    }

    //현재 락커의 상태를 받아와서 리스트에 저장
    public void makeLockerList(){
        for(int i = 0; i < 200; i++){
            int lockerNum = lockers.size() + 1;
            Button newLocker = new Button(String.valueOf(lockerNum));
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

        // 이미 선택한 사물함이 있을 경우
        if(!basket.isEmpty()){
            for(Available ticket : basket){
                if(ticket instanceof Locker){
                    int number = ((Locker) ticket).getNum() - 1;
                    int period = ((Locker) ticket).getPeriod();

                    if(!selectedLockerNum.getText().isEmpty()){
                        int oldNumber = Integer.parseInt(selectedLockerNum.getText()) - 1;
                        lockers.get(oldNumber).getStyleClass().remove("selectedLocker");
                        lockers.get(oldNumber).getStyleClass().add("unselectedLocker");
                    }

                    lockers.get(number).getStyleClass().remove("unselectedLocker");
                    lockers.get(number).getStyleClass().add("selectedLocker");
                    selectedLockerNum.setText(String.valueOf(number + 1));

                    switch (period){
                        case 30:
                            locker30Days.setSelected(true);
                            break;
                        case 90:
                            locker90Days.setSelected(true);
                            break;
                        case 180:
                            locker180Days.setSelected(true);
                            break;
                        case 360:
                            locker360Days.setSelected(true);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    //현재 락커의 상태를 표 형식으로 출력
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
                    Button newLocker = lockers.get(startNum);
                    newRow.getChildren().add(newLocker);
                    startNum++;
                }
            }
        }
    }

    //락커를 선택하는 이벤트 생성
    public void makeOnclickListener(){
        for(int i = 0; i < lockers.size(); i++){
            Button currentLocker = lockers.get(i);
            if(!currentLocker.getStyleClass().contains("occupiedLocker")){
                currentLocker.setOnMouseClicked(mouseEvent -> {
                    String selectedLockerNumText = selectedLockerNum.getText();

                    if(selectedLockerNumText.isEmpty()){
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

    public void confirmButtonClicked(ActionEvent event) throws IOException {
        String selectedLockerNumberText = selectedLockerNum.getText();

        if(selectedLockerNumberText.isEmpty()){
            showDialog("락커 번호를 선택하지 않았습니다");
            return;
        }

        int selectedLockerNumber = Integer.parseInt(selectedLockerNumberText);
        RadioButton selectedRadio = (RadioButton)lockerPeriodGroup.getSelectedToggle();

        if(selectedRadio == null){
            showDialog("기간을 선택하지 않았습니다");
            return;
        }

        if (selectedRadio.getText().equals("선택 안 함")) {
            removeItem(basket, Locker.class);
            selectLocker = false;
            PaymentTab.getInstance().setSelectedTabIndex(2);
            movePage(event, "/view/member/payment");
        } else {
            String selectedLockerPeriodText = selectedRadio.getText();
            String[] lockerPeriodTexts = selectedLockerPeriodText.split("일");
            int selectedLockerPeriod = Integer.parseInt(lockerPeriodTexts[0]);
            String selectedLockerPrice = lockerPeriodTexts[1].replace(" ", "").replace(",", "")
                    .replace("원", "");
            int selectedLockerPriceNum = Integer.parseInt(selectedLockerPrice);

            removeItem(basket, Locker.class);
            basket.add(new Locker(selectedLockerPeriod, selectedLockerNumber, selectedLockerPriceNum));

            PaymentTab.getInstance().setSelectedTabIndex(2);
            selectLocker = true;
            movePage(event, "/view/member/payment");
        }
    }

    public void goBackButtonClicked(ActionEvent event) throws IOException {
        PaymentTab.getInstance().setSelectedTabIndex(2);
        movePage(event, "/view/member/payment");
    }
}
