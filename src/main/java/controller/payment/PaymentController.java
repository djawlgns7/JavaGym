package controller.payment;

import domain.Item;
import domain.payment.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import repository.TrainerRepository;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

import static domain.member.SelectedMember.currentMember;
import static domain.trainer.SelectedTrainer.currentTrainer;
import static util.DialogUtil.*;
import static util.ControllerUtil.createImageViewFromBytes;
import static util.MemberUtil.*;
import static util.PageUtil.movePage;
import static util.PageUtil.moveToMainPage;
import static util.PurchaseUtil.purchaseItem;
import static util.SoundUtil.*;

public class PaymentController implements Initializable {

    public static boolean selectTrainer = false;

    private final TrainerRepository trainerRepository = new TrainerRepository();

    public static Set<Available> basket = new HashSet<>();

    /**
     * 화면 상단
     */
    @FXML
    Label memberNameLabel, itemTypeLabel, itemValueLabel, lockerLabel, currentLockerNumLabel, currentLockerPeriodLabel,
            clothesLabel, currentClothesSizeLabel, currentClothesPeriodLabel;

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

    /**
     * PT 이용권
     */
    @FXML
    ToggleGroup ptTicketRadio;

    @FXML
    VBox ptSelectBox;

    @FXML
    RadioButton noSelectPtButton, pt10Button, pt20Button, pt30Button;

    @FXML
    Button selectTrainerButton;

    @FXML
    Label firstSelectTrainerLabel, trainerNameLabel, trainerInfoLabel;

    @FXML
    ImageView selectTrainerImage;

    /**
     * 기타 (사물함, 운동복)
     */
    @FXML
    Label noSelectLockerLabel, lockerNumberLabel, lockerPeriodLabel, lockerPriceLabel;

    @FXML
    ToggleGroup clothesRadio;

    @FXML
    RadioButton noSelectClothesButton, clothes30Button, clothes90Button, clothes180Button, clothes360Button;

    /**
     * 장바구니
     */
    @FXML
    Label selectGymTypeLabel, selectGymDayLabel, selectGymPriceLabel, selectTrainerNameLabel, selectPtTicketLabel, selectPtPriceLabel,
            selectLockerNumLabel, selectLockerPeriodLabel, selectLockerPriceLabel, selectClothesSizeLabel, selectClothesPeriodLabel, selectClothesPriceLabel;

    /**
     * 총 가격 정보
     */
    @FXML
    Label totalPriceLabel;

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
        itemTypeLabel.setText("헬스장 이용 가능 기간");
        if (gymTicketRemain == 0) {
            itemValueLabel.setText("현재 이용 중인 이용권이 없습니다.");
        } else {
            itemValueLabel.setText(gymExpireDate + " (D-" + gymTicketRemain + ")");
        }

        // 상품 탭을 이동할 때마다 해당 상품에 대한 회원의 구매 정보 생성
        tab.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldTab, Tab newTab) {

                // 헬스장 이용권 탭
                if (newTab == gymTab) {
                    itemTypeLabel.setVisible(true);
                    itemValueLabel.setVisible(true);

                    itemTypeLabel.setText("헬스장 이용 가능 기간");

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
                    itemTypeLabel.setText("PT 이용 가능 횟수");

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
                    selectGymTypeLabel.setVisible(true);
                    selectGymDayLabel.setVisible(true);
                    selectGymPriceLabel.setVisible(true);
                    switch (period) {
                        case 1:
                            gym1Button.setSelected(true);
                            gymPrice = 20000;

                            totalPriceLabel.setText(" " + String.format("%,d", gymPrice + ptPrice + lockerPrice + clothesPrice));
                            selectGymTypeLabel.setText("일일권");
                            selectGymDayLabel.setText("1일");
                            selectGymPriceLabel.setText("20,000원");
                            break;
                        case 30:
                            gym30Button.setSelected(true);
                            gymPrice = 50000;

                            totalPriceLabel.setText(" " + String.format("%,d", gymPrice + ptPrice + lockerPrice + clothesPrice));
                            selectGymTypeLabel.setText("월간권");
                            selectGymDayLabel.setText("30일");
                            selectGymPriceLabel.setText("50,000원");
                            break;
                        case 90:
                            gym90Button.setSelected(true);
                            gymPrice = 150000;

                            totalPriceLabel.setText(" " + String.format("%,d", gymPrice + ptPrice + lockerPrice + clothesPrice));
                            selectGymTypeLabel.setText("월간권");
                            selectGymDayLabel.setText("90일");
                            selectGymPriceLabel.setText("150,000원");
                            break;
                        case 180:
                            gym180Button.setSelected(true);
                            gymPrice = 280000;

                            totalPriceLabel.setText(" " + String.format("%,d", gymPrice + ptPrice + lockerPrice + clothesPrice));
                            selectGymTypeLabel.setText("월간권");
                            selectGymDayLabel.setText("180일");
                            selectGymPriceLabel.setText("280,000원");
                            break;
                        case 360:
                            gym360Button.setSelected(true);
                            gymPrice = 510000;

                            totalPriceLabel.setText(" " + String.format("%,d", gymPrice + ptPrice + lockerPrice + clothesPrice));
                            selectGymTypeLabel.setText("월간권");
                            selectGymDayLabel.setText("360일");
                            selectGymPriceLabel.setText("510,000원");
                            break;
                    }
                }

                if (ticket instanceof PtTicket) {
                    int time = ((PtTicket) ticket).getTime();
                    selectPtTicketLabel.setVisible(true);
                    selectPtPriceLabel.setVisible(true);
                    selectTrainerNameLabel.setVisible(true);
                    switch (time) {
                        case 10:
                            pt10Button.setSelected(true);
                            ptPrice = 700000;

                            totalPriceLabel.setText(" " + String.format("%,d", gymPrice + ptPrice + lockerPrice + clothesPrice));
                            selectPtTicketLabel.setText("10회");
                            selectTrainerNameLabel.setText(currentTrainer.getName() + " 트레이너");
                            selectPtPriceLabel.setText("700,000원");
                            break;
                        case 20:
                            pt20Button.setSelected(true);
                            ptPrice = 1300000;

                            totalPriceLabel.setText(" " + String.format("%,d", gymPrice + ptPrice + lockerPrice + clothesPrice));
                            selectPtTicketLabel.setText("20회");
                            selectTrainerNameLabel.setText(currentTrainer.getName() + " 트레이너");
                            selectPtPriceLabel.setText("1,300,000원");
                            break;
                        case 30:
                            pt30Button.setSelected(true);
                            ptPrice = 1800000;

                            totalPriceLabel.setText(" " + String.format("%,d", gymPrice + ptPrice + lockerPrice + clothesPrice));
                            selectPtTicketLabel.setText("30회");
                            selectTrainerNameLabel.setText(currentTrainer.getName() + " 트레이너");
                            selectPtPriceLabel.setText("1,800,000원");
                            break;
                    }
                }

                if (ticket instanceof Locker) {
                    int period = ((Locker) ticket).getPeriod();
                    int number = ((Locker) ticket).getNum();
                    lockerPrice = ((Locker) ticket).getPrice();
                    String lockerPriceText = lockerPrice / 1000 + ",000원";

                    noSelectLockerLabel.setVisible(false);

                    selectLockerPeriodLabel.setVisible(true);
                    selectLockerPriceLabel.setVisible(true);
                    selectLockerNumLabel.setVisible(true);
                    lockerNumberLabel.setVisible(true);
                    lockerPeriodLabel.setVisible(true);
                    lockerPriceLabel.setVisible(true);

                    lockerNumberLabel.setText("No." + number);
                    lockerPeriodLabel.setText(period + "일");
                    lockerPriceLabel.setText(lockerPriceText);

                    totalPriceLabel.setText(" " + String.format("%,d", gymPrice + ptPrice + lockerPrice + clothesPrice));
                    selectLockerPeriodLabel.setText(period + "일");
                    selectLockerNumLabel.setText(number + "번");
                    selectLockerPriceLabel.setText(lockerPriceText);
                }

                if (ticket instanceof Clothes) {
                    int period = ((Clothes) ticket).getPeriod();
                    clothesPrice = ((Clothes) ticket).getPrice();
                    String clothesPriceText = clothesPrice / 1000 + ",000원";

                    selectClothesPeriodLabel.setVisible(true);
                    selectClothesPriceLabel.setVisible(true);
                    selectClothesSizeLabel.setVisible(true);

                    totalPriceLabel.setText(" " + String.format("%,d", gymPrice + ptPrice + lockerPrice + clothesPrice));
                    selectClothesPeriodLabel.setText(period + "일");
                    selectClothesSizeLabel.setText("Free Size");
                    selectClothesPriceLabel.setText(clothesPriceText);

                    switch(period){
                        case 30:
                            clothes30Button.setSelected(true);
                            break;
                        case 90:
                            clothes90Button.setSelected(true);
                            break;
                        case 180:
                            clothes180Button.setSelected(true);
                            break;
                        case 360:
                            clothes360Button.setSelected(true);
                            break;
                        default:
                            noSelectClothesButton.setSelected(true);
                    }
                }
            }
        }

        // 초기에 사용하지 않는 라벨들을 숨긴다.
        lockerLabel.setVisible(false);
        currentLockerNumLabel.setVisible(false);

        clothesLabel.setVisible(false);
        currentClothesSizeLabel.setVisible(false);

        // 트레이너 선택 전
        if (!selectTrainer) {
            firstSelectTrainerLabel.setVisible(true); // 먼저 트레이너를 선택해 주세요.
            ptSelectBox.setVisible(false);

            // 트레이너 선택 후
        } else {
            firstSelectTrainerLabel.setVisible(false);
            selectTrainerButton.setText("트레이너 변경"); // "트레이너 선택" 버튼 -> "트레이너 변경" 버튼
        }

        if (currentTrainer != null) {
            ImageView image = createImageViewFromBytes(currentTrainer.getPhoto());
            selectTrainerImage.setImage(image.getImage());
            trainerNameLabel.setText(currentTrainer.getName());
            trainerInfoLabel.setText(currentTrainer.getHeight() + "|" + currentTrainer.getWeight() + "|" + trainerRepository.getAge(currentTrainer) + "세");
        }

        // 헬스장 이용권을 선택할 때마다 가격 업데이트
        gymTicketRadio.selectedToggleProperty().addListener((observable, oldVal, newVal) -> {
            if (newVal != null) {
                updateGymPrice((RadioButton) newVal);
            }
        });

        // PT 이용권을 선택할 때마다 가격 업데이트
        ptTicketRadio.selectedToggleProperty().addListener((observable, oldVal, newVal) -> {
            if (newVal != null) {
                updatePtPrice((RadioButton) newVal);
            }
        });

        // 운동복 이용권을 선택할 때마다 가격 업데이트
        clothesRadio.selectedToggleProperty().addListener((observable, oldVal, newVal) -> {
            if (newVal != null) {
                updateClothesPrice((RadioButton) newVal);
            }
        });

        int tabIndex = PaymentTab.getInstance().getSelectedTabIndex();
        tab.getSelectionModel().select(tabIndex);
    } // initialize() 끝

    private void updateGymPrice(RadioButton selectedButton) {
        String selected = selectedButton.getText();

        switch (selected) {
            case "1일":
                gymPrice = 20000;
                selectGymDayLabel.setVisible(true);
                selectGymPriceLabel.setVisible(true);
                selectGymTypeLabel.setVisible(true);

                totalPriceLabel.setText(" " + String.format("%,d", gymPrice + ptPrice + lockerPrice + clothesPrice));
                selectGymTypeLabel.setText("일일권");
                selectGymDayLabel.setText("1일");
                selectGymPriceLabel.setText("20,000원");

                removeItem(basket, GymTicket.class);
                basket.add(new GymTicket(1, 20000));
                break;

            case "30일":
                gymPrice = 50000;
                selectGymDayLabel.setVisible(true);
                selectGymPriceLabel.setVisible(true);
                selectGymTypeLabel.setVisible(true);

                totalPriceLabel.setText(" " + String.format("%,d", gymPrice + ptPrice + lockerPrice + clothesPrice));
                selectGymTypeLabel.setText("월간권");
                selectGymDayLabel.setText("30일");
                selectGymPriceLabel.setText("50,000원");

                removeItem(basket, GymTicket.class);
                basket.add(new GymTicket(30, 50000));
                break;

            case "90일":
                gymPrice = 150000;
                selectGymDayLabel.setVisible(true);
                selectGymPriceLabel.setVisible(true);
                selectGymTypeLabel.setVisible(true);

                totalPriceLabel.setText(" " + String.format("%,d", gymPrice + ptPrice + lockerPrice + clothesPrice));
                selectGymTypeLabel.setText("월간권");
                selectGymDayLabel.setText("90일");
                selectGymPriceLabel.setText("150,000원");

                removeItem(basket, GymTicket.class);
                basket.add(new GymTicket(90, 150000));
                break;
            case "180일":
                gymPrice = 280000;
                selectGymDayLabel.setVisible(true);
                selectGymPriceLabel.setVisible(true);
                selectGymTypeLabel.setVisible(true);

                totalPriceLabel.setText(" " + String.format("%,d", gymPrice + ptPrice + lockerPrice + clothesPrice));
                selectGymTypeLabel.setText("월간권");
                selectGymDayLabel.setText("180일");
                selectGymPriceLabel.setText("280,000원");

                removeItem(basket, GymTicket.class);
                basket.add(new GymTicket(180, 280000));
                break;
            case "360일":
                gymPrice = 510000;
                selectGymDayLabel.setVisible(true);
                selectGymPriceLabel.setVisible(true);
                selectGymTypeLabel.setVisible(true);

                totalPriceLabel.setText(" " + String.format("%,d", gymPrice + ptPrice + lockerPrice + clothesPrice));
                selectGymTypeLabel.setText("월간권");
                selectGymDayLabel.setText("360일");
                selectGymPriceLabel.setText("510,000원");

                removeItem(basket, GymTicket.class);
                basket.add(new GymTicket(360, 510000));
                break;
            default:
                gymPrice = 0;
                totalPriceLabel.setText(" " + String.format("%,d", gymPrice + ptPrice + lockerPrice + clothesPrice));
                selectGymTypeLabel.setVisible(false);
                selectGymPriceLabel.setVisible(false);
                selectGymDayLabel.setVisible(false);
                removeItem(basket, GymTicket.class);
        }
    }

    // PT 이용권
    @FXML
    private void selectTrainer(ActionEvent event) throws IOException {
        movePage(event, "/view/member/selectTrainer");
    }

    private void updatePtPrice(RadioButton selectedButton) {
        String selected = selectedButton.getText();

        switch (selected) {
            case "10회":
                ptPrice = 700000;
                selectPtTicketLabel.setVisible(true);
                selectPtPriceLabel.setVisible(true);
                selectTrainerNameLabel.setVisible(true);
                selectTrainerNameLabel.setText(currentTrainer.getName() + " 트레이너");

                totalPriceLabel.setText(" " + String.format("%,d", gymPrice + ptPrice + lockerPrice + clothesPrice));
                selectPtTicketLabel.setText("10회");
                selectPtPriceLabel.setText("700,000원");

                removeItem(basket, PtTicket.class);
                basket.add(new PtTicket(10, 700000));
                break;
            case "20회":
                ptPrice = 1300000;
                selectPtTicketLabel.setVisible(true);
                selectPtPriceLabel.setVisible(true);
                selectTrainerNameLabel.setVisible(true);
                selectTrainerNameLabel.setText(currentTrainer.getName() + " 트레이너");

                totalPriceLabel.setText(" " + String.format("%,d", gymPrice + ptPrice + lockerPrice + clothesPrice));
                selectPtTicketLabel.setText("20회");
                selectPtPriceLabel.setText("1,300,000원");

                removeItem(basket, PtTicket.class);
                basket.add(new PtTicket(20, 1300000));
                break;
            case "30회":
                ptPrice = 1800000;
                selectPtTicketLabel.setVisible(true);
                selectPtPriceLabel.setVisible(true);
                selectTrainerNameLabel.setVisible(true);
                selectTrainerNameLabel.setText(currentTrainer.getName() + " 트레이너");

                totalPriceLabel.setText(" " + String.format("%,d", gymPrice + ptPrice + lockerPrice + clothesPrice));
                selectPtTicketLabel.setText("30회");
                selectPtPriceLabel.setText("1,800,000원");

                removeItem(basket, PtTicket.class);
                basket.add(new PtTicket(30, 1800000));
                break;
            default:
                ptPrice = 0;
                totalPriceLabel.setText(" " + String.format("%,d", gymPrice + ptPrice + lockerPrice + clothesPrice));
                selectPtTicketLabel.setVisible(false);
                selectPtPriceLabel.setVisible(false);
                selectTrainerNameLabel.setVisible(false);
                removeItem(basket, PtTicket.class);
        }
    }

    @FXML
    private void selectLocker(ActionEvent event) throws IOException {
        movePage(event, "/view/member/selectLocker");
    }

    private void updateClothesPrice(RadioButton selectedButton) {
        String selected = selectedButton.getText();

        switch (selected) {
            case "30일":
                clothesPrice = 7000;
                selectClothesPeriodLabel.setVisible(true);
                selectClothesPriceLabel.setVisible(true);
                selectClothesSizeLabel.setVisible(true);

                totalPriceLabel.setText(" " + String.format("%,d", gymPrice + ptPrice + lockerPrice + clothesPrice));
                selectClothesPeriodLabel.setText("30일");
                selectClothesSizeLabel.setText("Free Size");
                selectClothesPriceLabel.setText("7,000원");

                removeItem(basket, Clothes.class);
                basket.add(new Clothes(30, 7000));
                break;

            case "90일":
                clothesPrice = 18000;
                selectClothesPeriodLabel.setVisible(true);
                selectClothesPriceLabel.setVisible(true);
                selectClothesSizeLabel.setVisible(true);

                totalPriceLabel.setText(" " + String.format("%,d", gymPrice + ptPrice + lockerPrice + clothesPrice));
                selectClothesPeriodLabel.setText("90일");
                selectClothesSizeLabel.setText("Free Size");
                selectClothesPriceLabel.setText("18,000원");

                removeItem(basket, Clothes.class);
                basket.add(new Clothes(90, 18000));
                break;
            case "180일":
                clothesPrice = 31000;
                selectClothesPeriodLabel.setVisible(true);
                selectClothesPriceLabel.setVisible(true);
                selectClothesSizeLabel.setVisible(true);

                totalPriceLabel.setText(" " + String.format("%,d", gymPrice + ptPrice + lockerPrice + clothesPrice));
                selectClothesPeriodLabel.setText("180일");
                selectClothesSizeLabel.setText("Free Size");
                selectClothesPriceLabel.setText("31,000원");

                removeItem(basket, Clothes.class);
                basket.add(new Clothes(180, 31000));
                break;
            case "360일":
                clothesPrice = 52000;
                selectClothesPeriodLabel.setVisible(true);
                selectClothesPriceLabel.setVisible(true);
                selectClothesSizeLabel.setVisible(true);

                totalPriceLabel.setText(" " + String.format("%,d", gymPrice + ptPrice + lockerPrice + clothesPrice));
                selectClothesPeriodLabel.setText("360일");
                selectClothesSizeLabel.setText("Free Size");
                selectClothesPriceLabel.setText("52,000원");

                removeItem(basket, Clothes.class);
                basket.add(new Clothes(360, 52000));
                break;
            default:
                clothesPrice = 0;
                totalPriceLabel.setText(" " + String.format("%,d", gymPrice + ptPrice + lockerPrice + clothesPrice));
                selectClothesPeriodLabel.setVisible(false);
                selectClothesPriceLabel.setVisible(false);
                selectClothesSizeLabel.setVisible(false);
                removeItem(basket, Clothes.class);
        }
    }

    @FXML
    private void cancelPayment(ActionEvent event) throws IOException {
        selectTrainer = false;
        currentTrainer = null;
        basket.clear();
        movePage(event, "/view/member/helloMember");
    }

    @FXML
    private void payment(ActionEvent event) throws IOException {
        if(basket.isEmpty()){
            showDialog("물품을 하나 이상 선택해 주세요");
            return;
        }

        String totalPriceText;

        play("checkPayment");

        totalPrice = gymPrice + ptPrice + lockerPrice + clothesPrice;
        if(totalPrice >= 1000000){
            totalPriceText = "\n총 " + (totalPrice / 1000000) + "," + (totalPrice % 1000000 / 1000) + ",000원";
        }else{
            totalPriceText = "\n총 " +  (totalPrice / 1000) + ",000원";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("결제를 정상 진행하시겠습니까?\n\n");

        for(Available ticket : basket){
            if(ticket instanceof GymTicket){
                int period = ((GymTicket)ticket).getPeriod();
                int price = ((GymTicket)ticket).getPrice();
                String priceText = price / 1000 + ",000원";

                sb.append("이용권 ").append(period).append("일 ").append(priceText).append("\n");
            }
        }

        for(Available ticket : basket){
            if(ticket instanceof PtTicket){
                int time = ((PtTicket)ticket).getTime();
                int price = ((PtTicket)ticket).getPrice();
                String priceText;

                if(price >= 1000000) {
                    priceText = (price / 1000000) + "," + (price % 1000000 / 1000) + ",000원";
                }else{
                    priceText = price / 1000 + ",000원";
                }

                sb.append("PT ").append(time).append("회 ").append(priceText).append("\n");
            }
        }

        for(Available ticket : basket){
            if(ticket instanceof Locker){
                int period = ((Locker)ticket).getPeriod();
                int number = ((Locker)ticket).getNum();
                int price = ((Locker)ticket).getPrice();
                String priceText = price / 1000 + ",000원";

                sb.append("사물함 No.").append(number).append(" ").append(period).append("일 ").append(priceText).append("\n");
            }
        }

        for(Available ticket : basket){
            if(ticket instanceof Clothes){
                int period = ((Clothes)ticket).getPeriod();
                int price = ((Clothes)ticket).getPrice();
                String priceText = price / 1000 + ",000원";

                sb.append("운동복 ").append(period).append("일 ").append(priceText).append("\n");
            }
        }

        sb.append(totalPriceText);

        Optional<ButtonType> result = showDialogChoose(sb.toString());

        if(result.get() == ButtonType.OK){
            int memberNum = currentMember.getNum();

            for(Available ticket : basket){
                if(ticket instanceof PtTicket){
                    int trainerNum = currentTrainer.getNum();
                    int quantity = ((PtTicket)ticket).getTime();

                    purchaseItem(memberNum, Item.PT_TICKET, quantity);
                    changeTrainerOfMember(memberNum, trainerNum);
                }

                if(ticket instanceof GymTicket){
                    int quantity = ((GymTicket)ticket).getPeriod();

                    purchaseItem(memberNum, Item.GYM_TICKET, quantity);
                }

                if(ticket instanceof Locker){
                    int quantity = ((Locker)ticket).getPeriod();
                    int lockerNum = ((Locker)ticket).getNum();
                    int currentLockerNum = getLockerNum(memberNum);

                    if(currentLockerNum == 0){
                        purchaseItem(memberNum, Item.LOCKER, quantity);
                        setLockerNum(memberNum, lockerNum);
                    }else{
                        deleteLockerNum(memberNum);
                        purchaseItem(memberNum, Item.LOCKER, quantity);
                        setLockerNum(memberNum, lockerNum);
                    }
                }

                if(ticket instanceof Clothes){
                    int quantity = ((Clothes)ticket).getPeriod();

                    purchaseItem(memberNum, Item.CLOTHES, quantity);
                }
            }

            play("completePayment");
            showDialog("결제가 완료되었습니다!\n확인을 누르시면 메인 화면으로 이동합니다.");

            selectTrainer = false;
            currentTrainer = null;
            basket.clear();

            moveToMainPage(event);
        }
    }

    public static void removeItem(Set<Available> basket, Class<?> type) {
        basket.removeIf(ticket -> type.isInstance(ticket));
    }
}