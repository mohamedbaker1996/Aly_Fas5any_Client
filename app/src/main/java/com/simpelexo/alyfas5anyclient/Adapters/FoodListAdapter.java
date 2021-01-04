package com.simpelexo.alyfas5anyclient.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.simpelexo.alyfas5anyclient.Callback.ItemClickListener;
import com.simpelexo.alyfas5anyclient.Database.CartDataSource;
import com.simpelexo.alyfas5anyclient.Database.CartDatabase;
import com.simpelexo.alyfas5anyclient.Database.CartItem;
import com.simpelexo.alyfas5anyclient.Database.LocalCartDataSource;
import com.simpelexo.alyfas5anyclient.EventBus.CounterCartEvent;
import com.simpelexo.alyfas5anyclient.EventBus.FoodItemClick;
import com.simpelexo.alyfas5anyclient.Model.FoodModel;
import com.simpelexo.alyfas5anyclient.R;
import com.simpelexo.alyfas5anyclient.Utiles.Common;
import com.simpelexo.alyfas5anyclient.ui.Activity.BaseActivity;
import com.simpelexo.alyfas5anyclient.ui.Activity.HomeCycleActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.CompletableObserver;
import io.reactivex.Scheduler;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class FoodListAdapter extends RecyclerView.Adapter<FoodListAdapter.MyViewHolder> {
    private Context context;
    private List <FoodModel> foodModelList;
    private CompositeDisposable compositeDisposable;
    private CartDataSource cartDataSource;

    public FoodListAdapter(Context context, List<FoodModel> foodModelList) {
        this.context = context;
        this.foodModelList = foodModelList;
        this.compositeDisposable = new CompositeDisposable();
        this.cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(context).cartDAO());
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context)
        .inflate(R.layout.layout_food_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (Common.currentLanguage =="En") {
            Glide.with(context).load(foodModelList.get(position).getImage()).into(holder.img_food_image);
            holder.txt_food_price.setText(new StringBuilder("EGP")
                    .append(foodModelList.get(position).getPrice()));
        }else if(Common.currentLanguage =="ar"){
            Glide.with(context).load(foodModelList.get(position).getImage()).into(holder.img_food_image);
            holder.txt_food_price.setText(new StringBuilder("ج.م")
                    .append(foodModelList.get(position).getPrice()));
        }
        holder.txt_food_name.setText(new StringBuilder("")
        .append(foodModelList.get(position).getName()));

        //Event
        holder.setListener((view, position1, isLongClick) -> {
            Common.selectedFood = foodModelList.get(position1);
            Common.selectedFood.setKey(String.valueOf(position1));
            EventBus.getDefault().postSticky(new FoodItemClick(true, foodModelList.get(position1)));
        });
        holder.img_quick_cart.setOnClickListener(v -> {
            CartItem cartItem = new CartItem();
            cartItem.setUid(Common.currentUser.getUid());
            cartItem.setUserPhone(Common.currentUser.getPhone());

            cartItem.setFoodId(foodModelList.get(position).getId());
             cartItem.setFoodName(foodModelList.get(position).getName());
            cartItem.setFoodImage(foodModelList.get(position).getImage());
            cartItem.setFoodPrice(Double.valueOf(String.valueOf(foodModelList.get(position).getPrice())));
            cartItem.setFoodQuantity((double) 1);
            cartItem.setFoodSize("Default");


          cartDataSource.getItemWithSizeInCart(Common.currentUser.getUid(),
                  cartItem.getFoodId(),
                  cartItem.getFoodSize())
                  .subscribeOn(Schedulers.io())
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(new SingleObserver<CartItem>() {
                      @Override
                      public void onSubscribe(Disposable d) {

                      }

                      @Override
                      public void onSuccess(CartItem cartItemFromDB) {
                          if (cartItemFromDB.equals(cartItem)) {
                              cartItemFromDB.setFoodSize(cartItem.getFoodSize());
                              cartItemFromDB.setFoodQuantity(cartItemFromDB.getFoodQuantity() + cartItem.getFoodQuantity());


                              cartDataSource.updateCartItems(cartItemFromDB)
                                      .subscribeOn(Schedulers.io())
                                      .observeOn(AndroidSchedulers.mainThread())
                                      .subscribe(new SingleObserver<Integer>() {
                                          @Override
                                          public void onSubscribe(Disposable d) {

                                          }

                                          @Override
                                          public void onSuccess(Integer integer) {
//                                              Snackbar snackBar = Snackbar.make(v.findViewById(android.R.id.content),
//                                                      R.string.update_cart_success , Snackbar.LENGTH_LONG);
//                                              snackBar.setBackgroundTint(Color.parseColor("#2c3e50"));
//                                              snackBar.show();
                                              Toast.makeText(context, R.string.update_cart_success, Toast.LENGTH_SHORT).show();
                                              EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                          }

                                          @Override
                                          public void onError(Throwable e) {
//                                              Snackbar snackBar = Snackbar.make(v.findViewById(android.R.id.content),
//                                                      "[UPDATE CART]"+e.getMessage() , Snackbar.LENGTH_LONG);
//                                              snackBar.setBackgroundTint(Color.parseColor("#2c3e50"));
//                                              snackBar.show();
                                              Toast.makeText(context,"[UPDATE CART]"+e.getMessage() , Toast.LENGTH_SHORT).show();


                                          }
                                      });
                          }
                          else {
                              // insert new item
                              compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartItem)
                                      .subscribeOn(Schedulers.io())
                                      .observeOn(AndroidSchedulers.mainThread())
                                      .subscribe(()->{
//                                          Snackbar snackBar = Snackbar.make(v.findViewById(android.R.id.content),
//                                                  R.string.add_to_cart_success , Snackbar.LENGTH_LONG);
//                                          snackBar.setBackgroundTint(Color.parseColor("#2c3e50"));
//                                          snackBar.show();
                                          Toast.makeText(context, R.string.add_to_cart_success, Toast.LENGTH_SHORT).show();
                                          EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                      },throwable -> {
                                          Toast.makeText(context, "[CART ERROR]"+ throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                      }));


                          }
                      }

                      @Override
                      public void onError(Throwable e) {
                          if (e.getMessage().contains("empty")) {
                              //Default is cart is empty this code will run
                              compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartItem)
                                      .subscribeOn(Schedulers.io())
                                      .observeOn(AndroidSchedulers.mainThread())
                                      .subscribe(()->{
//                                          Snackbar snackBar = Snackbar.make(v.findViewById(android.R.id.content),
//                                                  R.string.add_to_cart_success , Snackbar.LENGTH_LONG);
//                                          snackBar.setBackgroundTint(Color.parseColor("#2c3e50"));
//                                          snackBar.show();
                                          Toast.makeText(context, R.string.add_to_cart_success, Toast.LENGTH_SHORT).show();
                                          EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                      },throwable -> {
                                          Toast.makeText(context, "[CART ERROR]"+ throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                      }));

                          }

                      //    Toast.makeText(context, "[GET CART]"+e.getMessage(), Toast.LENGTH_SHORT).show();
                      }
                  });


        });
    }

    @Override
    public int getItemCount() {
        return foodModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Unbinder unbinder;
        @BindView(R.id.txt_food_name)
        TextView txt_food_name;
        @BindView(R.id.txt_food_price)
        TextView txt_food_price;
        @BindView(R.id.img_food_image)
        ImageView img_food_image;
        @BindView(R.id.img_fav)
        ImageView img_fav;
        @BindView(R.id.img_quick_cart)
        ImageView img_quick_cart;


        ItemClickListener listener;

        public void setListener(ItemClickListener listener) {
            this.listener = listener;
        }

        public MyViewHolder(@NonNull View itemView) {
           super(itemView);
           unbinder = ButterKnife.bind(this,itemView);
           itemView.setOnClickListener(this);
       }

        @Override
        public void onClick(View v) {
            listener.onClick(v,getAdapterPosition(),false);
        }
    }
}
