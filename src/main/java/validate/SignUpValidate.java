package validate;

import domain.Member;
import javafx.scene.control.PasswordField;
import repository.MemberRepository;

import java.util.List;

public class SignUpValidate {

    MemberRepository repository = new MemberRepository();

    public boolean isDuplicatePhone(String phone) {
        Member member = repository.findByPhone(phone);
        if (member == null) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isDuplicateEmail(String email) {
        Member member = repository.findByEmail(email);
        if (member == null) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isWrongBirth(String birth) {

        Integer month = Integer.valueOf(birth.substring(2, 4));
        Integer day = Integer.valueOf(birth.substring(4));

        if ((1 <= month && month <= 12) && (1 <= day && day <= 31)) {
            return false;
        } else {
            return true;
        }
    }
}