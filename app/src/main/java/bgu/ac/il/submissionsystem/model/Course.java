package bgu.ac.il.submissionsystem.model;

import java.util.List;

/**
 * Created by Asaf on 10/12/2015.
 */
public class Course {
    private String name;
    private int id;

    private List<Assignment> assignments;

    public Course() {
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

    public List<Assignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<Assignment> assignments) {
        this.assignments = assignments;
    }
}
