package bgu.ac.il.submissionsystem.model;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.io.Serializable;

/**
 * Created by Asaf on 03/01/2016.
 */
public class ErrorListener<T extends Serializable> implements Response.ErrorListener{
    private String name;
    private Context context;

    public ErrorListener(String name, Context context) {
        this.name=name;
    }


    @Override
    public void onErrorResponse(VolleyError error) {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
        Intent intent = new Intent(name);
        intent.putExtra("response",error.toString());
        localBroadcastManager.sendBroadcast(intent);

    }
}
