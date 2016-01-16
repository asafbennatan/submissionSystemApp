package bgu.ac.il.submissionsystem.model;

import com.android.volley.ParseError;
import com.android.volley.Response;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.List;

/**
 * Created by Asaf on 03/01/2016.
 */
public class FrodoBodyRequest extends CustomSubmissionSystemRequest<Integer>{

    public FrodoBodyRequest(String url, Response.Listener<Integer> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, listener, errorListener);
    }

    @Override
    protected Integer createResponse(Document document) throws ParseError{
        try{
            SubmissionSystemResponse res= new SubmissionSystemResponse();
            Element el=document.getElementsByAttributeValue("name", "user-hash-code").get(2);
            int val=Integer.parseInt(el.attr("value"));
            return val;
        }
        catch(Exception e){
            throw new ParseError(e);
        }





    }




}
