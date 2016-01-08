package bgu.ac.il.submissionsystem.model;

import java.util.List;

/**
 * Created by Asaf on 10/12/2015.
 */
public class Group {

    private List<User> users;
    private int id;


    public Group() {
    }

    public Group(int id) {
        this.id = id;
    }

    public Group(List<User> users) {
        this.users = users;
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
}
