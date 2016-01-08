package bgu.ac.il.submissionsystem.model;

import com.android.volley.ParseError;
import com.android.volley.Response;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.List;

/**
 * Created by Asaf on 03/01/2016.
 */
public class RefreshRequest extends CustomSubmissionSystemRequest<Boolean>{

    public RefreshRequest(String url, Response.Listener<Boolean> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, listener, errorListener);
    }

    @Override
    protected Boolean createResponse(Document document) throws ParseError{
        String s=document.toString();
        if (s.contains("error")){
            throw new ParseError(new Exception("refresh failed"));
        }

     return true;

    }




}
