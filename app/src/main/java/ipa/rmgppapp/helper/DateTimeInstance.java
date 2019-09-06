package ipa.rmgppapp.helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateTimeInstance {

    public static String getTimeStamp(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(cal.getTime());
    }
}
