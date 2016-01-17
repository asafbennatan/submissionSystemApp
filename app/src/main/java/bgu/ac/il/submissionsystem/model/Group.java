package bgu.ac.il.submissionsystem.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Asaf on 10/12/2015.
 */
public class Group {

    private List<User> users;
    private int id;
    private Map<Integer,Submission> submissions;


    public Group() {
        submissions= new HashMap<>();
    }

    public Group(int id) {
        this();
        this.id = id;
    }



    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Map<Integer, Submission> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(Map<Integer, Submission> submissions) {
        this.submissions = submissions;
    }

    public void clear() {
        submissions.clear();
    }

    public boolean containsKey(Object key) {
        return submissions.containsKey(key);
    }

    public Submission put(Integer key, Submission value) {
        return submissions.put(key, value);
    }

    public Submission get(Object key) {
        return submissions.get(key);
    }

    public boolean containsValue(Object value) {
        return submissions.containsValue(value);
    }

    public boolean isEmpty() {
        return submissions.isEmpty();
    }

    public void putAll(Map<? extends Integer, ? extends Submission> map) {
        submissions.putAll(map);
    }

    public Set<Integer> keySet() {
        return submissions.keySet();
    }

    public Collection<Submission> values() {
        return submissions.values();
    }

    public Set<Map.Entry<Integer, Submission>> entrySet() {
        return submissions.entrySet();
    }

    public int size() {
        return submissions.size();
    }

    public Submission remove(Object key) {
        return submissions.remove(key);
    }
}
