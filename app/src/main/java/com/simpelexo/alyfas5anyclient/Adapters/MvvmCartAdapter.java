package com.simpelexo.alyfas5anyclient.Adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.simpelexo.alyfas5anyclient.Database.CartItem;
import com.simpelexo.alyfas5anyclient.EventBus.UpdateItemInCart;
import com.simpelexo.alyfas5anyclient.R;
import com.simpelexo.alyfas5anyclient.Utiles.Common;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MvvmCartAdapter extends RecyclerView.Adapter<MvvmCartAdapter.MyViewHolder> {
        Context context;
        List<CartItem> cartItemList;

    private Timer timer = new Timer();
    private final long DELAY = 1000; // in ms

    public MvvmCartAdapter(Context context, List<CartItem> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_cart_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(cartItemList.get(position).getFoodImage())
                .into(holder.img_cart);
        holder.txt_food_name.setText(new StringBuilder(cartItemList.get(position).getFoodName()));
//        Double extraSize = Common.calculateSizePrice(Common.selectedFood.getUserSelectedSize());
        if (cartItemList.get(position).getFoodSize().contains("Large") ) {
            holder.txt_food_price.setText(new StringBuilder("")
                    .append(context.getString(R.string.by_quantity_call)));

        } else
        {
            holder.txt_food_price.setText(new StringBuilder("")
                    .append(cartItemList.get(position).getFoodPrice() ));
    }
        holder.edt_quantity.setText(String.valueOf(cartItemList.get(position).getFoodQuantity()));

        //Event


        holder.edt_quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(timer != null)
                    timer.cancel();

            }

            @Override
            public void afterTextChanged(Editable s) {
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // TODO: do what you need here (refresh list)
                        // you will probably need to use
                        // runOnUiThread(Runnable action) for some specific
                        // actions
                    //    serviceConnector.getStopPoints(s.toString());
                        try {

                               cartItemList.get(position).setFoodQuantity(Double.parseDouble(String.valueOf(s)));
                               EventBus.getDefault().postSticky(new UpdateItemInCart(cartItemList.get(position)));
                           }catch (Exception e){}

                    }

                }, DELAY);
            }
        });


    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public CartItem getItemAtPosition(int pos) {
        return cartItemList.get(pos);

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private Unbinder unbinder;
        @BindView(R.id.img_cart)
        ImageView img_cart;
        @BindView(R.id.txt_food_price)
        TextView txt_food_price;
        @BindView(R.id.txt_food_name)
        TextView txt_food_name;
        @BindView(R.id.edt_quantity)
        EditText edt_quantity;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this,itemView);
        }
    }
}
