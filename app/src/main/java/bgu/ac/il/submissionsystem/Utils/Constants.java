package bgu.ac.il.submissionsystem.Utils;

import android.util.Log;

import org.apache.commons.io.FilenameUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    public static final String getGroupPageIntentName="bgu.ac.il.submmissionsystem.getGroupPage";
    public static final String getEditGroupPageIntentName="bgu.ac.il.submmissionsystem.getEditGroupPage";


    public static final String refreshIntentName="bgu.ac.il.submmissionsystem.refresh";
    public static final String coursesIntentName="bgu.ac.il.submmissionsystem.courses";
    public static final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    public static final SimpleDateFormat formatterWithHoursMin = new SimpleDateFormat("dd/MM/yyyy hh:mm");

    public static final String MENU_ACTION = "SHOW_MENU";
    public static final String module="Campus";
    public static final String type="STUDENT";
    public static final String refreshAction="REFRESH";
    public static final String ShowGreetings="SHOW_GREETINGS";
    public static final String chooseSon="CHOOSE_SON";
    public static final String showAssignments="SHOW_ASSIGNMENTS";
    public static final String SHOW_EDIT_SG="SHOW_EDIT_SG";
    public static final String ADD_TO_SG="ADD_TO_SG";
    public static final String SHOW_REGISTER_SG="SHOW_REGISTER_SG";
    public static final String assignmentId="assignment-id";
    public static final String submittalGroupId="submittal-group-id";
    public static final String GET_FILE="GET_FILE";
    public static final String SUBMIT_WORK="SUBMIT_WORK";


    public static Date parseDate(String d,boolean hoursAndMin){
        try{
            Date date=null;
            if(hoursAndMin){
                date=formatterWithHoursMin.parse(d);
            }
            else{
                date= formatter.parse(d);
            }
            return date;
        }
        catch(ParseException e){
            Log.e("dateParse","failed");
        }
        return null;
    }

    public static String formatDate(Date d,boolean hoursAndMin){
        if(hoursAndMin){
            return formatterWithHoursMin.format(d);
        }
            return formatter.format(d);
    }

    public static String fileExt(String name) {
        return FilenameUtils.getExtension(name);

    }

    public static String normalize(String name){
        return name.replaceAll("[?, ]", "_");
    }


}
