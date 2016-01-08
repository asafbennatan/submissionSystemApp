package bgu.ac.il.submissionsystem.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Asaf on 07/01/2016.
 */
public class ListHolder<T extends Serializable> implements Serializable{

    private List<T> list;

    public ListHolder() {
    }

    public ListHolder(List<T> list) {
        this.list = list;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
