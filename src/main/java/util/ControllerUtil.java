package util;

import domain.member.EntryLog;
import domain.member.Member;
import domain.member.UsingLocker;
import domain.trainer.Trainer;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import repository.EntryLogRepository;
import repository.MemberRepository;
import repository.PurchaseRepository;
import repository.TrainerRepository;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ControllerUtil {

    public static String getFullEmail(String emailId, String emailDomain) {
        return emailId + "@" + emailDomain;
    }

    public static String formatPhone(String phone) {
        return "010-" + phone.substring(0, 4) + "-" + phone.substring(4);
    }

    public static String getSelectedGender(RadioButton male, RadioButton female) {
        if (male.isSelected()) return "M";
        if (female.isSelected()) return "F";
        return null;
    }

    public static String getSelectedWorkingTime(RadioButton am, RadioButton pm) {
        if (am.isSelected()) return "AM";
        if (pm.isSelected()) return "PM";
        return null;
    }

    public static SimpleStringProperty sqlDateToLocalDate(Date sqlDate, DateTimeFormatter formatter) {
        LocalDate localDate = sqlDate.toLocalDate();
        String formattedDate = localDate.format(formatter);
        return new SimpleStringProperty(formattedDate);
    }

    public static void columnBindingMember(TableColumn<Member, String> numCol, TableColumn<Member, String> nameCol, TableColumn<Member, String> genderCol,
                                     TableColumn<Member, String> emailCol, TableColumn<Member, String> birthCol, TableColumn<Member, String> phoneCol,
                                     TableColumn<Member, String> enrollCol) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        numCol.setCellValueFactory(new PropertyValueFactory<>("num"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        birthCol.setCellValueFactory(cellData -> {
            Date sqlDate = cellData.getValue().getBirthDate();
            return sqlDateToLocalDate(sqlDate, formatter);
        });

        phoneCol.setCellValueFactory(cellData -> {
            String rawPhoneNumber = cellData.getValue().getPhone();
            String formattedPhoneNumber = formatPhone(rawPhoneNumber);
            return new SimpleStringProperty(formattedPhoneNumber);
        });

        enrollCol.setCellValueFactory(cellData -> {
            Date sqlDate = cellData.getValue().getEnrolDate();
            return sqlDateToLocalDate(sqlDate, formatter);
        });
    }

    public static void loadMemberData(TableView<Member> membersTable, MemberRepository memberRepository) {
        List<Member> members = memberRepository.findAllMembers();

        // 조회한 회원 정보를 TableView에 설정
        membersTable.setItems(FXCollections.observableArrayList(members));
    }

    public static void columnBindingTrainer(TableColumn<Trainer, String> numCol, TableColumn<Trainer, String> nameCol, TableColumn<Trainer, String> idCol,
                                     TableColumn<Trainer, String> genderCol, TableColumn<Trainer, String> workTimeCol, TableColumn<Trainer, String> birthCol,
                                     TableColumn<Trainer, String> phoneCol) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        numCol.setCellValueFactory(new PropertyValueFactory<>("num"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));
        workTimeCol.setCellValueFactory(new PropertyValueFactory<>("workingHour"));

        birthCol.setCellValueFactory(cellData -> {
            Date sqlDate = cellData.getValue().getBirthDate();
            return sqlDateToLocalDate(sqlDate, formatter);
        });

        phoneCol.setCellValueFactory(cellData -> {
            String rawPhoneNumber = cellData.getValue().getPhone();
            String formattedPhoneNumber = formatPhone(rawPhoneNumber);
            return new SimpleStringProperty(formattedPhoneNumber);
        });
    }

    public static void loadTrainerData(TableView<Trainer> membersTable, TrainerRepository trainerRepository) {
        List<Trainer> members = trainerRepository.findAllTrainer();
        membersTable.setItems(FXCollections.observableArrayList(members));
    }

    public static void loadEntryLog(Integer memberNum, TableView table, EntryLogRepository entryLogRepository) {
        TableColumn<EntryLog, String> entryNumColumn = new TableColumn<>("");
        entryNumColumn.setCellValueFactory(new PropertyValueFactory<>("entryNum"));

        TableColumn<EntryLog, String> entryLogColumn = new TableColumn<>("입장 일시");
        entryLogColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cellData.getValue().getEntryTime())
        ));

        table.getColumns().addAll(entryNumColumn, entryLogColumn);
        List<Timestamp> timestamps = entryLogRepository.findAllEntryLogs(memberNum);
        ObservableList<EntryLog> entryLogs = FXCollections.observableArrayList();

        int count = 1;
        for (Timestamp timestamp : timestamps) {
            EntryLog entryLog = new EntryLog();
            entryLog.setEntryTime(timestamp);
            entryLog.setEntryNum(count++);
            entryLogs.add(entryLog);
        }

        table.setItems(entryLogs);
    }

    public static void loadLockerInfo(TableView table, PurchaseRepository purchaseRepository) {

        TableColumn<UsingLocker, Number> countCol = new TableColumn<>("");
        TableColumn<UsingLocker, Number> memberNumCol = new TableColumn<>("회원 번호");
        TableColumn<UsingLocker, String> memberNameCol = new TableColumn<>("회원 이름");
        TableColumn<UsingLocker, Number> lockerNumCol = new TableColumn<>("사물함 번호");
        TableColumn<UsingLocker, Number> lockerPeriodCol = new TableColumn<>("사물함 기간");

        countCol.setCellValueFactory(new PropertyValueFactory<>("count"));
        memberNumCol.setCellValueFactory(new PropertyValueFactory<>("memberNum"));
        memberNameCol.setCellValueFactory(new PropertyValueFactory<>("memberName"));
        lockerNumCol.setCellValueFactory(new PropertyValueFactory<>("lockerNum"));
        lockerPeriodCol.setCellValueFactory(new PropertyValueFactory<>("lockerPeriod"));

        table.getColumns().addAll(countCol, memberNumCol, memberNameCol, lockerNumCol, lockerPeriodCol);

        List<UsingLocker> lockers = purchaseRepository.findAllUsingLocker();
        table.setItems(FXCollections.observableArrayList(lockers));
    }
}