package controller.member;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import static domain.member.SelectedMember.*;
import static util.MemberUtil.*;
import static util.PageUtil.*;

public class PaymentController implements Initializable {

    private static final ResourceBundle paymentConfig = ResourceBundle.getBundle("config.payment");

    /**
     * 화면 상단
     */
    @FXML
    Label memberNameLabel, itemTypeLabel, itemValueLabel, currentLockerLabel, currentLockerNumLabel, currentLockerPeriodLabel, currentClothesLabel, currentClothesSizeLabel, currentClothesPeriodLabel;

    @FXML
    TabPane tab;

    @FXML
    Tab gymTab, ptTab, etcTab;

    /**
     * 헬스장 이용권
     */
    @FXML
    RadioButton noSelectGymButton, gym1Button, gym30Button, gym90Button, gym180Button, gym360Button;

    @FXML
    ToggleGroup gymTicketRadio;

    @FXML
    Label gymPrice1Label, gymPrice30Label, gymPrice90Label, gymPrice180Label, gymPrice360Label;

    int gymPrice = 0;

    /**
     * PT 이용권
     */
    @FXML
    ToggleGroup ptTicketRadio;

    @FXML
    RadioButton noSelectPtButton, pt10Button, pt20Button, pt30Button;

    @FXML
    Label ptPrice10Label, ptPrice20Label, ptPrice30Label;

    int ptPrice = 0;

    /**
     * 기타
     */

    /**
     * 화면 하단
     */
    @FXML
    Label selectGymDayLabel, selectGymPriceLabel, gymWonLabel,
            selectTrainerNameLabel, selectPtTicketLabel, selectPtPriceLabel, ptWonLabel,
            selectLockerNumLabel, selectLockerPeriodLabel, selectLockerPriceLabel, lockerWonLabel,
            selectClothesPeriodLabel, selectClothesPriceLabel, clothesWonLabel;

    @FXML
    Label totalLabel, priceLabel, totalWonLabel;

    int totalPrice = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        totalPrice = 0;
        Integer memberNum = currentMember.getNum();

        List<Integer> remains = getRemainAll(memberNum);
        int gymTicket = remains.get(0);
        int ptTicket = remains.get(1);
        int clothes = remains.get(2);
        int locker = remains.get(3);

        int lockerNum = getLockerNum(memberNum);

        LocalDate today = LocalDate.now();
        LocalDate gymExpireDate = today.plusDays(gymTicket);
        LocalDate lockerExpireDate = today.plusDays(locker);
        LocalDate clothesExpireDate = today.plusDays(clothes);

        memberNameLabel.setText(currentMember.getName());
        itemTypeLabel.setText("헬스장 이용권 종료 기간");
        itemValueLabel.setText(gymExpireDate + " (D-" + gymTicket + ")");

        currentLockerLabel.setVisible(false);
        currentLockerNumLabel.setVisible(false);
        currentClothesLabel.setVisible(false);
        currentClothesSizeLabel.setVisible(false);
        gymWonLabel.setVisible(false);
        ptWonLabel.setVisible(false);
        lockerWonLabel.setVisible(false);
        clothesWonLabel.setVisible(false);

        // 헬스장 이용권 가격 설정
        gymPrice1Label.setText("   " + paymentConfig.getString("gymPrice1Day"));
        gymPrice30Label.setText("   " + paymentConfig.getString("gymPrice30Day"));
        gymPrice90Label.setText("   " + paymentConfig.getString("gymPrice90Day"));
        gymPrice180Label.setText("   " + paymentConfig.getString("gymPrice180Day"));
        gymPrice360Label.setText("   " + paymentConfig.getString("gymPrice360Day"));

        // PT 이용권 가격 설정
        ptPrice10Label.setText("   " + paymentConfig.getString("ptPrice10"));
        ptPrice20Label.setText("   " + paymentConfig.getString("ptPrice20"));
        ptPrice30Label.setText("   " + paymentConfig.getString("ptPrice30"));

        // 기타 가격 설정

        // 상품 탭을 이동할 때마다 상품에 대한 회원의 정보 생성
        tab.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldTab, Tab newTab) {
                if (newTab == gymTab) {
                    itemTypeLabel.setVisible(true);
                    itemTypeLabel.setText("헬스장 이용권 종료 기간");
                    itemValueLabel.setText(lockerExpireDate + " (D-" + gymTicket + ")");
                    currentLockerLabel.setVisible(false);
                    currentClothesLabel.setVisible(false);
                    itemValueLabel.setVisible(true);
                    currentClothesSizeLabel.setVisible(false);
                    currentLockerNumLabel.setVisible(false);
                    currentLockerPeriodLabel.setVisible(false);
                    currentClothesPeriodLabel.setVisible(false);
                } else if (newTab == ptTab) {
                    itemTypeLabel.setVisible(true);
                    itemTypeLabel.setText("PT 이용권 잔여 개수");
                    itemValueLabel.setText(String.valueOf(ptTicket) + "개");
                    currentLockerLabel.setVisible(false);
                    currentClothesLabel.setVisible(false);
                    itemValueLabel.setVisible(true);
                    currentClothesSizeLabel.setVisible(false);
                    currentLockerNumLabel.setVisible(false);
                    currentLockerPeriodLabel.setVisible(false);
                    currentClothesPeriodLabel.setVisible(false);
                } else {
                    itemTypeLabel.setVisible(false);
                    currentLockerLabel.setVisible(true);
                    currentClothesLabel.setVisible(true);
                    itemValueLabel.setVisible(false);
                    currentClothesSizeLabel.setVisible(true);
                    currentLockerNumLabel.setVisible(true);
                    currentLockerPeriodLabel.setVisible(true);
                    currentClothesPeriodLabel.setVisible(true);
                    currentLockerNumLabel.setText(String.valueOf(lockerNum) + "번");
                    currentLockerPeriodLabel.setText(lockerExpireDate + " (D-" + locker + ")");
                    currentClothesPeriodLabel.setText(clothesExpireDate + " (D-" + clothes + ")");
                }
            }
        });

        gymTicketRadio.selectedToggleProperty().addListener((observable, oldVal, newVal) -> {
            if (newVal != null) {
                updateGymPrice((RadioButton) newVal);
                priceLabel.setVisible(true);
                totalWonLabel.setVisible(true);
            }
        });
    }

    private void updateGymPrice(RadioButton selectedRadioButton) {
        String selected = selectedRadioButton.getText();
        switch (selected) {
            case "1일":
                gymPrice = Integer.parseInt(gymPrice1Label.getText());
                priceLabel.setText(String.valueOf(gymPrice + ptPrice));
                priceLabel.setVisible(true);
                selectGymDayLabel.setVisible(true);
                selectGymDayLabel.setText(gymPrice1Label.getText());
                selectGymPriceLabel.setVisible(true);
                gymWonLabel.setVisible(true);
                selectGymPriceLabel.setText(gymPrice1Label.getText());
                break;
            case "30일":
                gymPrice = Integer.parseInt(gymPrice30Label.getText());
                priceLabel.setText(gymPrice30Label.getText());
                selectGymDayLabel.setVisible(true);
                selectGymDayLabel.setText(gym30Button.getText());
                selectGymPriceLabel.setVisible(true);
                gymWonLabel.setVisible(true);
                selectGymPriceLabel.setText(gymPrice30Label.getText());
                break;
            case "90일":
                gymPrice = Integer.parseInt(gymPrice90Label.getText());
                priceLabel.setText(gymPrice90Label.getText());
                selectGymDayLabel.setVisible(true);
                selectGymDayLabel.setText(gym90Button.getText());
                selectGymPriceLabel.setVisible(true);
                gymWonLabel.setVisible(true);
                selectGymPriceLabel.setText(gymPrice90Label.getText());
                break;
            case "180일":
                gymPrice = Integer.parseInt(gymPrice180Label.getText());
                priceLabel.setText(gymPrice180Label.getText());
                selectGymDayLabel.setVisible(true);
                selectGymDayLabel.setText(gym180Button.getText());
                selectGymPriceLabel.setVisible(true);
                gymWonLabel.setVisible(true);
                selectGymPriceLabel.setText(gymPrice180Label.getText());
                break;
            case "360일":
                gymPrice = Integer.parseInt(gymPrice360Label.getText());
                priceLabel.setText(gymPrice360Label.getText());
                selectGymDayLabel.setVisible(true);
                selectGymDayLabel.setText(gym360Button.getText());
                selectGymPriceLabel.setVisible(true);
                gymWonLabel.setVisible(true);
                selectGymPriceLabel.setText(gymPrice360Label.getText());
                break;
            default:
                gymPrice = 0;
                priceLabel.setText(" 0");
                selectGymPriceLabel.setVisible(false);
                selectGymDayLabel.setVisible(false);
                gymWonLabel.setVisible(false);
        }
    }

    @FXML
    private void cancelPayment(ActionEvent event) throws IOException {
        movePageCenter(event, "/view/member/helloMember");
    }

    @FXML
    private void payment(ActionEvent event) {

    }
}