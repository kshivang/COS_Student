package org.invincible.cosstudent.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.invincible.cosstudent.adapter.PaymentAdapter;
import org.invincible.cosstudent.R;
import org.invincible.cosstudent.misc.OrderModel;
import org.invincible.cosstudent.misc.Outlet;
import org.invincible.cosstudent.misc.UserLocalStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by kshivang on 02/10/16.
 */

public class HistoryScreen extends AppCompatActivity{

    private RecyclerView mRecyclerView;
    private ProgressBar progressBar;
    private TextView tvLoading;
    private TextView settledTextView;
    private TextView unsettledTextView;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_history);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        Intent fromIntent = getIntent();
        Outlet outlet = (Outlet) fromIntent.getSerializableExtra("outlet");
        tvLoading = (TextView) findViewById(R.id.transaction_loading);
        settledTextView = (TextView) findViewById(R.id.settled_value);
        unsettledTextView = (TextView) findViewById(R.id.unsettled_value);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        onPaymentHistory(outlet.getKey());
    }

    List<List<OrderModel>> orderModels = new ArrayList<>();

    private void onPaymentHistory(String outletId) {
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        UserLocalStore userLocalStore = new UserLocalStore(this);
        FirebaseDatabase.getInstance().getReference()
                .child("billing").child(outletId)
                .child(userLocalStore.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        progressBar.setVisibility(View.GONE);
                        tvLoading.setVisibility(View.GONE);
                        if(dataSnapshot != null) {
                            int i = 0;
                            for(DataSnapshot data: dataSnapshot.getChildren()) {
                                orderModels.add(new ArrayList<OrderModel>());
                                for(DataSnapshot order: data.child("bill").getChildren()) {
                                    OrderModel orderModel = order.getValue(OrderModel.class);
                                    orderModel.setKey(order.getKey());
                                    orderModel.setBillId(data.getKey());
                                    orderModel.setStatus(data
                                            .child("status").getValue(String.class));
                                    orderModel.setTotal_price(data
                                            .child("total_price").getValue(Integer.class));
                                    orderModels.get(i).add(orderModel);
                                }
                                i++;
                            }
                            onBillShow();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        progressBar.setVisibility(View.GONE);
                        tvLoading.setText("Check you internet connection");
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (actionBar != null) {
            actionBar.setTitle("Billing");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    long totalTransactionWorth = 0;
    long totalSettled = 0;
    long totalUnSettled = 0;

    private void onBillShow() {
       totalTransactionWorth = 0;
       totalSettled = 0;
       if (orderModels.size() > 0)  {
           List<OrderModel> orderModelListFlat = new ArrayList<>();
           for (int i = 0; i < orderModels.size(); i++) {
               if (orderModels.get(i).size() > 0) {
                   List<OrderModel> orderModelList = orderModels.get(i);
                   orderModelListFlat.addAll(orderModelList);
                   OrderModel firstOrder = orderModelList.get(0);
                   totalTransactionWorth = totalTransactionWorth + firstOrder.getTotal_price();
                   if (firstOrder.getStatus().equals("paid")) {
                       totalSettled = totalSettled + firstOrder.getTotal_price();
                   }
               }
           }

           PaymentAdapter paymentAdapter = new PaymentAdapter(this, orderModelListFlat);
           mRecyclerView.setAdapter(paymentAdapter);
           mRecyclerView.setVisibility(View.VISIBLE);
       }
        totalUnSettled = totalTransactionWorth - totalSettled;

        settledTextView.setText(String.format(Locale.ENGLISH,"%s%d",
                getString(R.string.rupee), totalSettled));
        unsettledTextView.setText(String.format(Locale.ENGLISH,"%s%d",
                getString(R.string.rupee), totalUnSettled));
    }
}
