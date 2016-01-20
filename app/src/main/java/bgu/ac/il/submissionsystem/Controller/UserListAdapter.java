package bgu.ac.il.submissionsystem.Controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import bgu.ac.il.submissionsystem.R;
import bgu.ac.il.submissionsystem.model.Group;
import bgu.ac.il.submissionsystem.model.User;
import bgu.ac.il.submissionsystem.view.MainActivity;

/**
 * Created by Asaf on 15/01/2016.
 */
public class UserListAdapter extends BaseAdapter {
    private Map<Integer,User> map;
    private ArrayList<User> values;
    private MainActivity activity;
    private LayoutInflater inflater;
    private Group group;

    public UserListAdapter(MainActivity activity, Map<Integer, User> map,Group group) {
        this.map = map;
        if(this.map==null){
            this.map= new HashMap<>();
        }
        values=new ArrayList<>(this.map.values());
        this.activity=activity;
        this.group=group;
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

        public CheckBox nameAndMark;


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.user_list_item, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ViewHolder();
            holder.nameAndMark = (CheckBox) vi.findViewById(R.id.userSelect);

            /************  Set holder with LayoutInflater ************/
            vi.setTag( holder );
        }
        else{
            holder=(ViewHolder)vi.getTag();
        }

        //holder.text.setTextColor(Color.BLACK);
        if(map.size()<=0)
        {
            holder.nameAndMark.setText("No Data");


        }
        else
        {
            User user=values.get(position);
            boolean checked=false;
            if(group!=null&&group.getUsers()!=null){
                checked=group.getUsers().containsKey(user.getId());
            }
            holder.nameAndMark.setEnabled(false);
            holder.nameAndMark.setText(user.getName());
            holder.nameAndMark.setChecked(checked);
            holder.nameAndMark.setFocusable(false);
            holder.nameAndMark.setClickable(false);



            /******** Set Item Click Listner for LayoutInflater for each row *******/
    OnItemClickListener onItemClickListener= new OnItemClickListener(user.getId());
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
        Comparator<User> comparator= new Comparator<User>() {
            @Override
            public int compare(User lhs, User rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        };
        Collections.sort(values, comparator);
        notifyDataSetChanged();
    }



    public void updateData(Map<Integer,User> newDataSet,Group group){
        map=newDataSet;
        this.group=group;
        updateData();
    }
    private class OnItemClickListener  implements View.OnClickListener {
        private int userId;



        OnItemClickListener(int userId){
            this.userId = userId;

        }

        @Override
        public void onClick(View arg0) {
            User user=map.get(userId);
                activity.promptUserId(user, group);

        }


    }


}
