package bgu.ac.il.submissionsystem.model;

import android.support.annotation.NonNull;

import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by niran-lap on 1/10/2016.
 */
public class CourseListRequest extends CustomSubmissionSystemRequest<ListHolder<Course>>{

    public CourseListRequest(String url, Response.Listener<ListHolder<Course>> listener, Response.ErrorListener errorListener) {
        super(Request.Method.GET, url, listener, errorListener);
    }

    @Override
    protected ListHolder<Course> createResponse(Document document) throws ParseError {
        List<Course> list = new ArrayList<>();

        List<Element> els=document.getElementsByTag("option");
        for (Element el:els) {
            int val=Integer.parseInt(el.attr("value"));
            String name=el.text();
            list.add(new Course(name, val));
        }

        return new ListHolder(list);

    }




}
