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
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import repository.ReservationRepository;
import repository.TrainerRepository;
import service.SmsService;
import util.DialogUtil;
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
    private Label[] days = new Label[71], timeButtons = new Label[6];
    @FXML
    private Label calendarHead, trainerName, trainerInfo, PTTicketRemain, prevPage, nextPage, ticketSelection, selectedReaservationNum, minusBtn, plusBtn;
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

        minusBtn.getStyleClass().add("disabled");

        for(int i = 0; i < 10; i++) {
            for(int j = 1; j <= 7; j++) {
                int index = 7*i + j;
                days[index] = new Label(String.valueOf(index));

                if(j == 1){
                    days[index].getStyleClass().add("sundayLabel");
                }else if(j == 7){
                    days[index].getStyleClass().add("saturdayLabel");
                }else{
                    days[index].getStyleClass().add("dayLabel");
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
            timeButtons[i].getStyleClass().add("disabledTimeButton");

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
    public void addButtonEvent(List<Boolean>[] reservations, int day, int dayindex){
        for(int i = 0; i < reservations[day].size(); i++){
            int rTime = adder + i;
            timeButtons[i].getStyleClass().remove("disabledTimeButton");
            timeButtons[i].getStyleClass().remove("selectedTimeButton");

            for(int j = 0; j < selectedReservations.size(); j++){
                if(selectedReservations.get(j).isExist(day, rTime)){
                    timeButtons[i].getStyleClass().add("selectedTimeButton");
                }
            }

            if(reservations[day].get(i)){
                int finalI = i;

                //예약이 가능한 버튼일 경우
                timeButtons[i].setOnMouseClicked(Event -> {
                    //이미 예약 목록에 있던 버튼인 경우
                    if(reservationRepository.isReservationExist(selectedReservations, day, rTime)) {
                        timeButtons[finalI].getStyleClass().remove("selectedTimeButton");
                        reservationRepository.removeReservation(selectedReservations, day, rTime);
                    //예약 목록에 없던 버튼인 경우
                    }else {
                        //예약을 추가 가능한 횟수가 없을 경우
                        if(getSelectedPTTicket() <= selectedReservations.size()){
                            DialogUtil.showDialog("더 이상 선택할 수 없습니다");
                        //추가 가능한 횟수가 있을 경우
                        }else {

                            if (!isFirstSelectReservation) {
                                SoundUtil.play("cancelReservation");
                                isFirstSelectReservation = true;
                            }

                            timeButtons[finalI].getStyleClass().add("selectedTimeButton");
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
                        if(!days[dayindex].getStyleClass().contains("reservedDay")) {
                            days[dayindex].getStyleClass().add("reservedDay");
                        }
                    }else{
                        days[dayindex].getStyleClass().remove("reservedDay");
                    }

                    showSelectedReservations();
                    setSelectedReservationNum();
                });
            //예약이 불가능한 버튼인 경우
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
            addButtonEvent(reservations, finalI, finalDayIndex);
            days[finalDayIndex].getStyleClass().add("selected");
            daySelectedIndex = finalDayIndex;
            selectedDate = LocalDate.now().plusDays(finalI);;
        });
    }

    // 선택한 날짜와 시간으로 예약하는 메소드
    @FXML
    public void saveReservation(ActionEvent event) throws IOException {
        if(getSelectedPTTicket() == 0){
            DialogUtil.showDialog("한 개 이상의 시간대를 선택해 주세요");
            return;
        }

        // 시간을 전부 선택하지 않았을 경우
        if(getSelectedPTTicket() != selectedReservations.size()){
            DialogUtil.showDialog("선택한 예약권의 수 만큼 예약을 해주세요");
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
            minusBtn.getStyleClass().remove("disabled");
            minusBtn.setOnMouseClicked(Event -> ticketMinus());
        }

        int newPTTicket = getSelectedPTTicket() + 1;
        ticketSelection.setText(newPTTicket + "개");

        if(newPTTicket == availableReservationNum){
            plusBtn.getStyleClass().add("disabled");
            plusBtn.setOnMouseClicked(event -> {});
        }
    }

    @FXML
    public void ticketMinus(){
        if(getSelectedPTTicket() == availableReservationNum){
            plusBtn.getStyleClass().remove("disabled");
            plusBtn.setOnMouseClicked(Event -> ticketPlus());
        }

        int newPTTicket = getSelectedPTTicket() - 1;
        ticketSelection.setText(newPTTicket + "개");

        if(newPTTicket == 0){
            minusBtn.getStyleClass().add("disabled");
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
        removeselectedReservation(index);

    }

    //입력한 인덱스에 해당하는 예약을 삭제 후 버튼들 색 갱신
    public void removeselectedReservation(int index){
        ReservationInformation removedReservation;
        removedReservation =  selectedReservations.remove(index);

        int rDDay = removedReservation.getDDay();
        int rRTime = removedReservation.getRTime() - adder;

        int todayOfWeek = LocalDate.now().getDayOfWeek().getValue();
        if(todayOfWeek == 7){todayOfWeek = 1;}
        else{todayOfWeek++;}
        int dateIndex = todayOfWeek + rDDay;

        if(daySelectedIndex == dateIndex){
            timeButtons[rRTime].getStyleClass().remove("selectedTimeButton");
        }

        int remainReservationNum = 0;

        for(int i = 0; i < selectedReservations.size(); i++){
            if(rDDay == selectedReservations.get(i).getDDay()){
                remainReservationNum++;
            }
        }

        if(remainReservationNum == 0){
            days[dateIndex].getStyleClass().remove("reservedDay");
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
            newDateTime.getStyleClass().add("selectedResrevationList");

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
                            removeselectedReservation(j);
                        }
                    }
                }
            });

            selectedReservationList.getChildren().add(newDateTime);
        }
    }

    //리셋 버튼을 눌렀을 때 선택한 예약들 초기화
    public void reset(){
        selectedReservations.clear();
        selectedReservationList.getChildren().clear();
        firstHBoxInScroll = null;
        ticketSelection.setText(0 + "개");

        for(int i = 1; i <= 70; i++){
            if(days[i].getStyleClass().contains("selected")) {
                days[i].getStyleClass().remove("selected");
            }

            if(days[i].getStyleClass().contains("reservedDay")) {
                days[i].getStyleClass().remove("reservedDay");
            }
        }

        for(int i = 0; i < timeButtons.length; i++){
            timeButtons[i].getStyleClass().clear();
            timeButtons[i].getStyleClass().add("timeButton");
            timeButtons[i].getStyleClass().add("disabledTimeButton");
        }

        setSelectedReservationNum();
    }

    @FXML
    public void setSelectedReservationNum(){
        int reservationNum = selectedReservations.size();
        selectedReaservationNum.setText("총" + reservationNum + "회");
    }

    @FXML
    public void callAdmin(){
        smsService.callAdmin();
    }
}
