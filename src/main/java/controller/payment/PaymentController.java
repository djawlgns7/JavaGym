package controller.payment;

import domain.payment.Available;
import domain.payment.GymTicket;
import domain.payment.PtTicket;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import repository.TrainerRepository;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

import static domain.member.SelectedMember.*;
import static domain.trainer.SelectedTrainer.currentTrainer;
import static util.ControllerUtil.createImageViewFromBytes;
import static util.MemberUtil.*;
import static util.PageUtil.*;

public class PaymentController implements Initializable {

    private static final ResourceBundle paymentConfig = ResourceBundle.getBundle("config.payment");
    public static boolean selectTrainer = false;

    private final TrainerRepository trainerRepository = new TrainerRepository();

    public static Set<Available> basket = new HashSet<>();

    /**
     * 화면 상단
     */
    @FXML
    Label memberNameLabel, itemTypeLabel, itemValueLabel, lockerLabel, currentLockerNumLabel, currentLockerPeriodLabel, clothesLabel, currentClothesSizeLabel, currentClothesPeriodLabel;

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

    /**
     * PT 이용권
     */
    @FXML
    ToggleGroup ptTicketRadio;

    @FXML
    RadioButton noSelectPtButton, pt10Button, pt20Button, pt30Button;

    @FXML
    Button selectTrainerButton;

    @FXML
    Label firstSelectTrainerLabel, ptPrice10Label, ptPrice20Label, ptPrice30Label, pt10Won, pt20Won, pt30Won, trainerNameLabel, trainerInfoLabel;

    @FXML
    ImageView selectTrainerImage;

    /**
     * 기타
     */

    /**
     * 장바구니
     */
    @FXML
    Label selectGymDayLabel, selectGymPriceLabel, selectTrainerNameLabel, selectPtTicketLabel, selectPtPriceLabel,
            selectLockerNumLabel, selectLockerPeriodLabel, selectLockerPriceLabel, selectClothesPeriodLabel, selectClothesPriceLabel;

    /**
     * 총 가격 정보
     */
    @FXML
    Label totalLabel, totalPriceLabel, totalWonLabel;

    int gymPrice, ptPrice, lockerPrice, clothesPrice, totalPrice = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // 회원의 현재 모든 결제 정보를 얻는다.
        Integer memberNum = currentMember.getNum();

        List<Integer> remains = getRemainAll(memberNum);
        int gymTicketRemain = remains.get(0);
        int ptTicketRemain = remains.get(1);
        int clothesRemain = remains.get(2);
        int lockerRemain = remains.get(3);
        int lockerNum = getLockerNum(memberNum);

        // 오늘을 기준으로 만료일을 얻는다.
        LocalDate today = LocalDate.now();
        LocalDate gymExpireDate = today.plusDays(gymTicketRemain);
        LocalDate lockerExpireDate = today.plusDays(lockerRemain);
        LocalDate clothesExpireDate = today.plusDays(clothesRemain);

        memberNameLabel.setText(currentMember.getName());

        // 처음에 보여지는 헬스장 이용권 탭 설정
        itemTypeLabel.setText("헬스장 이용권");
        if (gymTicketRemain == 0) {
            itemValueLabel.setText("현재 이용 중인 이용권이 없습니다.");
        } else {
            itemValueLabel.setText(gymExpireDate + " (D-" + gymTicketRemain + ")");
        }

        // 헬스장 이용권 가격 설정
        gymPrice1Label.setText("   " + paymentConfig.getString("gymPrice1Day"));
        gymPrice30Label.setText("   " + paymentConfig.getString("gymPrice30Day"));
        gymPrice90Label.setText("   " + paymentConfig.getString("gymPrice90Day"));
        gymPrice180Label.setText("   " + paymentConfig.getString("gymPrice180Day"));
        gymPrice360Label.setText("   " + paymentConfig.getString("gymPrice360Day"));

        // PT 이용권 가격 설정
        ptPrice10Label.setText("   " + paymentConfig.getString("ptPrice10Time"));
        ptPrice20Label.setText("   " + paymentConfig.getString("ptPrice20Time"));
        ptPrice30Label.setText("   " + paymentConfig.getString("ptPrice30Time"));

        // 기타 가격 설정
        /**
         *
         */

        // 상품 탭을 이동할 때마다 해당 상품에 대한 회원의 구매 정보 생성
        tab.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldTab, Tab newTab) {

                // 헬스장 이용권 탭
                if (newTab == gymTab) {
                    itemTypeLabel.setVisible(true);
                    itemValueLabel.setVisible(true);

                    itemTypeLabel.setText("헬스장 이용권");

                    if (gymTicketRemain == 0) {
                        itemValueLabel.setText("현재 이용 중인 이용권이 없습니다.");
                    } else {
                        itemValueLabel.setText(gymExpireDate + " (D-" + gymTicketRemain + ")");
                    }

                    lockerLabel.setVisible(false);
                    clothesLabel.setVisible(false);

                    currentLockerPeriodLabel.setVisible(false);
                    currentLockerNumLabel.setVisible(false);
                    currentClothesPeriodLabel.setVisible(false);
                    currentClothesSizeLabel.setVisible(false);


                    // PT 이용권 탭
                } else if (newTab == ptTab) {
                    itemTypeLabel.setVisible(true);
                    itemValueLabel.setVisible(true);
                    itemTypeLabel.setText("PT 이용권");

                    if (ptTicketRemain == 0) {
                        itemValueLabel.setText("현재 이용 중인 이용권이 없습니다.");
                    } else {
                        itemValueLabel.setText(ptTicketRemain + "개");
                    }

                    lockerLabel.setVisible(false);
                    clothesLabel.setVisible(false);

                    currentLockerPeriodLabel.setVisible(false);
                    currentLockerNumLabel.setVisible(false);
                    currentClothesPeriodLabel.setVisible(false);
                    currentClothesSizeLabel.setVisible(false);

                    // 기타 이용권 탭
                } else {
                    lockerLabel.setVisible(true);
                    currentLockerNumLabel.setVisible(true);
                    currentLockerPeriodLabel.setVisible(true);
                    clothesLabel.setVisible(true);
                    currentClothesSizeLabel.setVisible(true);
                    currentClothesPeriodLabel.setVisible(true);

                    if (lockerRemain == 0) {
                        currentLockerPeriodLabel.setText("현재 이용 중인 사물함이 없습니다.");
                        currentLockerNumLabel.setVisible(false);
                    } else {
                        currentLockerNumLabel.setText(lockerNum + "번");
                        currentLockerPeriodLabel.setText(lockerExpireDate + " (D-" + lockerRemain + ")");
                    }

                    if (clothesRemain == 0) {
                        currentClothesPeriodLabel.setText("현재 이용 중인 운동복이 없습니다.");
                        currentClothesSizeLabel.setVisible(false);
                    } else {
                        currentClothesPeriodLabel.setText(clothesExpireDate + " (D-" + clothesRemain + ")");
                    }

                    itemTypeLabel.setVisible(false);
                    itemValueLabel.setVisible(false);
                }
            }
        });


        if (!basket.isEmpty()) {
            for (Available ticket : basket) {
                if (ticket instanceof GymTicket) {
                    int period = ((GymTicket) ticket).getPeriod();
                    selectGymDayLabel.setVisible(true);
                    selectGymPriceLabel.setVisible(true);
                    switch (period) {
                        case 1:
                            gym1Button.setSelected(true);
                            gymPrice = 20000;

                            totalPriceLabel.setText(" " + String.format("%,d", gymPrice + ptPrice));
                            selectGymDayLabel.setText("1일");
                            selectGymPriceLabel.setText(gymPrice1Label.getText() + "원");
                            break;
                        case 30:
                            gym30Button.setSelected(true);
                            break;
                        case 90:
                            gym90Button.setSelected(true);
                            break;
                    }
                }

                if (ticket instanceof PtTicket) {
                    int time = ((PtTicket) ticket).getTime();
                    selectPtTicketLabel.setVisible(true);
                    selectPtPriceLabel.setVisible(true);
                    switch (time) {
                        case 10:
                            pt10Button.setSelected(true);
                            ptPrice = 700000;

                            totalPriceLabel.setText(" " + String.format("%,d", gymPrice + ptPrice));
                            selectPtTicketLabel.setText("10회");
                            selectPtPriceLabel.setText(ptPrice10Label.getText() + "원");
                            break;
                    }
                }
            }

        }

        // 초기에 사용하지 않는 라벨들을 숨긴다.
        lockerLabel.setVisible(false);
        currentLockerNumLabel.setVisible(false);

        clothesLabel.setVisible(false);
        currentClothesSizeLabel.setVisible(false);

        // 헬스장 이용권을 선택할 때마다 가격 업데이트
        gymTicketRadio.selectedToggleProperty().addListener((observable, oldVal, newVal) -> {
            if (newVal != null) {
                updateGymPrice((RadioButton) newVal);
            }
        });

        // 트레이너 선택 전
        if (!selectTrainer) {
            firstSelectTrainerLabel.setVisible(true); // 먼저 트레이너를 선택해 주세요.

            noSelectPtButton.setVisible(false);
            pt10Button.setVisible(false);
            pt20Button.setVisible(false);
            pt30Button.setVisible(false);
            noSelectPtButton.setVisible(false);
            ptPrice10Label.setVisible(false);
            ptPrice20Label.setVisible(false);
            ptPrice30Label.setVisible(false);
            pt10Won.setVisible(false);
            pt20Won.setVisible(false);
            pt30Won.setVisible(false);

        // 트레이너 선택 후
        } else {
            firstSelectTrainerLabel.setVisible(false);
            selectTrainerButton.setText("트레이너 변경"); // "트레이너 선택" 버튼 -> "트레이너 변경" 버튼
        }

        // PT 이용권을 선택할 때마다 가격 업데이트
        ptTicketRadio.selectedToggleProperty().addListener((observable, oldVal, newVal) -> {
            if (newVal != null) {
                updatePtPrice((RadioButton) newVal);
            }
        });

        if (currentTrainer != null) {
            ImageView image = createImageViewFromBytes(currentTrainer.getPhoto());
            selectTrainerImage.setImage(image.getImage());
            trainerNameLabel.setText(currentTrainer.getName());
            trainerInfoLabel.setText(currentTrainer.getHeight() + "|" + currentTrainer.getWeight() + "|" + trainerRepository.getAge(currentTrainer) + "세");
        }

        int tabIndex = PaymentTab.getInstance().getSelectedTabIndex();
        tab.getSelectionModel().select(tabIndex);
    } // initialize() 끝

    // 헬스장 이용권
    private void updateGymPrice(RadioButton selectedButton) {
        String selected = selectedButton.getText();

        String gym1Price = gymPrice1Label.getText();
        String gym30Price = gymPrice30Label.getText();
        String gym90Price = gymPrice90Label.getText();
        String gym180Price = gymPrice180Label.getText();
        String gym360Price = gymPrice360Label.getText();

        switch (selected) {
            case "1일":
                gymPrice = 20000;
                selectGymDayLabel.setVisible(true);
                selectGymPriceLabel.setVisible(true);

                totalPriceLabel.setText(" " + String.format("%,d", gymPrice + ptPrice));
                selectGymDayLabel.setText("1일");
                selectGymPriceLabel.setText(gym1Price + "원");

                removeItem(basket, GymTicket.class);
                basket.add(new GymTicket(1, 20000));
                break;

            case "30일":
                gymPrice = 50000;
                selectGymDayLabel.setVisible(true);
                selectGymPriceLabel.setVisible(true);

                totalPriceLabel.setText(" " + String.format("%,d", gymPrice + ptPrice));
                selectGymDayLabel.setText("30일");
                selectGymPriceLabel.setText(gym30Price + "원");

                removeItem(basket, GymTicket.class);
                basket.add(new GymTicket(30, 50000));
                break;

            case "90일":
                gymPrice = 150000;
                selectGymDayLabel.setVisible(true);
                selectGymPriceLabel.setVisible(true);

                totalPriceLabel.setText(" " + String.format("%,d", gymPrice + ptPrice));
                selectGymDayLabel.setText("90일");
                selectGymPriceLabel.setText(gym90Price + "원");

                removeItem(basket, GymTicket.class);
                basket.add(new GymTicket(90, 50000));
                break;
            case "180일":
                gymPrice = 280000;
                selectGymDayLabel.setVisible(true);
                selectGymPriceLabel.setVisible(true);

                totalPriceLabel.setText(" " + String.format("%,d", gymPrice + ptPrice));
                selectGymDayLabel.setText("180일");
                selectGymPriceLabel.setText(gym180Price + "원");

                removeItem(basket, GymTicket.class);
                basket.add(new GymTicket(180, 280000));
                break;
            case "360일":
                gymPrice = 510000;
                selectGymDayLabel.setVisible(true);
                selectGymPriceLabel.setVisible(true);

                totalPriceLabel.setText(" " + String.format("%,d", gymPrice + ptPrice));
                selectGymDayLabel.setText("360일");
                selectGymPriceLabel.setText(gym360Price + "원");

                removeItem(basket, GymTicket.class);
                basket.add(new GymTicket(360, 510000));
                break;
            default:
                gymPrice = 0;
                totalPriceLabel.setText(" " + (gymPrice + ptPrice));
                selectGymPriceLabel.setVisible(false);
                selectGymDayLabel.setVisible(false);
                removeItem(basket, GymTicket.class);
        }
    }

    // PT 이용권
    @FXML
    private void selectTrainer(ActionEvent event) throws IOException {
        movePageCenter(event, "/view/member/selectTrainer");
    }

    private void updatePtPrice(RadioButton selectedButton) {
        String selected = selectedButton.getText();

        String pt1Price = ptPrice10Label.getText();
        String pt20Price = ptPrice20Label.getText();
        String pt30Price = ptPrice30Label.getText();

        switch (selected) {
            case "10회":
                ptPrice = 700000;
                selectPtTicketLabel.setVisible(true);
                selectPtPriceLabel.setVisible(true);

                totalPriceLabel.setText(" " + String.format("%,d", gymPrice + ptPrice));
                selectPtTicketLabel.setText("10회");
                selectPtPriceLabel.setText(pt1Price + "원");

                removeItem(basket, PtTicket.class);
                basket.add(new PtTicket(10, 700000));
                break;
            case "20회":
                ptPrice = 1300000;
                selectPtTicketLabel.setVisible(true);
                selectPtPriceLabel.setVisible(true);

                totalPriceLabel.setText(" " + String.format("%,d", gymPrice + ptPrice));
                selectPtTicketLabel.setText("20회");
                selectPtPriceLabel.setText(pt20Price + "원");

                removeItem(basket, PtTicket.class);
                basket.add(new PtTicket(20, 1300000));
                break;
            case "30회":
                ptPrice = 1800000;
                selectPtTicketLabel.setVisible(true);
                selectPtPriceLabel.setVisible(true);

                totalPriceLabel.setText(" " + String.format("%,d", gymPrice + ptPrice));
                selectPtTicketLabel.setText("30회");
                selectPtPriceLabel.setText(pt30Price + "원");

                removeItem(basket, PtTicket.class);
                basket.add(new PtTicket(30, 1800000));
                break;
            default:
                ptPrice = 0;
                totalPriceLabel.setText(" " + (gymPrice + ptPrice));
                selectPtTicketLabel.setVisible(false);
                selectPtPriceLabel.setVisible(false);
                removeItem(basket, PtTicket.class);
        }
    }

    @FXML
    private void cancelPayment(ActionEvent event) throws IOException {
        selectTrainer = false;
        currentTrainer = null;
        basket.clear();
        movePageCenter(event, "/view/member/helloMember");
    }

    @FXML
    private void payment(ActionEvent event) {

    }

    public static void removeItem(Set<Available> basket, Class<?> type) {
        basket.removeIf(ticket -> type.isInstance(ticket));
    }

    public static void printBasket() {
        basket.forEach(ticket -> System.out.println(ticket));
    }
}