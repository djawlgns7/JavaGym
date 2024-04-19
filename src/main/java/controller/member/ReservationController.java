package controller.member;

import domain.member.Member;
import domain.member.MemberSchedule;
import domain.reservation.ReservationInformation;
import domain.trainer.Trainer;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import repository.ReservationRepository;
import repository.TrainerRepository;
import service.SmsService;
import util.SoundUtil;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static domain.member.SelectedMember.currentMember;
import static util.DialogUtil.*;
import static util.DialogUtil.showDialogAndMoveMainPage;
import static util.DialogUtil.showDialogChoose;
import static util.MemberUtil.getRemainAll;
import static util.MemberUtil.getTrainerNumForMember;
import static util.PageUtil.movePage;
import static util.TrainerUtil.getTrainerSchedule;

public class ReservationController implements Initializable {

    private final TrainerRepository trainerRepository = new TrainerRepository();
    private final ReservationRepository reservationRepository = new ReservationRepository();
    private final SmsService smsService = new SmsService();

    private boolean isFirstSelectPtTicket = false;
    private boolean isFirstSelectReservation = false;

    @FXML
    private HBox week1, week2, week3, week4, week5, timeArea;
    @FXML
    private HBox selectedReservationList;
    @FXML
    private Button[] days = new Button[71], timeButtons = new Button[6];
    @FXML
    private Label calendarHead, trainerName, trainerInfo, PTTicketRemain, ticketSelection, selectedReservationNum;
    @FXML
    Button prevPage, nextPage, minusBtn, plusBtn;
    @FXML
    private ImageView imageView;

    List<Boolean>[] reservations;
    List<ReservationInformation> selectedReservations;
    HBox[] weeks;
    HBox firstHBoxInScroll;
    Member member;
    Trainer trainer;
    int daySelectedIndex = 1;
    LocalDate selectedDate, startDay;
    int adder, availableReservationNum;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (currentMember != null) {

            SoundUtil.play("selectPt");

            member = currentMember;
            trainer = trainerRepository.findByNum(getTrainerNumForMember(member.getNum()));
            adder = trainerRepository.getWorkingHourAdder(trainer);
            reservations = getTrainerSchedule(trainer, 60);
            List<Integer> remain = getRemainAll(member.getNum());

            selectedReservations = new ArrayList<>();
            List<MemberSchedule> memberSchedule;
            memberSchedule = reservationRepository.findMemberSchedule(member.getNum());
            int memberReservationNum = memberSchedule.size();
            int PTTicket = remain.get(1);

            availableReservationNum = 4 - memberReservationNum;
            if(PTTicket < availableReservationNum) {
                availableReservationNum = PTTicket;
            }

            try {
                setMyInfo();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
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
        startDay = calenderDay.minusDays(todayOfWeek - 1);

        minusBtn.getStyleClass().add("reservation_disabledMinusBtn");

        for(int i = 0; i < 10; i++) {
            for(int j = 1; j <= 7; j++) {
                int index = 7*i + j;
                days[index] = new Button(String.valueOf(index));

                if(j == 1){
                    days[index].getStyleClass().add("reservation_Calendar_SundayLabel");
                }else if(j == 7){
                    days[index].getStyleClass().add("reservation_Calendar_SaturdayLabel");
                }else{
                    days[index].getStyleClass().add("reservation_Calendar_DayLabel");
                }

                if(i < 5) {
                    weeks[i].getChildren().add(days[index]);
                }
            }
        }

        for(int i = 0; i < 6; i++) {
            if(adder + i < 10) {
                timeButtons[i] = new Button("0" + (adder + i) + ":00");
            }else{
                timeButtons[i] = new Button((adder + i) + ":00");
            }

            timeButtons[i].setId(i + adder + "");
            timeButtons[i].getStyleClass().add("reservation_TimeButton");
            timeButtons[i].getStyleClass().add("reservation_DisabledTimeButton");

            timeArea.getChildren().add(timeButtons[i]);
        }

        calendarHead.setText("~~~~~~ " + today.getMonth().getValue() + "월 ~~~~~~");

        for(int i = 1; i <= todayOfWeek; i++){
            days[dayIndex].getStyleClass().add("reservation_DisabledDay");
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
                days[dayIndex].getStyleClass().add("reservation_DisabledDay");
            }else{
                addDateEvent(i, dayIndex);
            }
            dayIndex++;
            calenderDay = calenderDay.plusDays(1);
        }

        for(int i = 0; i < 10 - todayOfWeek; i++){
            days[dayIndex].getStyleClass().add("reservation_DisabledDay");
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
        trainerName.setText(trainer.getName() + " 트레이너");
        trainerInfo.setText(trainer.getHeight() + " cm | " + trainer.getWeight() + " kg | " + trainerAge + " 세");
        PTTicketRemain.setText(PTTicket + "");
    }

    // 시간 버튼을 눌렀을 때 이벤트 추가
    @FXML
    public void addButtonEvent(List<Boolean>[] reservations, int day, int dayindex){
        for(int i = 0; i < reservations[day].size(); i++){
            int rTime = adder + i;
            timeButtons[i].getStyleClass().remove("reservation_DisabledTimeButton");
            timeButtons[i].getStyleClass().remove("reservation_SelectedTimeButton");

            for(int j = 0; j < selectedReservations.size(); j++){
                if(selectedReservations.get(j).isExist(day, rTime)){
                    timeButtons[i].getStyleClass().add("reservation_SelectedTimeButton");
                }
            }

            if(reservations[day].get(i)){
                int finalI = i;

                //예약이 가능한 버튼일 경우
                timeButtons[i].setOnMouseClicked(Event -> {
                    //이미 예약 목록에 있던 버튼인 경우
                    if(reservationRepository.isReservationExist(selectedReservations, day, rTime)) {
                        timeButtons[finalI].getStyleClass().remove("reservation_SelectedTimeButton");
                        reservationRepository.removeReservation(selectedReservations, day, rTime);
                    //예약 목록에 없던 버튼인 경우
                    }else {
                        //예약을 추가 가능한 횟수가 없을 경우
                        if(getSelectedPTTicket() <= selectedReservations.size()){
                            showDialogErrorMessage("maxSelect");
                        //추가 가능한 횟수가 있을 경우
                        }else {

                            if (!isFirstSelectReservation) {
                                SoundUtil.play("cancelReservation");
                                isFirstSelectReservation = true;
                            }

                            timeButtons[finalI].getStyleClass().add("reservation_SelectedTimeButton");
                            ReservationInformation newReservation = new ReservationInformation();
                            newReservation.setDDay(day);
                            newReservation.setRTime(rTime);
                            selectedReservations.add(newReservation);
                        }
                    }

                    boolean isreservationExist = false;

                    for(int j = 0; j < selectedReservations.size(); j++){
                        for(int k = 0; k < 6; k++) {
                            if (selectedReservations.get(j).isExist(day, adder + k)) {
                                isreservationExist = true;
                            }
                        }
                    }

                    //예약을 선택한 날짜는 색을 칠함
                    if(isreservationExist){
                        if(!days[dayindex].getStyleClass().contains("reservation_Calendar_ReservedDay")) {
                            days[dayindex].getStyleClass().add("reservation_Calendar_ReservedDay");
                        }
                    }else{
                        days[dayindex].getStyleClass().remove("reservation_Calendar_ReservedDay");
                    }

                    showSelectedReservations();
                    setSelectedReservationNum();
                });
            //예약이 불가능한 버튼인 경우
            }else{
                timeButtons[i].getStyleClass().add("reservation_DisabledTimeButton");
            }
        }
    }

    // 날짜를 선택했을 때 이벤트 추가
    @FXML
    public void addDateEvent(int index, int dayIndex){
        int finalI = index;
        int finalDayIndex = dayIndex;
        days[dayIndex].setOnMouseClicked(Event ->{
            if (getSelectedPTTicket() == 0) {
                showDialogErrorMessage("noSelectPtTicket");
                return;
            }
            days[daySelectedIndex].getStyleClass().remove("reservation_Calendar_SelectedDay");
            addButtonEvent(reservations, finalI, finalDayIndex);
            days[finalDayIndex].getStyleClass().add("reservation_Calendar_SelectedDay");
            daySelectedIndex = finalDayIndex;
            selectedDate = LocalDate.now().plusDays(finalI);;
        });
    }

    // 선택한 날짜와 시간으로 예약하는 메소드
    @FXML
    public void saveReservation(ActionEvent event) throws IOException {
        if(getSelectedPTTicket() == 0){
            showDialog("한 개 이상의 시간대를 선택해 주세요");
            return;
        }

        // 시간을 전부 선택하지 않았을 경우
        if(getSelectedPTTicket() != selectedReservations.size()){
            showDialog("선택한 예약권의 수 만큼 예약을 해주세요");
            return;
        }

        String trainerName = trainer.getName();
        int reservationNum = selectedReservations.size() - 1;
        ObservableList<Node> hBoxChildren = selectedReservationList.getChildren();
        String firstDateTime = hBoxChildren.get(0).toString();

        String[] splittedDateTime = firstDateTime.split("\\'");
        String[] dateNTime = splittedDateTime[1].split(" ");
        String date = dateNTime[0].replace("/", "-");
        date = LocalDate.now().getYear() + "-" + date;

        String fromTime = dateNTime[1];
        String toTime = fromTime.substring(0, 2);
        int hour = Integer.parseInt(toTime) + 1;

        if(hour < 10){
            toTime = "0" + hour + ":00";
        }else{
            toTime = hour + ":00";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("예약을 확정하시겠습니까?\n").append("\n담당 트레이너 - ").append(trainerName).append(" 트레이너")
                .append("\n일시 - ").append(date).append(" ").append(fromTime).append(" ~ ").append(toTime)
                .append(" 외 ").append(reservationNum).append("건");
        String reservationConfirmMsg = sb.toString();

        //예약이 가능한 경우
        Optional<ButtonType> result = showDialogChoose(reservationConfirmMsg);

        if (result.get() == ButtonType.OK){
            LocalDate today = LocalDate.now();
            for(int i = 0; i < selectedReservations.size(); i++){
                int reservationTime = selectedReservations.get(i).getRTime();
                LocalDate reservationDate = today.plusDays(selectedReservations.get(i).getDDay());
                reservationRepository.saveReservation(member.getNum(), trainer.getNum(), reservationDate, reservationTime);
            }
            showDialogAndMoveMainPage("예약이 확정되었습니다!", event);
        }
    }

    @FXML
    public void goBack(ActionEvent event) throws IOException {
        movePage(event, "/view/member/helloMember");
    }

    @FXML
    public void ticketPlus(){

        if (!isFirstSelectPtTicket) {
            SoundUtil.play("selectDateAndTime");
            isFirstSelectPtTicket = true;
        }

        if(getSelectedPTTicket() == 0){
            minusBtn.getStyleClass().remove("reservation_disabledMinusBtn");
            minusBtn.setOnMouseClicked(Event -> ticketMinus());
        }

        int newPTTicket = getSelectedPTTicket() + 1;
        ticketSelection.setText(newPTTicket + "개");

        if(newPTTicket == availableReservationNum){
            plusBtn.getStyleClass().add("reservation_disabledPlusBtn");
            plusBtn.setOnMouseClicked(event -> {});
        }
    }

    @FXML
    public void ticketMinus(){
        if(getSelectedPTTicket() == availableReservationNum){
            plusBtn.getStyleClass().remove("reservation_disabledPlusBtn");
            plusBtn.setOnMouseClicked(Event -> ticketPlus());
        }

        int newPTTicket = getSelectedPTTicket() - 1;
        ticketSelection.setText(newPTTicket + "개");

        if(newPTTicket == 0){
            minusBtn.getStyleClass().add("reservation_disabledMinusBtn");
            minusBtn.setOnMouseClicked(event -> {});
        }

        renewReservation();
    }

    //사용할 티켓의 수가 선택된 예약의 수보다 적어질 경우 예약 하나 삭제
    @FXML
    public void renewReservation(){
        if(getSelectedPTTicket() >= selectedReservations.size()){
            return;
        }

        int index = selectedReservations.size() - 1;
        removeSelectedReservation(index);

    }

    //입력한 인덱스에 해당하는 예약을 삭제 후 버튼들 색 갱신
    public void removeSelectedReservation(int index){
        ReservationInformation removedReservation;
        removedReservation =  selectedReservations.remove(index);

        int rDDay = removedReservation.getDDay();
        int rRTime = removedReservation.getRTime() - adder;

        int todayOfWeek = LocalDate.now().getDayOfWeek().getValue();
        if(todayOfWeek == 7){todayOfWeek = 1;}
        else{todayOfWeek++;}
        int dateIndex = todayOfWeek + rDDay;

        if(daySelectedIndex == dateIndex){
            timeButtons[rRTime].getStyleClass().remove("reservation_SelectedTimeButton");
        }

        int remainReservationNum = 0;

        for(int i = 0; i < selectedReservations.size(); i++){
            if(rDDay == selectedReservations.get(i).getDDay()){
                remainReservationNum++;
            }
        }

        if(remainReservationNum == 0){
            days[dateIndex].getStyleClass().remove("reservation_Calendar_ReservedDay");
        }

        showSelectedReservations();
        setSelectedReservationNum();
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
        nextPage.getStyleClass().remove("clickableBtn");
        nextPage.getStyleClass().add("unclickableBtn");

        prevPage.setOnMouseClicked(event->{
            prevPage();
        });
        prevPage.getStyleClass().remove("unclickableBtn");
        prevPage.getStyleClass().add("clickableBtn");
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
        nextPage.getStyleClass().remove("unclickableBtn");
        nextPage.getStyleClass().add("clickableBtn");

        prevPage.setOnMouseClicked(event->{});
        prevPage.getStyleClass().remove("clickableBtn");
        prevPage.getStyleClass().add("unclickableBtn");
    }

    //현재 사용 하기로 선택 한 PT티켓 수를 반환한다
    public int getSelectedPTTicket(){
        String selectedTicketNum = ticketSelection.getText();
        selectedTicketNum = selectedTicketNum.substring(0, selectedTicketNum.length() - 1);
        int PTRemain = Integer.parseInt(selectedTicketNum);

        return PTRemain;
    }

    //선택한 예약 목록을 스크롤페인에 출력
    public void showSelectedReservations(){
        List<ReservationInformation> copiedReservations = new ArrayList<>();
        List<ReservationInformation> sortedReservations = new ArrayList<>();

        selectedReservationList.getChildren().clear();
        firstHBoxInScroll = null;

        for(int i = 0; i < selectedReservations.size(); i++){
            copiedReservations.add(new ReservationInformation());
            copiedReservations.get(i).setDDay(selectedReservations.get(i).getDDay());
            copiedReservations.get(i).setRTime(selectedReservations.get(i).getRTime());
        }

        while(!copiedReservations.isEmpty()){
            sortedReservations.add(new ReservationInformation());
            int earliestIndex = 0;
            for(int i = 1; i < copiedReservations.size(); i++){
                int earliestDDay = copiedReservations.get(earliestIndex).getDDay();
                int earliestRTime = copiedReservations.get(earliestIndex).getRTime();
                int indexDDay = copiedReservations.get(i).getDDay();
                int indexRTime = copiedReservations.get(i).getRTime();

                if(indexDDay < earliestDDay){
                    earliestIndex = i;
                }else if(indexDDay == earliestDDay){
                    if(indexRTime < earliestRTime){
                        earliestIndex = i;
                    }
                }
            }
            sortedReservations.get(sortedReservations.size() - 1).setDDay(copiedReservations.get(earliestIndex).getDDay());
            sortedReservations.get(sortedReservations.size() - 1).setRTime(copiedReservations.get(earliestIndex).getRTime());
            copiedReservations.remove(earliestIndex);
        }

        for(int i = 0; i < sortedReservations.size(); i++){
            int indexDDay = sortedReservations.get(i).getDDay();
            int indexRTime = sortedReservations.get(i).getRTime();
            LocalDate indexLocalDate = LocalDate.now().plusDays(indexDDay);
            String indexDate = indexLocalDate.getMonthValue() + "/" + indexLocalDate.getDayOfMonth();
            String indexTime;
            if(indexRTime < 10){
                indexTime = "0" + indexRTime + ":00";
            }else{
                indexTime = indexRTime + ":00";
            }

            String indexDateTime = indexDate + " " + indexTime;

            Label newDateTime = new Label(indexDateTime);
            newDateTime.getStyleClass().add("selectedReservationList");

            newDateTime.setOnMouseClicked(Event -> {
                for(int j = 0; j < selectedReservations.size(); j++){
                    if(selectedReservations.get(j).isExist(indexDDay, indexRTime)){
                        StringBuilder sb = new StringBuilder();
                        sb.append(indexDateTime).append(" ~ ").append(indexRTime + 1).append(":00")
                                .append("\n\n해당 예약을 삭제하시겠습니까?");

                        String deleteConfirmMsg = sb.toString();
                        Optional<ButtonType> result = showDialogChoose(deleteConfirmMsg);

                        if (result.get() == ButtonType.OK){
                            selectedReservationList.getChildren().remove(newDateTime);
                            removeSelectedReservation(j);
                        }
                    }
                }
            });

            selectedReservationList.getChildren().add(newDateTime);
        }
    }

    //리셋 버튼을 눌렀을 때 선택한 예약들 초기화
    public void reset() {
        selectedReservations.clear();
        selectedReservationList.getChildren().clear();
        firstHBoxInScroll = null;
        ticketSelection.setText(1 + "개");
        ticketMinus();

        for(int i = 1; i <= 70; i++){
            if(days[i].getStyleClass().contains("reservation_Calendar_SelectedDay")) {
                days[i].getStyleClass().remove("reservation_Calendar_SelectedDay");
            }

            if(days[i].getStyleClass().contains("reservation_Calendar_ReservedDay")) {
                days[i].getStyleClass().remove("reservation_Calendar_ReservedDay");
            }
        }

        timeArea.getChildren().clear();

        for(int i = 0; i < 6; i++) {
            if(adder + i < 10) {
                timeButtons[i] = new Button("0" + (adder + i) + ":00");
            }else{
                timeButtons[i] = new Button((adder + i) + ":00");
            }

            timeButtons[i].setId(i + adder + "");
            timeButtons[i].getStyleClass().add("reservation_TimeButton");
            timeButtons[i].getStyleClass().add("reservation_DisabledTimeButton");

            timeArea.getChildren().add(timeButtons[i]);
        }

        setSelectedReservationNum();
    }

    @FXML
    public void setSelectedReservationNum(){
        int reservationNum = selectedReservations.size();
        selectedReservationNum.setText(reservationNum + "");
    }

    @FXML
    public void callAdmin(){
        smsService.callAdmin();
    }
}
