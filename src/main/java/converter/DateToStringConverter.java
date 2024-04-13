package converter;

import java.util.Date;
import java.text.SimpleDateFormat;

public class DateToStringConverter {
    public static String dateToString(Date birth) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyMMdd");
        return formatter.format(birth);
    }
}