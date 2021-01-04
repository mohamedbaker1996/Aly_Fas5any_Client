package com.simpelexo.alyfas5anyclient.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.simpelexo.alyfas5anyclient.Database.CartItem;
import com.simpelexo.alyfas5anyclient.R;


import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.ViewHolder> {
    Context context;
    List<CartItem> cartItemList;
    Gson gson;

    public OrderDetailAdapter(Context context, List<CartItem> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
        gson = new Gson();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_order_detail_item,parent,false));

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(cartItemList.get(position).getFoodImage()).into(holder.img_food_image);
        holder.txt_food_name.setText(new StringBuilder(" اسم المنتج :").append(cartItemList.get(position).getFoodName()));
        holder.txt_food_quantity.setText(new StringBuilder(" الكميه  :").append(cartItemList.get(position).getFoodQuantity()));
        holder.txt_food_price.setText(new StringBuilder(" سعر الكيلو :").append(cartItemList.get(position).getFoodPrice()));
        if (cartItemList.get(position).getFoodSize().contains("Large")) {
            holder.txt_size.setText(new StringBuilder(" نوع الطلب :").append("بالعدد"));

        }else {
        holder.txt_size.setText(new StringBuilder(" نوع الطلب :").append(" بالوزن "));
    }
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txt_food_name)
        TextView txt_food_name;
        @BindView(R.id.txt_food_quantity)
        TextView txt_food_quantity;
        @BindView(R.id.txt_food_price)
        TextView txt_food_price;
        @BindView(R.id.txt_size)
        TextView txt_size;
        @BindView(R.id.img_food_image)
        ImageView img_food_image;

        private Unbinder unbinder;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this,itemView);
        }
    }
}
