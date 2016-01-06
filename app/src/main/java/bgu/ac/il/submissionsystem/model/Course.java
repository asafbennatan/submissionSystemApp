package bgu.ac.il.submissionsystem.model;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Asaf on 10/12/2015.
 */
public class Course {
    private String name;
    private int id;

    private ConcurrentHashMap<String,Assignment> assignments;

    public Course() {
        assignments= new ConcurrentHashMap<>();
    }


    public Course(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Assignment get(Object key) {
        return assignments.get(key);
    }

    public Assignment put(String key, Assignment value) {
        return assignments.put(key, value);
    }

    public void putAll(Map<? extends String, ? extends Assignment> m) {
        assignments.putAll(m);
    }

    public boolean containsKey(Object key) {
        return assignments.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return assignments.containsValue(value);
    }

    public boolean contains(Object value) {
        return assignments.contains(value);
    }
}
