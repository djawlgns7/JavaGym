package converter;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class StringToDateConverter {

    /**
     * java.util.Date -> java.sql.Date
     */
    public static Date stringToDate(String birth) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyMMdd");
        java.util.Date utilDate = formatter.parse(birth);

        return new Date(utilDate.getTime());
    }
}