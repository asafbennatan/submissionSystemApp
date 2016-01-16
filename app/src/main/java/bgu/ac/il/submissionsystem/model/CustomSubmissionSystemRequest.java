package bgu.ac.il.submissionsystem.model;

import android.util.Log;
import android.util.Xml;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Asaf on 03/01/2016.
 */
public abstract class CustomSubmissionSystemRequest<T> extends Request<T> implements Serializable{
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

    public void setParams(Map<? extends String, ? extends String> map) {
        params.putAll(map);
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
        String string=new String(response.data, StandardCharsets.UTF_8);
        Log.i("recived html",string);
        Document doc = Jsoup.parse(string);
        try {


            T toRet=createResponse(doc);
            Cache.Entry header= HttpHeaderParser.parseCacheHeaders(response);
            Response<T> r= Response.success(toRet,header);
            return r;
        }
        catch(Exception  e) {
            String s=parseError(doc);
            return Response.error(new ParseError(e)
                    );
        }


    }

    public String getParam(String paramName){
        return params.get(paramName);
    }

protected String parseError(Document doc){
    Element el=doc.getElementsByAttributeValue("class", "error-title").last();
    if(el!=null){
       return el.text();
    }
    return "";
}

    public static String attachParamsToUrl(String url,Map<String,String> params){

        StringBuilder builder = new StringBuilder();

        for (String key : params.keySet())
        {
            Object value = params.get(key);
            if (value != null)
            {
                try
                {
                    value = URLEncoder.encode(String.valueOf(value), StandardCharsets.UTF_8.name());


                    if (builder.length() > 0)
                        builder.append("&");
                    builder.append(key).append("=").append(value);
                }
                catch (UnsupportedEncodingException e)
                {
                    Log.e("CusomReq","error while adding params");
                }
            }
        }

        url += "?" + builder.toString();
        return url;
    }

    protected abstract T createResponse(Document parser) throws ParseError;


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
