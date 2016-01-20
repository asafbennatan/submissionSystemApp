package bgu.ac.il.submissionsystem.Controller;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import bgu.ac.il.submissionsystem.R;
import bgu.ac.il.submissionsystem.Utils.Constants;
import bgu.ac.il.submissionsystem.model.Assignment;
import bgu.ac.il.submissionsystem.view.MainActivity;

/**
 * Created by Asaf on 15/01/2016.
 */
public class AssignmentListAdapter extends BaseAdapter {
    private Map<Integer,Assignment> map;
    private ArrayList<Assignment> values;
    private MainActivity activity;
    private LayoutInflater inflater;

    public AssignmentListAdapter(MainActivity activity, Map<Integer, Assignment> map) {
        this.map = map;
        if(this.map==null){
            this.map= new HashMap<>();
        }
        values=new ArrayList<>(this.map.values());
        this.activity=activity;
        inflater = ( LayoutInflater )activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return map.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder{

        public TextView name;
        public TextView deadline;
        public TextView publishDate;
        public TextView publisher;
        public TextView grade;


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.list_item, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ViewHolder();
            holder.name = (TextView) vi.findViewById(R.id.assignmentName);
            holder.deadline =(TextView)vi.findViewById(R.id.deadline);
            holder.name.setFocusable(false);
            holder.name.setClickable(false);
            holder.deadline.setFocusable(false);
            holder.deadline.setClickable(false);
            holder.publishDate =(TextView)vi.findViewById(R.id.publishDate);
            holder.publisher =(TextView)vi.findViewById(R.id.publisher);
            holder.grade =(TextView)vi.findViewById(R.id.grade);
            holder.publishDate.setFocusable(false);
            holder.publishDate.setClickable(false);
            holder.publisher.setFocusable(false);
            holder.publisher.setClickable(false);
            holder.grade.setFocusable(false);
            holder.grade.setClickable(false);
            /************  Set holder with LayoutInflater ************/
            vi.setTag( holder );
        }
        else{
            holder=(ViewHolder)vi.getTag();
        }

        //holder.text.setTextColor(Color.BLACK);
        if(map.size()<=0)
        {
            holder.name.setText("No Data");


        }
        else
        {
           Assignment assignment=values.get(position);

            holder.name.setText( assignment.getName() );
          holder.deadline.setText("Deadline: "+ Constants.formatDate(assignment.getDeadline(),true));
            holder.publishDate.setText("Publish Date: "+Constants.formatDate(assignment.getPublishDate(),false));
            holder.publisher.setText("Publisher: " +assignment.getPublisher());
            holder.grade.setText("Grade: "+assignment.getGrade());

            /******** Set Item Click Listner for LayoutInflater for each row *******/
    OnItemClickListener onItemClickListener= new OnItemClickListener(assignment.getId());
            vi.setOnClickListener(onItemClickListener);

        }
        return vi;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();


    }

    public void updateData(){
        values=new ArrayList<>(map.values());
        Comparator<Assignment> comparator= new Comparator<Assignment>() {
            @Override
            public int compare(Assignment lhs, Assignment rhs) {
                return lhs.getOrder()-rhs.getOrder();
            }
        };
        Collections.sort(values, comparator);
        notifyDataSetChanged();
    }



    public void updateData(Map<Integer,Assignment> newDataSet){
        map=newDataSet;
        updateData();
    }
    private class OnItemClickListener  implements View.OnClickListener {
        private int assId;



        OnItemClickListener(int assId){
            this.assId = assId;

        }

        @Override
        public void onClick(View arg0) {
            Assignment ass=map.get(assId);
            if(ass.getGroup()!=null){
                activity.startGroupPage(ass,-1,"",false);
            }
            else{
                activity.startGroupEditFragment(ass);
            }

        }


    }


}
