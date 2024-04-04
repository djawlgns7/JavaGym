package util;

import javafx.scene.control.PasswordField;

public class StyleUtil {

    public static void stylePassword(PasswordField password, PasswordField passwordConfirm) {
        boolean containsStyle = passwordConfirm.getStyleClass().contains("password-field-error");

        if (!password.getText().equals(passwordConfirm.getText())) {
            if (!containsStyle) {
                passwordConfirm.getStyleClass().add("password-field-error");
            }
        } else {
            passwordConfirm.getStyleClass().remove("password-field-error");
        }
    }
}