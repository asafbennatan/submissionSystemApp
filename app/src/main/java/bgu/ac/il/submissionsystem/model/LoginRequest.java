package bgu.ac.il.submissionsystem.model;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.ParseError;
import com.android.volley.Response;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Asaf on 03/01/2016.
 */
public class LoginRequest  extends CustomSubmissionSystemRequest<SubmissionSystemResponse>{

    public LoginRequest(String url, Response.Listener<SubmissionSystemResponse> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, listener, errorListener);
    }

    @Override
    protected SubmissionSystemResponse createResponse(Document document) throws ParseError{
        SubmissionSystemResponse res= new SubmissionSystemResponse();
        List<Element> els=document.getElementsByTag("frame");
        String csid=null;
        for (Element el:els) {
            String attr=el.attr("src");
            if(!attr.equals("")){
                int start=attr.indexOf("csid=")+"csid=".length();
                int end=attr.indexOf("&");
                csid=attr.substring(start,end);

                break;
            }
        }

        if(csid==null){
            throw new ParseError(new Exception("unable to find csid"));
        }
        res.put("csid",csid);
        String username=getParam("login");
        if(username==null){
            username="";
        }
        res.put("username",username);


        return res;

    }




}
