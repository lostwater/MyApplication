package com.example.turtlejk.myapplication.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.turtlejk.myapplication.R;


public class StepFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflateAndSetupView(inflater, container, savedInstanceState, R.layout.step_fragment);
    }

    private View inflateAndSetupView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState, int layoutResourceId) {
        View layout = inflater.inflate(layoutResourceId, container, false);
        LinearLayout totreepicture = (LinearLayout) layout.findViewById(R.id.totreepicture);
        totreepicture.setOnClickListener(totreeOnClick);
        return layout;
    }

    View.OnClickListener totreeOnClick = new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            FragmentsActivity factivity = (FragmentsActivity) getActivity();
            factivity.vpager.setCurrentItem(factivity.PAGE_TREE);
        }
    };
}
