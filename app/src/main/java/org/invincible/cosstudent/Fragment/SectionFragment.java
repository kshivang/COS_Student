package org.invincible.cosstudent.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.invincible.cosstudent.OutletAdapter;
import org.invincible.cosstudent.R;
import org.invincible.cosstudent.misc.Outlet;
import org.invincible.cosstudent.misc.UserLocalStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shivang on 1/10/16.
 **/
public class SectionFragment extends Fragment {

    private static final String FLAG_SECTION = "section";

    public SectionFragment(){
    }

    public static SectionFragment newInstance(String section) {
        Bundle args = new Bundle();
        args.putString(FLAG_SECTION, section);
        SectionFragment fragment = new SectionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private RecyclerView mRecyclerView;
    private ProgressBar progressBar;
    private TextView tvLoading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.layout_section, container,false);
        mRecyclerView = (RecyclerView) root.findViewById(R.id.recyclerList);
        progressBar = (ProgressBar) root.findViewById(R.id.progress_bar);
        tvLoading = (TextView) root.findViewById(R.id.loading);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        UserLocalStore userLocalStore = new UserLocalStore(getContext());
        FirebaseDatabase.getInstance().getReference().child("campus")
                .child(userLocalStore.getCampusID()).child("outlets")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        progressBar.setVisibility(View.GONE);
                        if(dataSnapshot != null) {
                            List<Outlet> outlets = new ArrayList<>();
                            for(DataSnapshot data: dataSnapshot.getChildren()) {
                                if (data.child("type").getValue(String.class)
                                        .equals(getArguments().getString(FLAG_SECTION))){
                                    Outlet outlet = data.getValue(Outlet.class);
                                    outlet.setKey(data.getKey());
                                    outlets.add(outlet);
                                }
                            }
                            if (outlets.size() > 0) {
                                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                OutletAdapter outletAdapter = new OutletAdapter(getContext(), outlets);
                                mRecyclerView.setAdapter(outletAdapter);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        progressBar.setVisibility(View.GONE);
                        tvLoading.setVisibility(View.VISIBLE);
                        tvLoading.setText("Internet not working.");
                    }
                });

    }
}
