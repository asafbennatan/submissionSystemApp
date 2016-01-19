package bgu.ac.il.submissionsystem.Controller;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import bgu.ac.il.submissionsystem.R;

/**
 * Created by Asaf on 17/01/2016.
 */
public class NotificationCreatorService extends IntentService{
    private static AtomicInteger nextId=new AtomicInteger(0);

    public NotificationCreatorService() {
        super("notificationCreatorService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int id=intent.getIntExtra("id", -1);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if(id==-1){
            String header=intent.getStringExtra("header");
            String text=intent.getStringExtra("text");
            Intent dismissIntent=new Intent(this, NotificationCreatorService.class);
            id=nextId.getAndIncrement();
            dismissIntent.putExtra("id",id);
            PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(this, id, dismissIntent, 0);
            Notification.Builder builder= new Notification.Builder(this)
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setContentTitle(header)
                    .setContentText(text)
                    .setContentIntent(dismissPendingIntent)
                    .setOngoing(true);


            notificationManager.notify(id, builder.build());
        }
        else{
                notificationManager.cancel(id);
        }




    }
}
