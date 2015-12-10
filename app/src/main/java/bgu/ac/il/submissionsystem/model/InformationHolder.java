package bgu.ac.il.submissionsystem.model;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Asaf on 10/12/2015.
 */
public class InformationHolder {

    private static final String baseUrl="http://frodo.cs.bgu.ac.il/cs_service/servlet/service";
    private static String username;
    private static String password;
    private static String id;

    private static ConcurrentHashMap<Integer,Course> loadedCourses = new ConcurrentHashMap<>();

    public static String getBaseUrl() {
        return baseUrl;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        InformationHolder.username = username;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        InformationHolder.password = password;
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




}
