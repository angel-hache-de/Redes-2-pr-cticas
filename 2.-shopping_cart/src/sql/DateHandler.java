package sql;

import java.sql.Date;
import java.time.LocalDate;

public class DateHandler {
//    private DateHandler instance = null;
//
//    private DateHandler() {}
//
//    public DateHandler getInstance() {
//        if(instance != null)
//            return instance;
//
//        instance = new DateHandler();
//        return instance;
//    }

    public static Date getSQLDate(LocalDate date) {
        return java.sql.Date.valueOf(date);
    }

    public static Date getSQLDate(String date) {
        return java.sql.Date.valueOf(date);
    }

    public static String getCurrentDate() {
        return LocalDate.now().toString();
    }

    public static String getUtilDate(Date sqlDate) {
        return sqlDate.toLocalDate().toString();
    }
}
