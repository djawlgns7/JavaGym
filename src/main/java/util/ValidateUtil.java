package util;

import domain.Item;
import domain.member.Member;
import domain.trainer.Trainer;
import domain.trainer.WorkingHour;
import javafx.scene.control.*;
import repository.MemberRepository;
import repository.ReservationRepository;
import repository.TrainerRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.*;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;

import static domain.trainer.SelectedTrainer.*;
import static util.DialogUtil.*;
import static util.ControllerUtil.getSelectedGender;
import static util.ControllerUtil.getSelectedWorkingTime;
import static util.MemberUtil.*;

/**
 * 검증 시 사용하는 메서드 분리 (성진)
 */
public class ValidateUtil {

    private static final MemberRepository memberRepository = new MemberRepository();
    private static final TrainerRepository trainerRepository = new TrainerRepository();

    private static final ReservationRepository reservationRepository = new ReservationRepository();

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

        if (isDuplicateName(name) && !loginTrainer.getName().equals(name)) {
            showDialogErrorMessage("duplicateName");
            return true;
        }

        if (isDuplicatePhone(phone) && isDuplicateTrainerId(id) && !loginTrainer.getPhone().equals(phone) && !loginTrainer.getId().equals(id)) {
            showDialogErrorMessage("duplicateIdAndPhone");
            return true;
        }

        if (isDuplicateTrainerId(id) && !loginTrainer.getId().equals(id)) {
            showDialogErrorMessage("duplicateId");
            return true;
        }

        if (isDuplicatePhone(phone) && !loginTrainer.getPhone().equals(phone)) {
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

    public static boolean addReservationValidate(String name) {
        if (name.length() > 10) {
            showDialogErrorMessage("tooLongName");
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
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendValueReduced(ChronoField.YEAR_OF_ERA, 2, 2, 1900)
                .appendPattern("MMdd")
                .toFormatter()
                .withResolverStyle(ResolverStyle.STRICT);

        try {
            TemporalAccessor parsedDate = formatter.parse(birth);
            if (!parsedDate.isSupported(ChronoField.YEAR_OF_ERA) ||
                    !parsedDate.isSupported(ChronoField.MONTH_OF_YEAR) ||
                    !parsedDate.isSupported(ChronoField.DAY_OF_MONTH)) {
                return true;
            }

            LocalDate birthDate = LocalDate.of(
                    parsedDate.get(ChronoField.YEAR_OF_ERA),
                    parsedDate.get(ChronoField.MONTH_OF_YEAR),
                    parsedDate.get(ChronoField.DAY_OF_MONTH));

            if (birthDate.isAfter(LocalDate.now())) {
                return true;
            }

            return false;
        } catch (Exception e) {
            return true;
        }
    }

    public static boolean isWrongLengthPhone(String phone) {
        return !(phone.length() == 8);
    }

    public static boolean isEmptyAnyField(TextField num, TextField name, DatePicker date, ComboBox time) {
        return  num.getText().trim().isEmpty() ||
                name.getText().trim().isEmpty() ||
                date.getValue() == null ||
                time.getValue() == null;
    }

    //트레이너 근무 시간 검증
    public static boolean isValidTimeForTrainer(Trainer trainer, int hour) {
        if (trainer.getWorkingHour() == WorkingHour.AM) {
            return hour >= 8 && hour < 14;
        } else if (trainer.getWorkingHour() == WorkingHour.PM) {
            return hour >= 14 && hour < 20;
        }
        return false;
    }

    // 두달 제한 검증, 과거 시간 검증
    public static boolean isDateAndTimeValid(LocalDate reservationDate, int reservationTime) {
        LocalDate now = LocalDate.now();
        int currentHour = LocalTime.now().getHour();
        long daysBeetween = ChronoUnit.DAYS.between(now, reservationDate);

        //두 달 제한 검증
        boolean twoMonths = daysBeetween >= 0 && daysBeetween <= 60;

        //과거 시간 검증
        boolean timeValid = reservationDate.isAfter(now) || (reservationDate.isEqual(now) && reservationTime > currentHour);

        return twoMonths && timeValid;
    }

    //중복 예약 검증
    public static boolean isReservationExist (int trainerNum, LocalDate date, int time) {
        return reservationRepository.checkReservation(trainerNum, date, time);

    }

    //PT 이용권 검증
    public static boolean isValidPtTicket(int memberNum) {
        int remainPT = getRemain(memberNum, Item.PT_TICKET);
        return remainPT != 0;
    }

    public static boolean isNotYourMember(int memberNum) {
        int trainerNum = getTrainerNumForMember(memberNum);
        return trainerNum != loginTrainer.getNum();
    }
}
