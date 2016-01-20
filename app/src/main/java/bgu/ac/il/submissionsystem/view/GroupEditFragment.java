package bgu.ac.il.submissionsystem.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.LinkedHashMap;

import bgu.ac.il.submissionsystem.Controller.AssignmentListAdapter;
import bgu.ac.il.submissionsystem.Controller.UserListAdapter;
import bgu.ac.il.submissionsystem.R;
import bgu.ac.il.submissionsystem.Utils.Constants;
import bgu.ac.il.submissionsystem.model.Assignment;
import bgu.ac.il.submissionsystem.model.AssignmentsRequest;
import bgu.ac.il.submissionsystem.model.Course;
import bgu.ac.il.submissionsystem.model.CustomSubmissionSystemRequest;
import bgu.ac.il.submissionsystem.model.ErrorListener;
import bgu.ac.il.submissionsystem.model.Group;
import bgu.ac.il.submissionsystem.model.InformationHolder;
import bgu.ac.il.submissionsystem.model.ListHolder;
import bgu.ac.il.submissionsystem.model.RequestListener;
import bgu.ac.il.submissionsystem.model.StudentsRequest;
import bgu.ac.il.submissionsystem.model.User;


/**
 * Created by Asaf on 06/01/2016.
 */
public class GroupEditFragment extends Fragment {
    private MainActivity mainActivity;
    private boolean registeredRecivers;
    private ListView userList;
    private UserListAdapter adapter;
    private FloatingActionButton registerButton;
    private ProgressBar busyIndicator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.group_edit_fragment, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registeredRecivers=false;


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userList =(ListView)view.findViewById(R.id.userList);

        mainActivity=(MainActivity)getActivity();
        Bundle args=getArguments();
        int courseId=args.getInt("courseId", -1);
        int assignmentId=args.getInt("assignmentId", -1);
        Group group=InformationHolder.getLoadedCourses().get(courseId).get(assignmentId).getGroup();
        busyIndicator = (ProgressBar)view.findViewById(R.id.progressBar);

        adapter= new UserListAdapter(mainActivity,null,group);
        userList.setAdapter(adapter);
        requestUserPage(courseId,assignmentId);


    }

    @Override
    public void onStart() {
        super.onStart();

        registerbroadcastListeners();


    }

    public void requestUserPage(int courseId,int assId){
        RequestListener<ListHolder<User>> requestListener= new RequestListener<>(Constants.getEditGroupPageIntentName,this.getContext());
        ErrorListener<ListHolder<User>> errorListener= new ErrorListener<>(Constants.getEditGroupPageIntentName+"error",this.getContext());
        LinkedHashMap<String,String> map= new LinkedHashMap<>();
        map.put("csid", InformationHolder.getCsid());
        map.put("action",Constants.SHOW_REGISTER_SG);
        map.put("assignment-id",assId+"");
        String url= CustomSubmissionSystemRequest.attachParamsToUrl(InformationHolder.getBaseUrl(), map);
        map.put("course-id",courseId+"");
        StudentsRequest studentRequest= new StudentsRequest(url,requestListener,errorListener);
        studentRequest.setParams(map);
        busyIndicator.setIndeterminate(true);
        busyIndicator.setVisibility(View.VISIBLE);
        mainActivity.getRequestQueue().add(studentRequest);

    }

    private void registerbroadcastListeners(){
        if(!registeredRecivers){
            BroadcastReceiver userListReciver= new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    ListHolder<User> users=(ListHolder<User>)intent.getSerializableExtra("response");
                    String courseIds=users.getProps().getProperty("courseId", "-1");
                    String assignmentIds=users.getProps().getProperty("assID", "-1");
                    String groupIdS=users.getProps().getProperty("groupId", "-1");
                    if(courseIds!=null&&!courseIds.isEmpty()&&assignmentIds!=null&&
                            !assignmentIds.isEmpty()&&groupIdS!=null&&!groupIdS.isEmpty()){
                        int courseId=Integer.parseInt(courseIds);
                        int assId=Integer.parseInt(assignmentIds);
                        int groupId=Integer.parseInt(groupIdS);
                        Course course=InformationHolder.getLoadedCourses().get(courseId);
                        Assignment assignment=course.get(assId);
                        if(assignment.getGroup()==null){
                            assignment.setGroup(new Group(groupId));
                            assignment.getGroup().setAssignment(assignment);

                        }
                        Group group=assignment.getGroup();
                        for (User user:users.getList()) {
                            InformationHolder.getAvailableUsers().put(user.getId(), user);
                        }
                        adapter.updateData(InformationHolder.getAvailableUsers(),group);

                    }
                    busyIndicator.setVisibility(View.GONE);


                }
            };
            BroadcastReceiver userListErrorReciver= new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if(busyIndicator!=null){
                        busyIndicator.setVisibility(View.GONE);
                    }



                }
            };
            IntentFilter userListFilter= new IntentFilter(Constants.getEditGroupPageIntentName);
            IntentFilter userListErrorFilter= new IntentFilter(Constants.getEditGroupPageIntentName+"error");
            LocalBroadcastManager localBroadcastManager=LocalBroadcastManager.getInstance(getActivity());
            localBroadcastManager.registerReceiver(userListReciver,userListFilter);
            localBroadcastManager.registerReceiver(userListErrorReciver,userListErrorFilter);
            registeredRecivers=true;
        }

    }
}