package bgu.ac.il.submissionsystem.model;

import java.util.Date;

/**
 * Created by Asaf on 10/12/2015.
 */
public class Assignment {
    private String id;
    private String name;
    private Date dueDate;
    private boolean submitted;

    public Assignment() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isSubmitted() {
        return submitted;
    }

    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }
}
