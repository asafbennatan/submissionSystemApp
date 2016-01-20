package bgu.ac.il.submissionsystem.model;

import com.android.volley.ParseError;
import com.android.volley.Response;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Asaf on 20/01/2016.
 */
public class StudentsRequest extends CustomSubmissionSystemRequest<ListHolder<User>>{

    public StudentsRequest( String url, Response.Listener<ListHolder<User>> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, listener, errorListener);
    }

    @Override
    protected ListHolder<User> createResponse(Document parser) throws ParseError {
        List<User> users= new ArrayList<>();
        String groupId="";
        try {
            Elements groupIdHolder = parser.getElementsByAttributeValueContaining("name", "submittal-group-id");
            if(!groupIdHolder.isEmpty()){
                Element el=groupIdHolder.first();
               groupId=el.attr("value");

            }
            List<Element> els = parser.getElementsByAttributeValueContaining("name", "student-id-");
            User user = null;
            for (Element el : els) {
                String name = el.parent().parent().text();
                String nameTag = el.attr("name");
                nameTag=nameTag.substring("student-id-".length(),nameTag.length() );
                int id = Integer.parseInt(nameTag);
                user = new User(name, id);
                users.add(user);
            }
            ListHolder<User> list = new ListHolder<>(users);
            String assignmetId = getParam("assignment-id");
            String courseId=getParam("course-id");
            list.getProps().setProperty("assID", assignmetId);
            list.getProps().setProperty("groupId",groupId);
            list.getProps().setProperty("courseId",courseId);
            return list;
        }
        catch(Exception e){
            throw new ParseError(e);
        }
    }
}
