package test;

import org.junit.jupiter.api.Test;

public class BirthValidator {

    static String birth = "001231";

    @Test
    void validateBirth() {
        String month = birth.substring(2, 4);
        String day = birth.substring(4);

        Integer monthInt = Integer.valueOf(month);
        Integer dayInt = Integer.valueOf(day);

        if (1 <= monthInt && monthInt <= 12) {
            System.out.println("ok");
        }

        if (1 <= dayInt && dayInt <= 31) {
            System.out.println("ok");
        }
    }
}
