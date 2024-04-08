package util;

import domain.member.Member;
import domain.trainer.Trainer;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import repository.MemberRepository;
import repository.TrainerRepository;

import static util.AlertUtil.*;
import static util.ControllerUtil.getSelectedGender;
import static util.ControllerUtil.getSelectedWorkingTime;

/**
 * 검증 시 사용하는 메서드 분리 (성진)
 */
public class ValidateUtil {

    private static final MemberRepository memberRepository = new MemberRepository();
    private static final TrainerRepository trainerRepository = new TrainerRepository();

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

    public static boolean isEmptyAnyField(TextField name, TextField id,
                                          TextField birth, TextField phone,
                                          RadioButton male, RadioButton female,
                                          RadioButton am, RadioButton pm,
                                          TextField heightField, TextField weightField) {

        return  name.getText().trim().isEmpty() ||
                id.getText().trim().isEmpty() ||
                getSelectedGender(male, female) == null ||
                birth.getText().trim().isEmpty() ||
                phone.getText().trim().isEmpty() ||
                getSelectedWorkingTime(am, pm) == null ||
                heightField.getText().trim().isEmpty() ||
                weightField.getText().trim().isEmpty();
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

    public static boolean addTrainerValidate(String phone, String id, String birth) {
        if (isDuplicatePhone(phone) && isDuplicateTrainerId(id)) {
            showAlertAddTrainerFail("duplicateIdAndPhone");
            return true;
        }

        if (isDuplicateTrainerId(id)) {
            showAlertAddTrainerFail("duplicateId");
            return true;
        }

        if (isWrongBirth(birth)) {
            showAlertAddTrainerFail("wrongBirth");
            return true;
        }

        if (isDuplicatePhone(phone)) {
            showAlertAddTrainerFail("duplicatePhone");
            return true;
        }

        if (isWrongId(id)) {
            showAlertAddTrainerFail("wrongID");
            return true;
        }
        return false;
    }

    // 트레이너 아이디에 한글이 포함되면 안 된다.
    public static boolean isWrongId(String id) {
        return id.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*");
    }

    public static boolean isDuplicateTrainerId(String id) {
        Trainer trainer = trainerRepository.findById(id);
        if (trainer == null) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isWrongEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        return !email.matches(emailRegex);
    }

    public static boolean isDuplicatePhone(String phone) {
        Member member = memberRepository.findByPhone(phone);
        Trainer trainer = trainerRepository.findByPhone(phone);
        if (member == null && trainer == null) {
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
}
