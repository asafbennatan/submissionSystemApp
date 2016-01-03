package bgu.ac.il.submissionsystem.model;

import java.util.HashMap;

/**
 * Created by Asaf on 03/01/2016.
 */
public class SubmissionSystemResponse {
    private HashMap<String,String> recivedData;

    public SubmissionSystemResponse() {
        recivedData= new HashMap<>();
    }

    public String put(String key, String value) {
        return recivedData.put(key, value);
    }

    public String get(Object key) {
        return recivedData.get(key);
    }

    public boolean containsKey(Object key) {
        return recivedData.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return recivedData.containsValue(value);
    }
}
