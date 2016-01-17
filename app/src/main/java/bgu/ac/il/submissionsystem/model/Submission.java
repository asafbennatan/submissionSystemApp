package bgu.ac.il.submissionsystem.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Asaf on 16/01/2016.
 */
public class Submission implements Serializable {
    private int id;
    private String name;
    private Date date;

    public Submission() {
    }

    public Submission(int id, String name, Date date) {
        this.id = id;
        this.name = name;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
