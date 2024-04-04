package util;

import domain.Member;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import repository.MemberRepository;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static util.AlertUtil.showAlertLoginFail;
import static util.AlertUtil.showAlertSignUpFail;

public class ControllerUtil {

    private static final MemberRepository repository = new MemberRepository();

    public static String getFullEmail(String emailId, String emailDomain) {
        return emailId + "@" + emailDomain;
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

    public static boolean signUpValidate(String pw, String pwConfirm, String phone, String email, String birth) {
        if (!pw.equals(pwConfirm)) {
            showAlertSignUpFail("wrongPw");
            return true;
        }

        if (isDuplicatePhone(phone) && isDuplicateEmail(email)) {
            showAlertSignUpFail("duplicatePhoneAndEmail");
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

        if (isDuplicatePhone(phone)) {
            showAlertSignUpFail("duplicatePhone");
            return true;
        }
        return false;

    }

    public static boolean isDuplicatePhone(String phone) {
        Member member = repository.findByPhone(phone);
        if (member == null) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isDuplicateEmail(String email) {
        Member member = repository.findByEmail(email);
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
}