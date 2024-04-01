package converter;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class StringToDateConverter {

    public static Date stringToDate(String birth) throws ParseException {
        // java.util.Date 반환
        SimpleDateFormat formatter = new SimpleDateFormat("yyMMdd");
        java.util.Date utilDate = formatter.parse(birth);

        // java.util.Date를 java.sql.Date로 변환
        return new Date(utilDate.getTime());
    }
}