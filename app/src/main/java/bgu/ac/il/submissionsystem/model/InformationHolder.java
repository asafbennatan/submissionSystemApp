package bgu.ac.il.submissionsystem.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Asaf on 10/12/2015.
 */
public class InformationHolder {

    private static final String baseUrl="http://frodo.cs.bgu.ac.il/cs_service/servlet/service";
    private static String csid;
    private static String id;
    private static String username;



    private static ConcurrentHashMap<Integer,Course> loadedCourses = new ConcurrentHashMap<>();

    public static String getBaseUrl() {
        return baseUrl;
    }

    public static Course putCourse(int val,Course c) {

        return loadedCourses.put(val,c);
    }
    public static String getId() {
        return id;
    }

    public static void setId(String id) {
        InformationHolder.id = id;
    }

    public static ConcurrentHashMap<Integer, Course> getLoadedCourses() {
        return loadedCourses;
    }
    public static Collection<Course> getCourses() {
        return loadedCourses.values();
    }


    public static String getCsid() {
        return csid;
    }

    public static void setCsid(String csid) {
        InformationHolder.csid = csid;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        InformationHolder.username = username;
    }
}
