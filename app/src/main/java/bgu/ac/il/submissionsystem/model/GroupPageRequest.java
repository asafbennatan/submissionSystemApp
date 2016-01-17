package bgu.ac.il.submissionsystem.model;

import com.android.volley.ParseError;
import com.android.volley.Response;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import bgu.ac.il.submissionsystem.Utils.Constants;

/**
 * Created by Asaf on 03/01/2016.
 */
public class GroupPageRequest extends CustomSubmissionSystemRequest<ListHolder<Submission>> {

    public GroupPageRequest(String url, Response.Listener<ListHolder<Submission>> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, listener, errorListener);
    }

    @Override
    protected ListHolder<Submission> createResponse(Document document) throws ParseError {
        ListHolder<Submission> list= new ListHolder<>();
        List<Submission> subs= new ArrayList<>();
        try {
            List<Element> els = document.getElementsByAttribute("href");
            Submission sub = null;
            for (Element e : els) {
                if(e.tag().getName().equals("a")){
                    String name = e.text();
                    String date = e.parent().siblingElements().first().text();
                    String url = e.attr("href");
                    int s = url.indexOf("submitted-work-id=") + "submitted-work-id=".length();
                    String idS = url.substring(s, url.length());
                    int id = Integer.parseInt(idS);
                    sub = new Submission();
                    sub.setId(id);
                    sub.setName(name);
                    sub.setDate(Constants.parseDate(date));
                    subs.add(sub);
                }

            }
            list.setList(subs);
            String assignmentId=getParam(Constants.assignmentId);
            String courseId=getParam("course-id");
            list.getProps().setProperty("assignmentId",assignmentId);
            list.getProps().setProperty("courseId",courseId);
        }
        catch(Exception e){
            throw new ParseError(e);
        }
        return list;

    }



}