package com.simpelexo.alyfas5anyclient.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.simpelexo.alyfas5anyclient.Callback.ItemClickListener;
import com.simpelexo.alyfas5anyclient.Database.CartItem;
import com.simpelexo.alyfas5anyclient.Model.OrderMV;
import com.simpelexo.alyfas5anyclient.R;
import com.simpelexo.alyfas5anyclient.Utiles.Common;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {
    private Context context;
    private List<OrderMV> orderList;
    private Calendar calendar;
    private SimpleDateFormat simpleDateFormat;

    public OrdersAdapter(Context context, List<OrderMV> orderList) {
        this.context = context;
        this.orderList = orderList;
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_order_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(orderList.get(position).getCartItemList().get(0).getFoodImage()).into(holder.img_order);
        calendar.setTimeInMillis(orderList.get(position).getCreateDate());
        Date date = new Date(orderList.get(position).getCreateDate());
        holder.txt_order_date.setText(new StringBuilder(Common.getDateOfWeek(calendar.get(Calendar.DAY_OF_WEEK)))
        .append(" ")
         .append(simpleDateFormat.format(date)));
        holder.txt_order_number.setText(new StringBuilder("Order # ").append(orderList.get(position).getOrderNumber()));
        holder.txt_order_comment.setText(new StringBuilder("Comment: ").append(orderList.get(position).getComment()));
        holder.txt_order_status.setText(new StringBuilder("Status: ").append(Common.convertCodeToStatus(orderList.get(position).getOrderStatus())));

        holder.setItemClickListener((view, position1, isLongClick) -> {
            showDialog(orderList.get(position1).getCartItemList());
        });
    }

    private void showDialog(List<CartItem> cartItemList) {
        View layout_dialog = LayoutInflater.from(context).inflate(R.layout.layout_dialoge_order_detail,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(layout_dialog);
        Button btn_ok =(Button)layout_dialog.findViewById(R.id.btn_ok);
        RecyclerView rv_order_detail = (RecyclerView)layout_dialog.findViewById(R.id.rv_order_detail);
        rv_order_detail.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        rv_order_detail.setLayoutManager(layoutManager);
        rv_order_detail.addItemDecoration(new DividerItemDecoration(context,layoutManager.getOrientation()));
        OrderDetailAdapter orderDetailAdapter = new OrderDetailAdapter(context,cartItemList);
        rv_order_detail.setAdapter(orderDetailAdapter);
        //show
        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);
        btn_ok.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.txt_order_status)
        TextView txt_order_status;
        @BindView(R.id.txt_order_comment)
        TextView txt_order_comment;
        @BindView(R.id.txt_order_number)
        TextView txt_order_number;
        @BindView(R.id.txt_order_date)
        TextView txt_order_date;
        @BindView(R.id.img_order)
        ImageView img_order;

        Unbinder unbinder;
        ItemClickListener itemClickListener;

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v,getAdapterPosition(),false);
        }
    }
}
