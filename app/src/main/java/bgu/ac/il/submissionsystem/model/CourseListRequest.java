package bgu.ac.il.submissionsystem.model;

import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.List;

/**
 * Created by niran-lap on 1/10/2016.
 */
public class CourseListRequest extends CustomSubmissionSystemRequest<Boolean>{

    public CourseListRequest(String url, Response.Listener<Boolean> listener, Response.ErrorListener errorListener) {
        super(Request.Method.GET, url, listener, errorListener);
    }

    @Override
    protected Boolean createResponse(Document document) throws ParseError {
        SubmissionSystemResponse res= new SubmissionSystemResponse();
        List<Element> els=document.getElementsByTag("option");
        for (Element el:els) {
            int val=Integer.parseInt(el.attr("value"));
            String name=el.text();
            InformationHolder.putCourse(val,new Course(name,val));
        }

        return true;

    }




}
