package bgu.ac.il.submissionsystem.Utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Asaf on 03/01/2016.
 */
public class Constants {

    public static final String PREFS_NAME = "bgu.ac.il.submissionsystemprefs";
    public static final String PREF_USERNAME = "username";
    public static final String PREF_PASSWORD = "password";

    public static final String loginIntentName="bgu.ac.il.submmissionsystem.login";
    public static final String frodobodyIntentName="bgu.ac.il.submmissionsystem.frodobody";
    public static final String submissionSystemStartIntentName="bgu.ac.il.submmissionsystem.submissionSystemStart";
    public static final String getAssignmentsIntentName="bgu.ac.il.submmissionsystem.getAssignments";
    public static final String assignmetsInentName="bgu.ac.il.submmissionsystem.assignments";
    public static final String refreshIntentName="bgu.ac.il.submmissionsystem.refresh";
    public static final String coursesIntentName="bgu.ac.il.submmissionsystem.courses";
    public static final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

    public static final String MENU_ACTION = "SHOW_MENU";
    public static final String module="Campus";
    public static final String type="STUDENT";
    public static final String refreshAction="REFRESH";
    public static final String ShowGreetings="SHOW_GREETINGS";
    public static final String chooseSon="CHOOSE_SON";
    public static final String showAssignments="SHOW_ASSIGNMENTS";


    public static Date parseDate(String d){
        try{
            return formatter.parse(d);
        }
        catch(ParseException e){
            Log.e("dateParse","failed");
        }
        return null;
    }
}
