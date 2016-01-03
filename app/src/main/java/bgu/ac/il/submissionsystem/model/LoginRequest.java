package bgu.ac.il.submissionsystem.model;

import com.android.volley.Response;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Asaf on 03/01/2016.
 */
public class LoginRequest  extends CustomSubmissionSystemRequest<SubmissionSystemResponse>{

    public LoginRequest(int method, String url, Response.Listener<SubmissionSystemResponse> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    @Override
    protected SubmissionSystemResponse readFeed(XmlPullParser parser) throws XmlPullParserException, IOException{
        SubmissionSystemResponse res= new SubmissionSystemResponse();
        parser.require(XmlPullParser.START_TAG, ns, "html");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("frame")) {
                String link = parser.getAttributeValue(null, "src");
                int location=link.indexOf("csid=")+"csid=".length();
                int end=link.indexOf("&");
                String csid=link.substring(location,end);
                res.put("csid",csid);

            } else {
                skip(parser);
            }
        }
        return res;

    }




}
