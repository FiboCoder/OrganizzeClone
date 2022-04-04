package Helper;

import java.text.SimpleDateFormat;

public class DateUtil{

    public static String currentDate(){

        long date = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy");
        String dataString = simpleDateFormat.format(date);

        return dataString;
    }

    public static String myDate(String date){

        String returnDate[] = date.split("/");
        String day = returnDate[0];
        String month = returnDate[1];
        String year = returnDate[2];

        String monthYear = month + year;

        return monthYear;
    }
}
