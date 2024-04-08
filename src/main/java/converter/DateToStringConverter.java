package converter;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateToStringConverter {
    public static String dateToString(Date birth) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyMMdd");
        String birthDate = formatter.format(birth);

        return birthDate;
    }
}
