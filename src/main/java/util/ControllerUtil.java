package util;

import domain.Member;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import repository.MemberRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static util.AlertUtil.showAlertAddMemberFail;
import static util.AlertUtil.showAlertSignUpFail;

public class ControllerUtil {

    private static final MemberRepository memberRepository = new MemberRepository();

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

    public static boolean isEmptyAnyField(TextField name, TextField emailId, ComboBox<String> emailDomain,
                                          TextField birth, TextField phone, PasswordField password,
                                          PasswordField passwordConfirm,
                                          RadioButton male, RadioButton female) {

        return  name.getText().trim().isEmpty() ||
                password.getText().trim().isEmpty() ||
                passwordConfirm.getText().trim().isEmpty() ||
                getSelectedGender(male, female) == null ||
                emailId.getText().trim().isEmpty() ||
                emailDomain.getValue() == null ||
                emailDomain.getEditor().getText().trim().isEmpty() ||
                birth.getText().trim().isEmpty() ||
                phone.getText().trim().isEmpty();
    }

    public static boolean isEmptyAnyField(TextField name, TextField email,
                                          TextField birth, TextField phone,
                                          RadioButton male, RadioButton female) {

        return  name.getText().trim().isEmpty() ||
                getSelectedGender(male, female) == null ||
                email.getText().trim().isEmpty() ||
                birth.getText().trim().isEmpty() ||
                phone.getText().trim().isEmpty();
    }

    public static boolean signUpValidate(String pw, String pwConfirm, String phone, String email, String birth) {

        if (isDuplicatePhone(phone)) {
            showAlertSignUpFail("duplicatePhone");
            return true;
        }

        if (!pw.equals(pwConfirm)) {
            showAlertSignUpFail("wrongPw");
            return true;
        }

        if (isDuplicatePhone(phone) && isDuplicateEmail(email)) {
            showAlertSignUpFail("duplicatePhoneAndEmail");
            return true;
        }

        if (isWrongEmail(email)) {
            showAlertSignUpFail("wrongEmail");
            return true;
        }

        if (isDuplicateEmail(email)) {
            showAlertSignUpFail("duplicateEmail");
            return true;
        }

        if (isWrongBirth(birth)) {
            showAlertSignUpFail("wrongBirth");
            return true;
        }

        return false;
    }

    public static boolean addMemberValidate(String phone, String email, String birth) {
        if (isDuplicatePhone(phone) && isDuplicateEmail(email)) {
            showAlertAddMemberFail("duplicatePhoneAndEmail");
            return true;
        }

        if (isDuplicateEmail(email)) {
            showAlertAddMemberFail("duplicateEmail");
            return true;
        }

        if (isWrongBirth(birth)) {
            showAlertAddMemberFail("wrongBirth");
            return true;
        }

        if (isDuplicatePhone(phone)) {
            showAlertAddMemberFail("duplicatePhone");
            return true;
        }

        if (isWrongEmail(email)) {
            showAlertAddMemberFail("wrongEmail");
            return true;
        }
        return false;
    }

    public static boolean isWrongEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        return !email.matches(emailRegex);
    }

    public static boolean isDuplicatePhone(String phone) {
        Member member = memberRepository.findByPhone(phone);
        if (member == null) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isDuplicateEmail(String email) {
        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isWrongBirth(String birth) {
        Integer month = Integer.valueOf(birth.substring(2, 4));
        Integer day = Integer.valueOf(birth.substring(4));

        if ((1 <= month && month <= 12) && (1 <= day && day <= 31)) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isWrongLengthPhone(String phone) {
        return !(phone.length() == 8);
    }

    public static SimpleStringProperty sqlDateToLocalDate(Date sqlDate, DateTimeFormatter formatter) {
        LocalDate localDate = sqlDate.toLocalDate();
        String formattedDate = localDate.format(formatter);
        return new SimpleStringProperty(formattedDate);
    }

    public static void columnBinding(TableColumn<Member, String> numCol, TableColumn<Member, String> nameCol, TableColumn<Member, String> genderCol,
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

    public static void loadMemberData(TableView<Member> membersTable) {
        List<Member> members = memberRepository.findAllMembers();

        // 조회한 회원 정보를 TableView에 설정
        membersTable.setItems(FXCollections.observableArrayList(members));
    }
}