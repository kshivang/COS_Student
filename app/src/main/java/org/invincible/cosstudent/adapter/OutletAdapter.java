package org.invincible.cosstudent.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.invincible.cosstudent.R;
import org.invincible.cosstudent.activity.HistoryActivity;
import org.invincible.cosstudent.activity.OrderActivity;
import org.invincible.cosstudent.misc.Outlet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kshivang on 02/10/16.
 *
 */

public class OutletAdapter extends RecyclerView.Adapter<OutletAdapter.CustomViewHolder> {
    private List<Outlet> outlets = new ArrayList<>();
    private Context mContext;

    public OutletAdapter(Context context, List<Outlet> outlet) {
        this.outlets = outlet;
        this.mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_outlet,
                viewGroup, false);

        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder customViewHolder, int i) {
        final Outlet outlet = outlets.get(i);
        customViewHolder.tvName.setText(outlet.getName());
        customViewHolder.tvLocation.setText(outlet.getLocation());

        customViewHolder.btOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext,
                        OrderActivity.class).putExtra("outlet", outlet));
            }
        });
        customViewHolder.btHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext,
                        HistoryActivity.class).putExtra("outlet", outlet));
            }
        });

        Picasso.with(mContext).load(outlet.getSrc())
                .error(R.drawable.cash)
                .placeholder(R.drawable.cash)
                .into(customViewHolder.ivOutletImage);
    }

    @Override
    public int getItemCount() {
        return (null != outlets ? outlets.size() : 0);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvLocation;
        ImageView ivOutletImage;
        Button btOrder, btHistory;

        CustomViewHolder(View view) {
            super(view);

            tvName = (TextView) view.findViewById(R.id.name);
            tvLocation = (TextView) view.findViewById(R.id.location);
            ivOutletImage = (ImageView) view.findViewById(R.id.outlet_image);
            btOrder = (Button) view.findViewById(R.id.order);
            btHistory = (Button) view.findViewById(R.id.history);
        }
    }
}