package org.invincible.cosstudent;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.invincible.cosstudent.misc.OrderModel;

import java.util.List;
import java.util.Locale;

/**
 * Created by kshivang on 02/10/16.
 */

public class PaymentAdapter  extends RecyclerView.Adapter<PaymentAdapter.CustomViewHolder> {
    private List<OrderModel> orderList;
    private Context mContext;

    public PaymentAdapter(Context context, List<OrderModel> orderList) {
        this.orderList = orderList;
        this.mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_payment, viewGroup, false);

        return new CustomViewHolder(view);
    }
    private String HrToAMPM(String Hr){
        Hr = Hr.substring(0, 2) + ":" + Hr.substring(2, 4);
        String AMPM;
        int temp=(((int)Hr.charAt(0)) - 48)*10 + (((int)Hr.charAt(1)) - 48);
        if(temp > 12){
            temp = temp%12 ;
            AMPM = temp + Hr.substring(2) + " PM";
        }else if(temp == 12){
            AMPM = temp + Hr.substring(2) + " PM";
        }else {
            AMPM = temp + Hr.substring(2) + " AM";
        }
        return AMPM;
    }
    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        OrderModel order = orderList.get(i);

        if(i == 0 || !orderList.get(i).getBillId().equals(orderList.get(i - 1).getBillId())) {
            customViewHolder.total.setVisibility(View.VISIBLE);
            customViewHolder.date.setText((order.getBillId().substring(0, 2) + "/"
                    + (order.getBillId().substring(2, 4)) + "/" + (order.getBillId().substring(4,6))));
            customViewHolder.Unsettled.setText(String.format(Locale.ENGLISH, "%s%d",
                    mContext.getString(R.string.rupee), order.getTotal_price()));
            //Handle click event on both title and image click
            customViewHolder.time.setText((HrToAMPM(order.getBillId().substring(6))));
        } else {
            customViewHolder.total.setVisibility(View.GONE);
            customViewHolder.item_name.setText(order.getName());
            customViewHolder.price.setText(String.valueOf(order.getPrice()));
            customViewHolder.quantity.setText(String.valueOf(order.getQty()));
        }
    }

    @Override
    public int getItemCount() {
        return (null != orderList ? orderList.size() : 0);
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView Unsettled;
        TextView date;
        TextView time;
        View total;
        TextView item_name;
        TextView price;
        TextView quantity;


        public CustomViewHolder(View view) {
            super(view);
            this.date = (TextView) view.findViewById(R.id.date_value);
            this.Unsettled = (TextView) view.findViewById(R.id.transaction_value);
            this.time = (TextView) view.findViewById(R.id.time);
            this.total = view.findViewById(R.id.total);
            this.item_name = (TextView) view.findViewById(R.id.item_name);
            this.price = (TextView) view.findViewById(R.id.price);
            this.quantity = (TextView) view.findViewById(R.id.quantity);
        }
    }
}
