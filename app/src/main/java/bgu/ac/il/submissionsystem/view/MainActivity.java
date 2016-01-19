package bgu.ac.il.submissionsystem.view;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.MimeTypeMap;
import android.widget.TextView;

import com.alexbbb.uploadservice.MultipartUploadRequest;
import com.alexbbb.uploadservice.UploadNotificationConfig;
import com.alexbbb.uploadservice.UploadServiceBroadcastReceiver;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.nbsp.materialfilepicker.utils.FileUtils;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import bgu.ac.il.submissionsystem.Controller.DownloadService;
import bgu.ac.il.submissionsystem.Controller.RefreshService;
import bgu.ac.il.submissionsystem.Controller.RefreshServiceConnection;
import bgu.ac.il.submissionsystem.R;
import bgu.ac.il.submissionsystem.Utils.Constants;
import bgu.ac.il.submissionsystem.model.Assignment;
import bgu.ac.il.submissionsystem.model.Course;
import bgu.ac.il.submissionsystem.model.CourseListRequest;
import bgu.ac.il.submissionsystem.model.CustomSubmissionSystemRequest;
import bgu.ac.il.submissionsystem.model.DownloadFileAsyncTask;
import bgu.ac.il.submissionsystem.model.ErrorListener;
import bgu.ac.il.submissionsystem.model.Group;
import bgu.ac.il.submissionsystem.model.InformationHolder;
import bgu.ac.il.submissionsystem.model.ListHolder;
import bgu.ac.il.submissionsystem.model.RequestListener;
import bgu.ac.il.submissionsystem.model.Submission;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private NavigationView navigationView;
    private Course selectedCourse;
    private RequestQueue requestQueue;
    private RefreshServiceConnection refreshServiceConnection;
    private Menu menu;
    private AssignmentFragment assignmentFragment;
    private GroupPageFragment groupPageFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.setDrawerListener(toggle);

        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        //must progrematicly inflate headerview so text,image are available
        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        TextView t=(TextView)headerView.findViewById(R.id.username_holder);
        t.setText(InformationHolder.getUsername());
        bindRefreshService();
        navigationView.setNavigationItemSelectedListener(this);
        requestQueue= Volley.newRequestQueue(this);
        registerBroadcasts();
        requestCourses();
        assignmentFragment= new AssignmentFragment();

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.content_frame, assignmentFragment);
        transaction.commit();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void bindRefreshService(){
        Intent refreshIntent= new Intent(this, RefreshService.class);
        refreshServiceConnection= new RefreshServiceConnection();
        bindService(refreshIntent, refreshServiceConnection, Context.BIND_AUTO_CREATE);
    }
    public void requestCourses(){
        RequestListener<ListHolder<Course>> listener= new RequestListener<>(Constants.coursesIntentName,this);
        ErrorListener<ListHolder<Course>> errorListener= new ErrorListener<>(Constants.coursesIntentName+"error",this);
        LinkedHashMap<String,String> params = new LinkedHashMap<>();
        params.put("csid", InformationHolder.getCsid());
        params.put("action", Constants.MENU_ACTION);
        String url= CustomSubmissionSystemRequest.attachParamsToUrl(InformationHolder.getBaseUrl(), params);
        CourseListRequest courseListRequest= new CourseListRequest(url,listener,errorListener);
        courseListRequest.setParams(params);

        requestQueue.add(courseListRequest);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void updateNavigationDrawerMenu(List<Course> courseList){
        Menu m = navigationView.getMenu();
        for (Course course:courseList) {
            if(m.findItem(course.getId())==null){
                InformationHolder.getLoadedCourses().put(course.getId(),course);
                m.add(Menu.NONE,course.getId(),Menu.NONE,course.getName());
            }

        }



    }

    public void startGroupPage(Assignment assignment){
        if(assignment.getGroup()!=null){
            if(groupPageFragment==null){
                groupPageFragment = new GroupPageFragment();
            }
            Bundle bundle= new Bundle();
            bundle.putInt("groupId",assignment.getGroup().getId());
            bundle.putInt("assignmentId",assignment.getId());
            bundle.putInt("courseId", assignment.getCourseId());
            groupPageFragment.setArguments(bundle);
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(R.id.content_frame, groupPageFragment);
            transaction.commit();


        }

    }



    public void uploadFile(int groupId,String filePath){

        final String uploadID = UUID.randomUUID().toString();
        final String serverUrlString = InformationHolder.getBaseUrl();
        final UploadServiceBroadcastReceiver uploadReceiver =
                new UploadServiceBroadcastReceiver() {
                    private static final String TAG = "AndroidUploadService";
                                        // you can override this progress method if you want to get
                    // the completion progress in percent (0 to 100)
                    // or if you need to know exactly how many bytes have been transferred
                    // override the method below this one
                    @Override
                    public void onProgress(String uploadId, int progress) {
                        Log.i(TAG, "The progress of the upload with ID "
                                + uploadId + " is: " + progress);
                    }

                    @Override
                    public void onProgress(final String uploadId,
                                           final long uploadedBytes,
                                           final long totalBytes) {
                        Log.i(TAG, "Upload with ID " + uploadId +
                                " uploaded bytes: " + uploadedBytes
                                + ", total: " + totalBytes);
                    }

                    @Override
                    public void onError(String uploadId, Exception exception) {
                        Log.e(TAG, "Error in upload with ID: " + uploadId + ". "
                                + exception.getLocalizedMessage(), exception);
                    }

                    @Override
                    public void onCompleted(String uploadId,
                                            int serverResponseCode,
                                            String serverResponseMessage) {
                        Log.i(TAG, "Upload with ID " + uploadId
                                + " has been completed with HTTP " + serverResponseCode
                                + ". Response from server: " + serverResponseMessage);

                        //If your server responds with a JSON, you can parse it
                        //from serverResponseMessage string using a library
                        //such as org.json (embedded in Android) or Google's gson
                    }
                };
        String filename= FilenameUtils.getName(filePath);
        String mime=MimeTypeMap.getSingleton().getMimeTypeFromExtension(Constants.fileExt(filename));
        uploadReceiver.register(this);
        try {
            new MultipartUploadRequest(this, uploadID, serverUrlString)
                    .addFileToUpload(filePath, "submitted-work", filename, mime)
                    .addParameter("csid", InformationHolder.getCsid())
                    .addHeader("Accept-Charset","utf-8")
                    .addParameter("action", Constants.SUBMIT_WORK)
                    .addParameter("submittal-group-id",groupId+"")
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .startUpload();
        } catch (Exception exc) {
            Log.e("AndroidUploadService", exc.getMessage(), exc);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
        //test
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showCourseAssignments(Course course) {

        assignmentFragment.requestAssignments(course);
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Course selected=InformationHolder.getLoadedCourses().get(id);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        if(selected!=null){
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(R.id.content_frame, assignmentFragment);
            transaction.commit();
            showCourseAssignments(selected);
            return true;
        }

        return false;
    }





    private void registerBroadcasts(){
        BroadcastReceiver courseReceiver= new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ListHolder<Course> list=( ListHolder<Course>) intent.getSerializableExtra("response");
                if(list!=null){
                    List<Course> listC =list.getList();
                    updateNavigationDrawerMenu(listC);

                }





            }
        };
        IntentFilter courseIntentFilter= new IntentFilter(Constants.coursesIntentName);

        BroadcastReceiver courseReceivererror= new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

            }
        };
        IntentFilter courseIntentFiltererror= new IntentFilter(Constants.loginIntentName+"error");


        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(courseReceiver, courseIntentFilter);

        localBroadcastManager.registerReceiver(courseReceivererror, courseIntentFiltererror);

    }


    public void downloadSubmittedWork(Submission submission){
        LinkedHashMap<String,String> params=new LinkedHashMap<>();
        params.put("csid", InformationHolder.getCsid());
        params.put("action", Constants.GET_FILE);
        params.put("submitted-work-id", submission.getId() + "");

        String url =CustomSubmissionSystemRequest.attachParamsToUrl(InformationHolder.getBaseUrl(),params);
        Intent intent= new Intent(this, DownloadService.class);
        intent.putExtra("url",url);
        intent.putExtra("name",Constants.normalize(submission.getName()));
       startService(intent);



    }



    public Course getSelectedCourse() {
        return selectedCourse;
    }

    public void setSelectedCourse(Course selectedCourse) {
        this.selectedCourse = selectedCourse;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public void setRequestQueue(RequestQueue requestQueue) {
        this.requestQueue = requestQueue;
    }
}
