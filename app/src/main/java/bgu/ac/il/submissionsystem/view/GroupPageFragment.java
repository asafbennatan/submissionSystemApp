package bgu.ac.il.submissionsystem.view;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import bgu.ac.il.submissionsystem.Controller.AssignmentListAdapter;
import bgu.ac.il.submissionsystem.Controller.SubmissionListAdapter;
import bgu.ac.il.submissionsystem.R;
import bgu.ac.il.submissionsystem.Utils.Constants;
import bgu.ac.il.submissionsystem.model.Assignment;
import bgu.ac.il.submissionsystem.model.AssignmentsRequest;
import bgu.ac.il.submissionsystem.model.Course;
import bgu.ac.il.submissionsystem.model.CustomSubmissionSystemRequest;
import bgu.ac.il.submissionsystem.model.ErrorListener;
import bgu.ac.il.submissionsystem.model.Group;
import bgu.ac.il.submissionsystem.model.GroupPageRequest;
import bgu.ac.il.submissionsystem.model.InformationHolder;
import bgu.ac.il.submissionsystem.model.ListHolder;
import bgu.ac.il.submissionsystem.model.RequestListener;
import bgu.ac.il.submissionsystem.model.Submission;
import bgu.ac.il.submissionsystem.model.SubmissionSystemResponse;

/**
 * Created by Asaf on 06/01/2016.
 */
public class GroupPageFragment extends Fragment {
    private MainActivity mainActivity;
    private boolean registeredRecivers;
    private ListView assList;
    private SubmissionListAdapter adapter;
    private TextView name;
    private TextView deadline;
    private TextView publishDate;
    private TextView publisher;
    private TextView grade;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.group_page_fragment, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registeredRecivers=false;


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assList=(ListView)view.findViewById(R.id.subList);
        mainActivity=(MainActivity)getActivity();
        adapter= new SubmissionListAdapter(mainActivity,null);
        assList.setAdapter(adapter);
        name=(TextView)view.findViewById(R.id.SGPassignmentName);
        publisher=(TextView)view.findViewById(R.id.SGPpublisher);
        deadline=(TextView)view.findViewById(R.id.SGPdeadline);
        grade=(TextView)view.findViewById(R.id.SGPgrade);
        publishDate=(TextView)view.findViewById(R.id.SGPpublishDate);
        Bundle bundle=getArguments();
        int groupId=bundle.getInt("groupId");
        int assignmentId=bundle.getInt("assignmentId");
        int courseId=bundle.getInt("courseId");
       updateInfo(groupId,assignmentId,courseId);



    }




    private void updateInfo(int groupId,int assigmentId,int courseId){
        Assignment assignment=InformationHolder.getLoadedCourses().get(courseId).get(assigmentId);
        updateFields(assignment);
        requestGroupPage(groupId, assigmentId, courseId);
    }

    private void updateFields(Assignment assignment){
        name.setText(assignment.getName());
        publisher.setText("Publisher :" + assignment.getPublisher());
        publishDate.setText("Publish Date :" + Constants.formatDate(assignment.getPublishDate()));
        deadline.setText("Deadline :" + Constants.formatDate(assignment.getDeadline()));
        grade.setText("Grade: " + assignment.getGrade() + "");

    }

    private void requestGroupPage(int groupId,int assigmentId,int courseId){
        RequestListener<ListHolder<Submission>> requestListener= new RequestListener<>(Constants.getGroupPageIntentName,this.getContext());
        ErrorListener<ListHolder<Submission>> errorListener= new ErrorListener<>(Constants.getGroupPageIntentName+"error",this.getContext());
        LinkedHashMap<String,String> map= new LinkedHashMap<>();
        map.put("csid", InformationHolder.getCsid());
        map.put("action",Constants.SHOW_EDIT_SG);
       map.put(Constants.assignmentId,assigmentId+"");
        map.put(Constants.submittalGroupId,groupId+"");
        String url= CustomSubmissionSystemRequest.attachParamsToUrl(InformationHolder.getBaseUrl(), map);
        map.put("course-id", courseId + "");
        GroupPageRequest groupPageRequest= new GroupPageRequest(url,requestListener,errorListener);
        groupPageRequest.setParams(map);
        this.mainActivity.getRequestQueue().add(groupPageRequest);

    }

    private void registerbroadcastListeners() {
        if (!registeredRecivers) {
            BroadcastReceiver groupReciver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    ListHolder<Submission> submissions=(ListHolder<Submission>)intent.getSerializableExtra("response");
                    int assId=Integer.parseInt(submissions.getProps().getProperty("assignmentId","-1"));
                    int courseId=Integer.parseInt(submissions.getProps().getProperty("courseId","-1"));
                    Group group=InformationHolder.getLoadedCourses().get(courseId).get(assId).getGroup();
                    if(assId!=-1&&courseId!=-1){
                        for (Submission sub:submissions.getList()) {
                            group.put(sub.getId(),sub);
                        }
                        adapter.updateData(group.getSubmissions());
                    }

                }
            };
            BroadcastReceiver groupErrorReciver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.e("groupPage", intent.getStringExtra("response"));


                }
            };
            IntentFilter groupFilter = new IntentFilter(Constants.getGroupPageIntentName);
            IntentFilter groupsErrorFilter = new IntentFilter(Constants.getGroupPageIntentName + "error");
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
            localBroadcastManager.registerReceiver(groupReciver, groupFilter);
            localBroadcastManager.registerReceiver(groupErrorReciver, groupsErrorFilter);
            registeredRecivers = true;
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        registerbroadcastListeners();



    }






}