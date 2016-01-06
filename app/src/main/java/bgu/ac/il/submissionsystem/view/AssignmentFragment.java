package bgu.ac.il.submissionsystem.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bgu.ac.il.submissionsystem.R;

/**
 * Created by Asaf on 06/01/2016.
 */
public class AssignmentFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.assignment_fragment, container, false);
    }
}