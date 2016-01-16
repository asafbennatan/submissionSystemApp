package bgu.ac.il.submissionsystem.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by Asaf on 07/01/2016.
 */
public class ListHolder<T extends Serializable> implements Serializable{

    private List<T> list;
    private Properties props;

    public ListHolder() {
        list= new ArrayList<>();
    }

    public ListHolder(List<T> list) {
        this.list = list;
        this.props=new Properties();
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public Properties getProps() {
        return props;
    }

    public void setProps(Properties props) {
        this.props = props;
    }
}
