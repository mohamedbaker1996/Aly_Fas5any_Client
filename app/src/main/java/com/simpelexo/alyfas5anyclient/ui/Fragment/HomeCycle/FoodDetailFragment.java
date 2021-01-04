package com.simpelexo.alyfas5anyclient.ui.Fragment.HomeCycle;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.andremion.counterfab.CounterFab;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.simpelexo.alyfas5anyclient.Database.CartDataSource;
import com.simpelexo.alyfas5anyclient.Database.CartDatabase;
import com.simpelexo.alyfas5anyclient.Database.CartItem;
import com.simpelexo.alyfas5anyclient.Database.LocalCartDataSource;
import com.simpelexo.alyfas5anyclient.EventBus.CounterCartEvent;
import com.simpelexo.alyfas5anyclient.EventBus.MenuItemBack;
import com.simpelexo.alyfas5anyclient.Model.Comment;
import com.simpelexo.alyfas5anyclient.Model.FoodModel;
import com.simpelexo.alyfas5anyclient.Model.SizeModel;
import com.simpelexo.alyfas5anyclient.R;
import com.simpelexo.alyfas5anyclient.Utiles.Common;
import com.simpelexo.alyfas5anyclient.Utiles.HelperMethod;
import com.simpelexo.alyfas5anyclient.ViewModel.FoodDetailViewModel;
import com.simpelexo.alyfas5anyclient.ui.Fragment.BaseFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class FoodDetailFragment extends BaseFragment {

    @BindView(R.id.rating_bar)
    RatingBar ratingBar;
    private Unbinder unbinder;
    private android.app.AlertDialog waitingDialog;

    private CartDataSource cartDataSource;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @BindView(R.id.img_food)
    ImageView imgFood;
    private FoodDetailViewModel foodDetailViewModel;
    @BindView(R.id.btn_cart)
    CounterFab btnCart;
    @BindView(R.id.btn_rating)
    FloatingActionButton btnRating;
    @BindView(R.id.tv_food_name)
    TextView tvFoodName;
    @BindView(R.id.tv_food_price)
    TextView tvFoodPrice;
    @BindView(R.id.layout_price)
    LinearLayout layoutPrice;
    @BindView(R.id.edt_quantity)
    EditText edtQuantity;
    @BindView(R.id.tv_food_descriptionde)
    TextView tvFoodDescriptionde;
    @BindView(R.id.rdi_group_order_by)
    RadioGroup rdi_group_order_by;

    @OnClick(R.id.btn_rating)
    void onRatingButtonClicked() {
        showDialogeRating();
    }

    private void showDialogeRating() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle(R.string.rating_food);
        alertDialog.setMessage(R.string.enter_review);
        // final EditText edtAddress = new EditText(Cart.this);
     //   LayoutInflater inflater = this.getLayoutInflater();
     //   View itemView = inflater.inflate(R.layout.layout_rating, null);
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.layout_rating,null);
        RatingBar ratingBar = (RatingBar) itemView.findViewById(R.id.rating_bar);
        EditText edt_comment = (EditText) itemView.findViewById(R.id.edt_comment);
        alertDialog.setView(itemView);
        alertDialog.setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss());
        alertDialog.setPositiveButton("OK", (dialog, which) -> {
            Comment commentModel = new Comment();
            commentModel.setName(Common.currentUser.getName());
            commentModel.setUid(Common.currentUser.getUid());
            commentModel.setComment(edt_comment.getText().toString());
            commentModel.setRatingValue(ratingBar.getRating());
            Map<String, Object> serverTimeStamp = new HashMap<>();
            serverTimeStamp.put("time Stamp", ServerValue.TIMESTAMP);
            commentModel.setCommentTimeStamp(serverTimeStamp);
            foodDetailViewModel.setCommentModel(commentModel);
        });

        alertDialog.show();
    }

    @OnClick(R.id.btn_cart)
    void onCartItemAdded(){
        CartItem cartItem = new CartItem();
        cartItem.setUid(Common.currentUser.getUid());
        cartItem.setUserPhone(Common.currentUser.getPhone());

        cartItem.setFoodId(Common.selectedFood.getId());
      //  cartItem.setFoodId(Common.selectedFood.getId());
      cartItem.setFoodName(Common.selectedFood.getName());
     cartItem.setFoodImage(Common.selectedFood.getImage());
        if (Common.selectedFood.getUserSelectedSize().getPrice()==0) {
            cartItem.setFoodPrice(0.0);
            cartItem.setFoodBasePrice((double) Common.selectedFood.getPrice());
        }else {
        cartItem.setFoodPrice(Double.valueOf(String.valueOf(Common.selectedFood.getPrice())));
        }
        cartItem.setFoodQuantity(Double.parseDouble(edtQuantity.getText().toString()));
    // Double  extraSize = Common.calculateSizePrice(Common.selectedFood.getUserSelectedSize());

        if (Common.selectedFood.getUserSelectedSize() != null) {
            cartItem.setFoodSize(new Gson().toJson(Common.selectedFood.getUserSelectedSize()));
        }else {
            cartItem.setFoodSize("Default");
        }
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
                                            Snackbar snackBar = Snackbar.make(getActivity().findViewById(android.R.id.content),
                                                    R.string.update_order_success, Snackbar.LENGTH_LONG);
                                            snackBar.setBackgroundTint(Color.parseColor("#2c3e50"));
                                            snackBar.show();
                                            EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                        }


                                        @Override
                                        public void onError(Throwable e) {
                                            Snackbar snackBar = Snackbar.make(getActivity().findViewById(android.R.id.content),
                                                    "[UPDATE CART]"+e.getMessage(), Snackbar.LENGTH_LONG);
                                            snackBar.setBackgroundTint(Color.parseColor("#2c3e50"));
                                            snackBar.show();
                                        }
                                    });
                        }
                        else {
                            compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartItem)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(()->{
                                        Snackbar snackBar = Snackbar.make(getActivity().findViewById(android.R.id.content),
                                                R.string.add_to_cart_success, Snackbar.LENGTH_LONG);
                                        snackBar.setBackgroundTint(Color.parseColor("#2c3e50"));
                                        snackBar.show();
                                        EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                    },throwable -> {
                                        Snackbar snackBar = Snackbar.make(getActivity().findViewById(android.R.id.content),
                                                "[CART ERROR]"+ throwable.getMessage(), Snackbar.LENGTH_LONG);
                                        snackBar.setBackgroundTint(Color.parseColor("#2c3e50"));
                                        snackBar.show();
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
                                        Snackbar snackBar = Snackbar.make(getActivity().findViewById(android.R.id.content),
                                                R.string.add_to_cart_success, Snackbar.LENGTH_LONG);
                                        snackBar.setBackgroundTint(Color.parseColor("#2c3e50"));
                                        snackBar.show();
                                        EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                    },throwable -> {
                                        Snackbar snackBar = Snackbar.make(getActivity().findViewById(android.R.id.content),
                                                "[CART ERROR]"+ throwable.getMessage(), Snackbar.LENGTH_LONG);
                                        snackBar.setBackgroundTint(Color.parseColor("#2c3e50"));
                                        snackBar.show();
                                    }));

                        }
                    else

                        Toast.makeText(getContext(), "[GET CART]"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


    public FoodDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setUpActivity();
        foodDetailViewModel = ViewModelProviders.of(this).get(FoodDetailViewModel.class);


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_food_detail, container, false);
        //  setUpActivity();
        unbinder = ButterKnife.bind(this, view);
        //init view
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(getContext()).cartDAO());


//        foodDetailViewModel.getMutableLiveDataFood().observe(this, this::displayInfo);
//        foodDetailViewModel.getMutableLiveDataComment().observe(this, this::submitRatingToFireBase);
        foodDetailViewModel.getMutableLiveDataFood().observe(this, foodModel -> {
            rdi_group_order_by.removeAllViews();
            displayInfo(foodModel);
        });
        foodDetailViewModel.getMutableLiveDataComment().observe(this, comment -> {
            submitRatingToFireBase(comment);
        });

        edtQuantity.setText("1");
        edtQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    calculateToatalPrice();
                } catch (Exception e) {
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return view;
    }


    private void submitRatingToFireBase(Comment comment) {
        HelperMethod.showProgressDialog(getActivity(), "Pleas Wait", false);

        try {
            FirebaseDatabase.getInstance()
                    .getReference(Common.COMMENT_REF)
                    .child(Common.selectedFood.getId())
                    .push()
                    .setValue(comment)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            addRatingToFood(comment.getRatingValue());
                        }
                        HelperMethod.dismissProgressDialog();

                    });
        } catch (Exception e) {
        }
    }

    private void addRatingToFood(float ratingValue) {
        try {


            FirebaseDatabase.getInstance()
                    .getReference(Common.CATEGORY_REF)
                    .child(Common.categorySelected.getMenu_id())
                    .child("foods")
                    .child(Common.selectedFood.getKey())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                FoodModel foodModel = snapshot.getValue(FoodModel.class);
                                assert foodModel != null;
                                foodModel.setKey(Common.selectedFood.getKey());
                                //Apply Rating
                                if (foodModel.getRatingValue() == null)
                                    foodModel.setRatingValue(0d);
                                if (foodModel.getRatingCount() == null)

                                    foodModel.setRatingCount(0l);
                                //  foodModel.getRatingCount(0l);


                                double sumRating = foodModel.getRatingValue() + ratingValue;
                                long ratingCount = foodModel.getRatingCount() + 1;
                                double result = sumRating / ratingCount;

                                Map<String, Object> updateData = new HashMap<>();
                                updateData.put("ratingValue", result);
                                updateData.put("ratingCount", ratingCount);
// update Data in variables
                                foodModel.setRatingValue(result);
                                foodModel.setRatingCount(ratingCount);

                                snapshot.getRef().updateChildren(updateData)
                                        .addOnCompleteListener(task -> {
                                            HelperMethod.dismissProgressDialog();
                                            if (task.isSuccessful()) {
                                                Snackbar snackBar = Snackbar.make(getActivity().findViewById(android.R.id.content),
                                                        R.string.thank_you, Snackbar.LENGTH_LONG);
                                                snackBar.setBackgroundTint(Color.parseColor("#2c3e50"));
                                                snackBar.show();
                                                Common.selectedFood = foodModel;
                                                foodDetailViewModel.setFoodModel(foodModel);
                                            }
                                        });

                            } else
                                HelperMethod.dismissProgressDialog();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            HelperMethod.dismissProgressDialog();
                            Snackbar snackBar = Snackbar.make(getActivity().findViewById(android.R.id.content),
                                    "" + error.getMessage(), Snackbar.LENGTH_LONG);
                            snackBar.setBackgroundTint(Color.parseColor("#2c3e50"));
                            snackBar.show();
                        }
                    });
        } catch (Exception e) {
        }
    }

    private void displayInfo(FoodModel foodModel) {
        Glide.with(Objects.requireNonNull(getActivity())).load(foodModel.getImage()).into(imgFood);
        tvFoodName.setText(new StringBuilder(foodModel.getName()));
        tvFoodDescriptionde.setText(new StringBuilder(foodModel.getDescription()));
        tvFoodPrice.setText(String.valueOf(foodModel.getPrice()));
        if (foodModel.getRatingValue()!=null) {
            ratingBar.setRating(foodModel.getRatingValue().floatValue());
        }
        ((AppCompatActivity) getActivity())
                .getSupportActionBar()
                .setTitle(Common.selectedFood.getName());

        //Size
        for (SizeModel sizeModel : Common.selectedFood.getSize()) {
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked && edtQuantity != null) {
                    Common.selectedFood.setUserSelectedSize(sizeModel);
                    calculateToatalPrice(); // update price

                }
            });
//            LinearLayout .LayoutParams params = new LinearLayout.LayoutParams(0,
//                    LinearLayout.LayoutParams.WRAP_CONTENT,1.0f);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, .7f);
            radioButton.setLayoutParams(params);
            // radioButton.setPadding(4,4,4,4);

//            radioButton.setText(sizeModel.getName());
            radioButton.setText(Common.convertSize(sizeModel.getName()));
            radioButton.setTag(sizeModel.getPrice());

            rdi_group_order_by.addView(radioButton);

        }
        if (rdi_group_order_by.getChildCount()> 0) {
            RadioButton radioButton = (RadioButton)rdi_group_order_by.getChildAt(0);
            radioButton.setChecked(true);
        }
        calculateToatalPrice();

    }

    private void calculateToatalPrice() {
        double totalPrice = Double.parseDouble(String.valueOf(Common.selectedFood.getPrice())),displayPrice=0.0;
        //size
       // totalPrice += Double.parseDouble(String.valueOf(Common.selectedFood.getUserSelectedSize().getPrice()));
        if (Common.selectedFood.getUserSelectedSize() != null)
        totalPrice *= Double.parseDouble(String.valueOf(Common.selectedFood.getUserSelectedSize().getPrice()));


            displayPrice = totalPrice * (Double.parseDouble(edtQuantity.getText().toString()));

        displayPrice = Math.round(displayPrice*100.0/100.0);

        tvFoodPrice.setText(new StringBuilder("").append(Common.formatPrice(displayPrice)).toString());
    }

    @Override
    public void onBack() {
        super.onBack();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().postSticky(new MenuItemBack());
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }
}
