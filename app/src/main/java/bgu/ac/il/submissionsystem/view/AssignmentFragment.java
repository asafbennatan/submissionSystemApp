package bgu.ac.il.submissionsystem.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.HashMap;

import bgu.ac.il.submissionsystem.Controller.AssignmentListAdapter;
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
    private ListView assList;
    private AssignmentListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.assignments_fragment, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registeredRecivers=false;


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assList=(ListView)view.findViewById(R.id.asslist);
        adapter= new AssignmentListAdapter(getActivity(),null);
        assList.setAdapter(adapter);


    }

    @Override
    public void onStart() {
        super.onStart();
    mainActivity=(MainActivity)getActivity();
        registerbroadcastListeners();


    }

    public void requestAssignments(Course course){
        RequestListener<ListHolder<Assignment>> requestListener= new RequestListener<>(Constants.getAssignmentsIntentName,this.getContext());
        ErrorListener<ListHolder<Assignment>> errorListener= new ErrorListener<>(Constants.getAssignmentsIntentName+"error",this.getContext());
        HashMap<String,String> map= new HashMap<>();
        map.put("csid", InformationHolder.getCsid());
        map.put("action",Constants.showAssignments);
        map.put("course-id",course.getId()+"");
        String url= CustomSubmissionSystemRequest.attachParamsToUrl(InformationHolder.getBaseUrl(), map);
        AssignmentsRequest assignmentsRequest= new AssignmentsRequest(url,requestListener,errorListener);
        assignmentsRequest.setParams(map);
        mainActivity.getRequestQueue().add(assignmentsRequest);

    }

    private void registerbroadcastListeners(){
        if(!registeredRecivers){
            BroadcastReceiver assignmentReciver= new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    ListHolder<Assignment> assignments=(ListHolder<Assignment>)intent.getSerializableExtra("response");
                    String courseIds=assignments.getProps().getProperty("courseId", "-1");
                    if(courseIds!=null&&!courseIds.isEmpty()){
                        int courseId=Integer.parseInt(courseIds);
                        Course course=InformationHolder.getLoadedCourses().get(courseId);
                        if(course!=null){
                            for (Assignment ass:assignments.getList()) {
                                course.put(ass.getId(), ass);

                            }
                            adapter.updateData(course.getAssignments());
                        }
                    }



                }
            };
            BroadcastReceiver assignmentErrorReciver= new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.e("assignmentReciver",intent.getStringExtra("response"));


                }
            };
            IntentFilter assignemtnsFilter= new IntentFilter(Constants.getAssignmentsIntentName);
            IntentFilter assignemtnsErrorFilter= new IntentFilter(Constants.getAssignmentsIntentName+"error");
            LocalBroadcastManager localBroadcastManager=LocalBroadcastManager.getInstance(getActivity());
            localBroadcastManager.registerReceiver(assignmentReciver,assignemtnsFilter);
            localBroadcastManager.registerReceiver(assignmentErrorReciver,assignemtnsErrorFilter);
            registeredRecivers=true;
        }

    }
}