package bgu.ac.il.submissionsystem.model;

import android.util.Log;
import android.util.Xml;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Asaf on 03/01/2016.
 */
public abstract class CustomSubmissionSystemRequest<T> extends Request<T> {
    HashMap<String,String> headers= new HashMap<>();
    HashMap<String,String> params= new HashMap<>();
    private Response.Listener<T> listener;
    public final static String ns=null;

    public CustomSubmissionSystemRequest(int method, String url, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.listener=listener;
    }
    public void setHeader(String name,String value){
        headers.put(name, value);
    }

    public void setParam(String name,String value){
        params.put(name, value);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        Map<String,String> orig=super.getParams();
        HashMap<String,String> toRet= new HashMap<>();
        toRet.putAll(orig);
        toRet.putAll(params);
        return toRet;
    }


    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        XmlPullParser parser = Xml.newPullParser();
        ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(response.data);
        try{
            parser.setInput(byteArrayInputStream, null);
            parser.nextTag();
            T toRet=readFeed(parser);
            return Response.success(
                   toRet,
                    HttpHeaderParser.parseCacheHeaders(response));

        }
        catch(XmlPullParserException | IOException e){
            Log.e("LoginRequest", "error parsing xml", e);
        }


        return Response.error(
                new ParseError());

    }


    protected abstract T readFeed(XmlPullParser parser) throws XmlPullParserException, IOException;

    protected void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    public HashMap<String, String> getHeaders() throws AuthFailureError{
        Map<String,String> orig=super.getHeaders();
        HashMap<String,String> toRet= new HashMap<>();
        toRet.putAll(orig);
        toRet.putAll(headers);
        return toRet;
    }


}
