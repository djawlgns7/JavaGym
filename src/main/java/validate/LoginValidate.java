package validate;

public class LoginValidate {

    public boolean isWrongLengthPhone(String phone) {
        return !(phone.length() == 8);
    }

    public boolean isWrongLengthPassword(String password) {
        return !(password.length() == 4);
    }
}