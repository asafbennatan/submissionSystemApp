package bgu.ac.il.submissionsystem.Controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import bgu.ac.il.submissionsystem.R;
import bgu.ac.il.submissionsystem.Utils.Constants;
import bgu.ac.il.submissionsystem.model.Assignment;
import bgu.ac.il.submissionsystem.model.Submission;
import bgu.ac.il.submissionsystem.view.MainActivity;

/**
 * Created by Asaf on 15/01/2016.
 */
public class SubmissionListAdapter extends BaseAdapter {
    private Map<Integer,Submission> map;
    private ArrayList<Submission> values;
    private MainActivity activity;
    private LayoutInflater inflater;

    public SubmissionListAdapter(MainActivity activity, Map<Integer, Submission> map) {
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
        public TextView date;



    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.submission_list_item, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ViewHolder();
            holder.name = (TextView) vi.findViewById(R.id.submissionName);
            holder.name.setFocusable(false);
            holder.name.setClickable(false);
            holder.date=(TextView) vi.findViewById(R.id.submissionDate);
            holder.date.setFocusable(false);
            holder.date.setClickable(false);

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
           Submission sub=values.get(position);

            holder.name.setText( sub.getName() );
            holder.date.setText(Constants.formatDate(sub.getDate()));


            /******** Set Item Click Listner for LayoutInflater for each row *******/
    OnItemClickListener onItemClickListener= new OnItemClickListener(sub.getId());
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
        Comparator<Submission> comparator= new Comparator<Submission>() {
            @Override
            public int compare(Submission lhs, Submission rhs) {
                return lhs.getDate().compareTo(rhs.getDate());
            }
        };
        Collections.sort(values, comparator);
        notifyDataSetChanged();
    }



    public void updateData(Map<Integer,Submission> newDataSet){
        map=newDataSet;
        updateData();
    }
    private class OnItemClickListener  implements View.OnClickListener {
        private int subId;



        OnItemClickListener(int assId){
            this.subId = assId;

        }

        @Override
        public void onClick(View arg0) {
            Submission sub=map.get(subId);
          activity.downloadSubmittedWork(sub);


        }


    }


}
