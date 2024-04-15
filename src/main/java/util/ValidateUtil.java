package util;

import domain.member.Member;
import domain.trainer.Trainer;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import repository.MemberRepository;
import repository.TrainerRepository;

import static domain.trainer.SelectedTrainer.*;
import static util.DialogUtil.*;
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

    public static boolean isEmptyAnyField(TextField name) {
        return name.getText().trim().isEmpty();
    }

    public static boolean signUpValidate(String pw, String pwConfirm, String phone, String email, String birth) {


        if (isDuplicatePhone(phone)) {
            showDialogErrorMessage("duplicatePhone");
            return true;
        }

        if (!pw.equals(pwConfirm)) {
            showDialogErrorMessage("wrongPw");
            return true;
        }

        if (isDuplicatePhone(phone) && isDuplicateEmail(email)) {
            showDialogErrorMessage("duplicatePhoneAndEmail");
            return true;
        }

        if (isWrongEmail(email)) {
            showDialogErrorMessage("wrongEmail");
            return true;
        }

        if (isDuplicateEmail(email)) {
            showDialogErrorMessage("duplicateEmail");
            return true;
        }

        if (isWrongBirth(birth)) {
            showDialogErrorMessage("wrongBirth");
            return true;
        }

        return false;
    }

    public static boolean addMemberValidate(String name, String phone, String email, String birth) {

        if (name.length() > 10) {
            showDialogErrorMessage("tooLongName");
            return true;
        }
        if (isDuplicatePhone(phone) && isDuplicateEmail(email)) {
            showDialogErrorMessage("duplicatePhoneAndEmail");
            return true;
        }

        if (isWrongBirth(birth)) {
            showDialogErrorMessage("wrongBirth");
            return true;
        }

        if (isDuplicateEmail(email)) {
            showDialogErrorMessage("duplicateEmail");
            return true;
        }

        if (isDuplicatePhone(phone)) {
            showDialogErrorMessage("duplicatePhone");
            return true;
        }

        if (isWrongEmail(email)) {
            showDialogErrorMessage("wrongEmail");
            return true;
        }
        return false;
    }

    public static boolean addTrainerValidate(String name, String phone, String id, String birth, Double height, Double weight) {

        if (name.length() > 10) {
            showDialogErrorMessage("tooLongName");
            return true;
        }

        if (isDuplicateName(name)) {
            showDialogErrorMessage("duplicateName");
            return true;
        }

        if (isDuplicatePhone(phone) && isDuplicateTrainerId(id)) {
            showDialogErrorMessage("duplicateIdAndPhone");
            return true;
        }

        if (isDuplicateTrainerId(id)) {
            showDialogErrorMessage("duplicateId");
            return true;
        }

        if (isWrongBirth(birth)) {
            showDialogErrorMessage("wrongBirth");
            return true;
        }

        if (isDuplicatePhone(phone)) {
            showDialogErrorMessage("duplicatePhone");
            return true;
        }

        if (isWrongId(id)) {
            showDialogErrorMessage("wrongID");
            return true;
        }

        if (!((100 < height && height < 220) || (30 < weight && weight < 150))) {
            showDialogErrorMessage("wrongHeightOrWeight");
            return true;
        }
        return false;
    }

    public static boolean updateTrainerValidate(String name, String phone, String id, Double height, Double weight) {

        if (name.length() > 10) {
            showDialogErrorMessage("tooLongName");
            return true;
        }

        if (isDuplicateName(name) && !currentTrainer.getName().equals(name)) {
            showDialogErrorMessage("duplicateName");
            return true;
        }

        if (isDuplicatePhone(phone) && isDuplicateTrainerId(id) && !currentTrainer.getPhone().equals(phone) && !currentTrainer.getId().equals(id)) {
            showDialogErrorMessage("duplicateIdAndPhone");
            return true;
        }

        if (isDuplicateTrainerId(id) && !currentTrainer.getId().equals(id)) {
            showDialogErrorMessage("duplicateId");
            return true;
        }

        if (isDuplicatePhone(phone) && !currentTrainer.getPhone().equals(phone)) {
            showDialogErrorMessage("duplicatePhone");
            return true;
        }

        if (isWrongId(id)) {
            showDialogErrorMessage("wrongID");
            return true;
        }

        if (!((100 < height && height < 220) || (30 < weight && weight < 150))) {
            showDialogErrorMessage("wrongHeightOrWeight");
            return true;
        }
        return false;
    }

    public static boolean isDuplicateName(String name) {
        Trainer trainer = trainerRepository.findByName(name);
        return trainer != null;
    }

    // 트레이너 아이디에 한글이 포함되면 안 된다.
    public static boolean isWrongId(String id) {
        return id.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*");
    }

    public static boolean isDuplicateTrainerId(String id) {
        Trainer trainer = trainerRepository.findById(id);
        return trainer != null;
    }

    public static boolean isWrongEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        return !email.matches(emailRegex);
    }

    public static boolean isDuplicatePhone(String phone) {
        Member member = memberRepository.findByPhone(phone);
        Trainer trainer = trainerRepository.findByPhone(phone);
        return member != null || trainer != null;
    }

    public static boolean isDuplicateEmail(String email) {
        Member member = memberRepository.findByEmail(email);
        return member != null;
    }

    public static boolean isWrongBirth(String birth) {
        int month = Integer.parseInt(birth.substring(2, 4));
        int day = Integer.parseInt(birth.substring(4));

        return (1 > month || month > 12) || (1 > day || day > 31);
    }

    public static boolean isWrongLengthPhone(String phone) {
        return !(phone.length() == 8);
    }
}
