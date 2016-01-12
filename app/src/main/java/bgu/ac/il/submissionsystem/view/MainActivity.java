package bgu.ac.il.submissionsystem.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.view.SubMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import bgu.ac.il.submissionsystem.Controller.RefreshService;
import bgu.ac.il.submissionsystem.Controller.RefreshServiceConnection;
import bgu.ac.il.submissionsystem.R;
import bgu.ac.il.submissionsystem.Utils.Constants;
import bgu.ac.il.submissionsystem.model.Course;
import bgu.ac.il.submissionsystem.model.CourseListRequest;
import bgu.ac.il.submissionsystem.model.CustomSubmissionSystemRequest;
import bgu.ac.il.submissionsystem.model.ErrorListener;
import bgu.ac.il.submissionsystem.model.InformationHolder;
import bgu.ac.il.submissionsystem.model.ListHolder;
import bgu.ac.il.submissionsystem.model.LoginRequest;
import bgu.ac.il.submissionsystem.model.RequestListener;
import bgu.ac.il.submissionsystem.model.SubmissionSystemActions;
import bgu.ac.il.submissionsystem.model.SubmissionSystemResponse;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private NavigationView navigationView;
    private Course selectedCourse;
    private RequestQueue requestQueue;
    private RefreshServiceConnection refreshServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/
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
        requestCourses();

    }

    public void bindRefreshService(){
        Intent refreshIntent= new Intent(this, RefreshService.class);
        refreshServiceConnection= new RefreshServiceConnection();
        bindService(refreshIntent,refreshServiceConnection, Context.BIND_AUTO_CREATE);
    }
    public void requestCourses(){
        RequestListener<ListHolder<Course>> listener= new RequestListener<>(Constants.coursesIntentName,this);
        ErrorListener<ListHolder<Course>> errorListener= new ErrorListener<>(Constants.coursesIntentName+"error",this);
        Map<String,String> params = new HashMap<>();
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
        SubMenu courses = m.getItem(R.id.courses_submenu).getSubMenu();
        for (Course course:courseList) {
            if(!InformationHolder.getLoadedCourses().containsKey(course.getId())){
                InformationHolder.getLoadedCourses().put(course.getId(),course);
                courses.add(Menu.NONE,course.getId(),Menu.NONE,course.getName());
            }

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
        //TODO: open course page

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
                ArrayList<Course> listC =(ArrayList<Course>)list.getList();
                Iterator<Course> itr =listC.iterator();
                while (itr.hasNext()) {
                    Course c = itr.next();
                    InformationHolder.putCourse(c.getId(), c);
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
        localBroadcastManager.registerReceiver(courseReceiver,courseIntentFilter);

        localBroadcastManager.registerReceiver(courseReceivererror,courseIntentFiltererror);

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
