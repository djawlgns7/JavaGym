package controller.member;

import domain.member.Member;
import domain.trainer.Trainer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import repository.MemberRepository;
import repository.ReservationRepository;
import repository.TrainerRepository;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static domain.member.SelectedMember.currentMember;
import static util.AlertUtil.showAlertAndMove;
import static util.AlertUtil.showAlertChoose;
import static util.MemberUtil.getRemainAll;
import static util.MemberUtil.getTrainerNumForMember;
import static util.PageUtil.movePage;
import static util.TrainerUtil.getTrainerSchedule;

public class ReservationController implements Initializable {

    private final TrainerRepository trainerRepository = new TrainerRepository();
    private final ReservationRepository reservationRepository = new ReservationRepository();
    private final MemberRepository memberRepository = new MemberRepository();

    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField memberNo, trainerNo;
    @FXML
    private ComboBox<Integer> timeComboBox;
    @FXML
    private HBox week1, week2, week3, week4, week5, timeArea;
    @FXML
    private Label[] days = new Label[71], timeButtons = new Label[6];
    @FXML
    private Label calendarHead, trainerName, trainerInfo, PTTicketRemain, prevPage, nextPage, ticketSelection;
    @FXML
    private ImageView imageView;

    List<Boolean>[] reservations;
    HBox[] weeks;
    Member member;
    Trainer trainer;
    int timeSelectedIndex = 1;
    int daySelectedIndex = 1;
    LocalDate selectedDate;
    int selectedTime;
    int adder;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (currentMember != null) {
            member = currentMember;
            trainer = trainerRepository.findByNum(getTrainerNumForMember(member.getNum()));
            adder = trainerRepository.getWorkingHourAdder(trainer);
            reservations = getTrainerSchedule(trainer, 60);

//            try {
//                setMyInfo();
//            } catch (ParseException e) {
//                throw new RuntimeException(e);
//            }
            makeCalendar();
        }
    }

    @FXML
    public void makeCalendar(){
        weeks = new HBox[] {week1, week2, week3, week4, week5};
        LocalDate today = LocalDate.now();
        LocalDate calenderDay = LocalDate.now();
        int todayOfWeek = today.getDayOfWeek().getValue();
        int dayIndex = 1;
        if(todayOfWeek == 7){todayOfWeek = 1;}
        else{todayOfWeek++;}
        calenderDay = calenderDay.minusDays(todayOfWeek - 1);

        for(int i = 0; i < 10; i++) {
            for(int j = 1; j <= 7; j++) {
                int index = 7*i + j;
                days[index] = new Label(String.valueOf(index));

                if(j == 1){
                    days[index].getStyleClass().add("sundayLabel");
                }else if(j == 7){
                    days[index].getStyleClass().add("saturdayLabel");
                }

                if(i < 5) {
                    weeks[i].getChildren().add(days[index]);
                }
            }
        }

        for(int i = 0; i < 6; i++) {
            if(adder + i < 10) {
                timeButtons[i] = new Label("0" + (adder + i) + ":00");
            }else{
                timeButtons[i] = new Label((adder + i) + ":00");
            }

            timeButtons[i].setId(i + adder + "");
            timeButtons[i].getStyleClass().add("timeButton");

            timeArea.getChildren().add(timeButtons[i]);
        }

        calendarHead.setText(today.getMonth().getValue() + "월");

        for(int i = 1; i <= todayOfWeek; i++){
            days[dayIndex].getStyleClass().add("disabled");
            days[dayIndex].setText(calenderDay.getDayOfMonth() + "");
            calenderDay = calenderDay.plusDays(1);
            dayIndex++;
        }

        for(int i = 1; i <= 60; i++){
            boolean isExist = true;
            days[dayIndex].setText(calenderDay.getDayOfMonth() + "");

            for(int j = 0; j < reservations[i].size(); j++){
                if(reservations[i].get(j)){isExist = false;}
            }

            if(isExist){
                days[dayIndex].getStyleClass().add("disabled");
            }else{
                addDateEvent(i, dayIndex);
            }
            dayIndex++;
            calenderDay = calenderDay.plusDays(1);
        }

        for(int i = 0; i < 10 - todayOfWeek; i++){
            days[dayIndex].getStyleClass().add("disabled");
            days[dayIndex].setText(calenderDay.getDayOfMonth() + "");
            calenderDay = calenderDay.plusDays(1);
            dayIndex++;
        }
    }

    @FXML
    public void setMyInfo() throws ParseException {
        int memberNum = member.getNum();
        List<Integer> remain = getRemainAll(memberNum);
        int PTTicket = remain.get(1);

        int trainerAge = trainerRepository.getAge(trainer);
        Image trainerImage = trainerRepository.getImage(trainer.getNum());

        imageView.setImage(trainerImage);
        trainerName.setText(trainer.getName() + "트레이너");
        trainerInfo.setText(trainer.getHeight() + "cm|" + trainer.getWeight() + "kg|" + trainerAge + "살");
        PTTicketRemain.setText("잔여 PT 이용권 개수 " + PTTicket + "개");

    }

    // 시간 버튼을 눌렀을 때 이벤트 추가
    @FXML
    public void addButtonEvent(List<Boolean>[] reservations, int day){
        for(int i = 0; i < reservations[day].size(); i++){
            timeButtons[i].getStyleClass().remove("disabledTimeButton");
            timeButtons[i].getStyleClass().remove("selectedTimeButton");
            if(reservations[day].get(i)){
                int finalI = i;
                timeButtons[i].setOnMouseClicked(Event -> {
                    timeButtons[timeSelectedIndex].getStyleClass().remove("selectedTimeButton");
                    timeButtons[finalI].getStyleClass().add("selectedTimeButton");
                    timeSelectedIndex = finalI;
                    selectedTime = finalI + adder;
                });
            }else{
                timeButtons[i].getStyleClass().add("disabledTimeButton");
            }
        }
    }

    // 날짜를 선택했을 때 이벤트 추가
    @FXML
    public void addDateEvent(int index, int dayIndex){
        int finalI = index;
        int finalDayIndex = dayIndex;
        days[dayIndex].setOnMouseClicked(Event ->{
            days[daySelectedIndex].getStyleClass().remove("selected");
            addButtonEvent(reservations, finalI);
            days[finalDayIndex].getStyleClass().add("selected");
            daySelectedIndex = finalDayIndex;
            selectedDate = LocalDate.now().plusDays(finalI);;
        });
    }

    // 선택한 날짜와 시간으로 예약하는 메소드
    @FXML
    public void saveReservation(ActionEvent event) throws IOException {
        Optional<ButtonType> result = showAlertChoose("예약을 확정하시겠습니까?");

        if (result.get() == ButtonType.OK){
            reservationRepository.saveReservation(member.getNum(), trainer.getNum(), selectedDate, selectedTime);
            showAlertAndMove("알림", "예약이 확정되었습니다.", Alert.AlertType.INFORMATION, "/view/member/memberLogin", event);
        }
    }

    @FXML
    public void goBack(ActionEvent event) throws IOException {
        movePage(event, "/view/member/helloMember");
    }

    @FXML
    public void ticketPlus(){
        String currentTicketNum = ticketSelection.getText();
        currentTicketNum = currentTicketNum.substring(0, currentTicketNum.length() - 1);
        int PTRemain = Integer.parseInt(currentTicketNum);
        int memberNum = member.getNum();
        List<Integer> remain = getRemainAll(memberNum);
        int PTTicket = remain.get(1);

        PTRemain++;
        if(0 <= PTRemain && PTRemain <= PTTicket) {
            ticketSelection.setText(PTRemain + "개");
        }
    }

    @FXML
    public void ticketMinus(){
        String currentTicketNum = ticketSelection.getText();
        currentTicketNum = currentTicketNum.substring(0, currentTicketNum.length() - 1);
        int PTRemain = Integer.parseInt(currentTicketNum);
        int memberNum = member.getNum();
        List<Integer> remain = getRemainAll(memberNum);
        int PTTicket = remain.get(1);

        PTRemain--;
        if(0 <= PTRemain && PTRemain <= PTTicket) {
            ticketSelection.setText(PTRemain + "개");
        }
    }

    @FXML
    public void nextPage(){
        int index = 36;
        for(int i = 0; i < 5; i++){
            weeks[i].getChildren().remove(0, 7);
            for(int j = 0; j < 7; j++) {
                weeks[i].getChildren().add(days[index++]);
            }
        }

        LocalDate today = LocalDate.now();
        int todayOfWeek = today.getDayOfWeek().getValue();
        if(todayOfWeek == 7){todayOfWeek = 1;}
        LocalDate nextPageDay = today.minusDays(todayOfWeek - 1);
        nextPageDay = nextPageDay.plusDays(35);
        int nextPageMonth = nextPageDay.getMonth().getValue();

        calendarHead.setText(nextPageMonth + "월");

        nextPage.setOnMouseClicked(event->{});
        nextPage.getStyleClass().remove("clickable");
        nextPage.getStyleClass().add("unclickable");

        prevPage.setOnMouseClicked(event->{
            prevPage();
        });
        prevPage.getStyleClass().remove("unclickable");
        prevPage.getStyleClass().add("clickable");
    }

    @FXML
    public void prevPage(){
        int index = 1;
        for(int i = 0; i < 5; i++){
            weeks[i].getChildren().remove(0, 7);
            for(int j = 0; j < 7; j++) {
                weeks[i].getChildren().add(days[index++]);
            }
        }

        calendarHead.setText(LocalDate.now().getMonth().getValue() + "월");

        nextPage.setOnMouseClicked(event->{
            nextPage();
        });
        nextPage.getStyleClass().remove("unclickable");
        nextPage.getStyleClass().add("clickable");

        prevPage.setOnMouseClicked(event->{});
        prevPage.getStyleClass().remove("clickable");
        prevPage.getStyleClass().add("unclickable");
    }
}
