package bgu.ac.il.submissionsystem.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.List;

import bgu.ac.il.submissionsystem.R;
import bgu.ac.il.submissionsystem.Utils.Constants;
import bgu.ac.il.submissionsystem.model.Assignment;
import bgu.ac.il.submissionsystem.model.AssignmentsRequest;
import bgu.ac.il.submissionsystem.model.Course;
import bgu.ac.il.submissionsystem.model.CustomSubmissionSystemRequest;
import bgu.ac.il.submissionsystem.model.ErrorListener;
import bgu.ac.il.submissionsystem.model.InformationHolder;
import bgu.ac.il.submissionsystem.model.ListHolder;
import bgu.ac.il.submissionsystem.model.RequestListener;

/**
 * Created by Asaf on 06/01/2016.
 */
public class AssignmentFragment extends Fragment {
    private MainActivity mainActivity;
    private boolean registeredRecivers;
    private Course course;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.assignment_fragment, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registeredRecivers=false;
    }

    @Override
    public void onStart() {
        super.onStart();
    mainActivity=(MainActivity)getActivity();
        course=mainActivity.getSelectedCourse();
        registerbroadcastListeners();
      requestAssignments();

    }

    public void requestAssignments(){
        RequestListener<ListHolder<Assignment>> requestListener= new RequestListener<>(Constants.getAssignmentsIntentName,this.getContext());
        ErrorListener<ListHolder<Assignment>> errorListener= new ErrorListener<>(Constants.getAssignmentsIntentName+"error",this.getContext());
        HashMap<String,String> map= new HashMap<>();
        map.put("csid", InformationHolder.getCsid());
        map.put("action",Constants.showAssignments);
        map.put("course-id",course.getId()+"");
        String url= CustomSubmissionSystemRequest.attachParamsToUrl(InformationHolder.getBaseUrl(), map);
        AssignmentsRequest assignmentsRequest= new AssignmentsRequest(url,requestListener,errorListener);
        mainActivity.getRequestQueue().add(assignmentsRequest);

    }

    private void registerbroadcastListeners(){
        if(!registeredRecivers){
            BroadcastReceiver assignmentReciver= new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    List<Assignment> assignments=(List<Assignment>)intent.getSerializableExtra("response");
                    if(course!=null){
                        for (Assignment ass:assignments) {
                            course.put(ass.getId(),ass);
                        }
                    }


                }
            };
            IntentFilter assignemtnsFilter= new IntentFilter(Constants.assignmetsInentName);
            LocalBroadcastManager localBroadcastManager=LocalBroadcastManager.getInstance(getContext());
            localBroadcastManager.registerReceiver(assignmentReciver,assignemtnsFilter);
            registeredRecivers=true;
        }

    }
}