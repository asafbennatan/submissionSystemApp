package bgu.ac.il.submissionsystem.Controller;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Timer;
import java.util.TimerTask;

import bgu.ac.il.submissionsystem.Utils.Constants;
import bgu.ac.il.submissionsystem.model.CustomSubmissionSystemRequest;
import bgu.ac.il.submissionsystem.model.ErrorListener;
import bgu.ac.il.submissionsystem.model.InformationHolder;
import bgu.ac.il.submissionsystem.model.RefreshRequest;
import bgu.ac.il.submissionsystem.model.RequestListener;

/**
 * Created by Asaf on 07/01/2016.
 */
public class RefreshService extends Service
{

    private Timer timer = new Timer();
    RequestQueue requestQueue;


    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        requestQueue=Volley.newRequestQueue(this);
        startRecivers();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sendRequestToServer();   //Your code here
            }
        }, 0, 1*60*1000);//1 Minutes
    }

    public void startRecivers(){
        BroadcastReceiver reciver= new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i("refresh","ok");
            }
        };

        BroadcastReceiver errorReciver= new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i("refresh","error");
            }
        };

        IntentFilter filter = new IntentFilter(Constants.refreshIntentName);
        IntentFilter errorFilter = new IntentFilter(Constants.refreshIntentName+"error");
        LocalBroadcastManager localBroadcastManager=LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(reciver,filter);
        localBroadcastManager.registerReceiver(errorReciver,errorFilter);
    }

    public void sendRequestToServer(){
        RequestListener<Boolean> listener= new RequestListener<>(Constants.refreshIntentName,this);
        ErrorListener<Boolean> errorListener= new ErrorListener<>(Constants.refreshIntentName+"error",this);
        LinkedHashMap<String,String> map= new LinkedHashMap<>();
        map.put("csid", InformationHolder.getCsid());
        map.put("action",Constants.refreshAction);
        String url= CustomSubmissionSystemRequest.attachParamsToUrl(InformationHolder.getBaseUrl(),map);
        RefreshRequest req = new RefreshRequest(url,listener,errorListener);
        req.setParams(map);
        requestQueue.add(req);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

}
