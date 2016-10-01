package org.invincible.cosstudent.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.invincible.cosstudent.R;

/**
 * Created by Shivang on 1/10/16.
 **/
public class SectionFragment extends Fragment {

    public SectionFragment(){
    }

    public static SectionFragment newInstance(String subject) {
        Bundle args = new Bundle();
        args.putString("subject", subject);
        SectionFragment fragment = new SectionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_section, container,false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}
