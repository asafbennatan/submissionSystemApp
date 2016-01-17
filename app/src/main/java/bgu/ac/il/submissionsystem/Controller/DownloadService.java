package bgu.ac.il.submissionsystem.Controller;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.webkit.MimeTypeMap;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import bgu.ac.il.submissionsystem.R;

/**
 * Created by Asaf on 17/01/2016.
 */
public class DownloadService extends IntentService{
    private NotificationManager mNotifyManager;
    private static AtomicInteger nextId;
    private String downloadDir="/sdcard/";



    public DownloadService() {
        super("SubmissionSystemDownloadService");
        if(nextId==null){
            nextId= new AtomicInteger(0);
        }

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int id=nextId.getAndIncrement();
        String downloadurl=intent.getStringExtra("url");
        String name=intent.getStringExtra("name");
        downloadFile(downloadurl, id,name);


    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        mNotifyManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    }

    private boolean downloadFile(String urlS,int id,String name){
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_stat_name)
                        .setContentTitle("Downloading "+name)
                        .setContentText("downloading");
        String mime="application/octet-stream";

        try {
            URL url = new URL(urlS);
            connection = (HttpURLConnection) url.openConnection();
            mime= MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExt(name));
            connection.setRequestProperty("Content-Type", mime);
            connection.connect();


            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return false;
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();
            Map<String,List<String>> check= connection.getHeaderFields();

            String header = connection.getHeaderField("Content-Disposition");
            if(header!=null){
                String[] split = header.split("filename=\"");

                if (split.length > 1) {
                    name = split[1].substring(0,split[1].length()-1);
                }
            }


            // download the file
            input = connection.getInputStream();
            output = new FileOutputStream(downloadDir + name);



            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button

                total += count;
                mBuilder.setProgress(fileLength,(int)total,false);
                mNotifyManager.notify(id, mBuilder.build());
                // publishing the progress....
                if (fileLength > 0) // only if total length is known

                output.write(data, 0, count);
            }
        } catch (Exception e) {
            closeNotification(mBuilder,id,"Download Failed",false,null,null);
            return false;
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }

        closeNotification(mBuilder,id,"Download Complete",true,mime,new File(downloadDir+name));
        return true;
    }

    private void closeNotification(NotificationCompat.Builder builder,int id,String status,boolean done,String mimeType,File file){
        builder.setContentText(status)
                // Removes the progress bar
                .setProgress(0, 0, false);

        Notification notification=builder.build();
        if(done){
            Intent notificationIntent = new Intent(Intent.ACTION_VIEW);
            notificationIntent.setDataAndType(Uri.fromFile(file),mimeType);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            PendingIntent intent = PendingIntent.getActivity(this, id,
                    notificationIntent, 0);
            notification.contentIntent=intent;

            notification.flags |= Notification.FLAG_AUTO_CANCEL;
        }
        mNotifyManager.notify(id, notification);


    }

    private void notificationRedirect(){

    }

    private String fileExt(String name) {
        List<String> EXTS = Arrays.asList("tar.gz");
        String lowerName=name.toLowerCase();
        for (String ext:EXTS) {
            if(lowerName.endsWith(ext.toLowerCase())){
                return ext;
            }
        }
        String[] split=name.split("\\.");
        return split[split.length-1];
    }
}
