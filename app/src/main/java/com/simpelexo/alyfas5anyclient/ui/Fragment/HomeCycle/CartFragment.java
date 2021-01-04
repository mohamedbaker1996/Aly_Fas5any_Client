package com.simpelexo.alyfas5anyclient.ui.Fragment.HomeCycle;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.simpelexo.alyfas5anyclient.Adapters.MvvmCartAdapter;
import com.simpelexo.alyfas5anyclient.Callback.ILoadTimeFromFirebaseListener;
import com.simpelexo.alyfas5anyclient.Database.CartDataSource;
import com.simpelexo.alyfas5anyclient.Database.CartDatabase;
import com.simpelexo.alyfas5anyclient.Database.CartItem;
import com.simpelexo.alyfas5anyclient.Database.LocalCartDataSource;
import com.simpelexo.alyfas5anyclient.EventBus.CounterCartEvent;
import com.simpelexo.alyfas5anyclient.EventBus.HideFABCart;
import com.simpelexo.alyfas5anyclient.EventBus.MenuItemBack;
import com.simpelexo.alyfas5anyclient.EventBus.UpdateItemInCart;
import com.simpelexo.alyfas5anyclient.Model.FCMResponse;
import com.simpelexo.alyfas5anyclient.Model.FCMSendData;
import com.simpelexo.alyfas5anyclient.Model.OrderMV;
import com.simpelexo.alyfas5anyclient.R;
import com.simpelexo.alyfas5anyclient.Service.IFCMService;
import com.simpelexo.alyfas5anyclient.Service.RetrofitFCMClient;
import com.simpelexo.alyfas5anyclient.Utiles.Common;
import com.simpelexo.alyfas5anyclient.Utiles.MySwipeHelper;
import com.simpelexo.alyfas5anyclient.ViewModel.CartViewModel;
import com.simpelexo.alyfas5anyclient.ui.Fragment.BaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static android.content.ContentValues.TAG;


public class CartFragment extends BaseFragment implements ILoadTimeFromFirebaseListener {

    @BindView(R.id.recycler_cart)
    RecyclerView recyclerCart;
    @BindView(R.id.txt_total_price)
    TextView txtTotalPrice;
    @BindView(R.id.group_place_holder)
    CardView groupPlaceHolder;
    @BindView(R.id.txt_empty_cart)
    TextView txtEmptyCart;
    @BindView(R.id.btn_place_order)
    MaterialButton btnPlaceOrder;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private Unbinder unbinder;
    private Parcelable recycleViewState;
    private CartDataSource cartDataSource;
    private CartViewModel cartViewModel;
    private MvvmCartAdapter adapter;
    IFCMService ifcmService;
    ILoadTimeFromFirebaseListener listener;

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        cartViewModel =
                ViewModelProviders.of(this).get(CartViewModel.class);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        setUpActivity();
        ifcmService = RetrofitFCMClient.getInstance().create(IFCMService.class);

        listener = this;// time from firebase

        cartViewModel.initCartDataSource(getContext());
        cartViewModel.getMutableLiveDataCartItems().observe(this, cartItems -> {
            if (cartItems == null || cartItems.isEmpty()) {
                recyclerCart.setVisibility(View.GONE);
                groupPlaceHolder.setVisibility(View.GONE);
                txtEmptyCart.setVisibility(View.VISIBLE);
            } else {
                recyclerCart.setVisibility(View.VISIBLE);
                groupPlaceHolder.setVisibility(View.VISIBLE);
                txtEmptyCart.setVisibility(View.GONE);

                adapter = new MvvmCartAdapter(getContext(), cartItems);
                recyclerCart.setAdapter(adapter);

            }
        });
        unbinder = ButterKnife.bind(this, view);
        initViews();
        return view;
    }

    private void initViews() {
        setHasOptionsMenu(true);
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(getContext()).cartDAO());
        //Hide Fab Button
        EventBus.getDefault().postSticky(new HideFABCart(true));
        recyclerCart.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerCart.setLayoutManager(layoutManager);
        recyclerCart.addItemDecoration(new DividerItemDecoration(getContext(), layoutManager.getOrientation()));
        // calculateTotalPrice();
        sumAllItemsInCart();

        MySwipeHelper mySwipeHelper = new MySwipeHelper(getContext(), recyclerCart, 200) {
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buf) {
                buf.add(new MyButton(getContext(), "Delete", 30, 0, Color.parseColor("#FF3c30"),
                        pos -> {
                            CartItem cartItem = adapter.getItemAtPosition(pos);
                            cartDataSource.deleteCartItem(cartItem)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new SingleObserver<Integer>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {

                                        }

                                        @Override
                                        public void onSuccess(Integer integer) {
                                            adapter.notifyItemRemoved(pos);
                                            EventBus.getDefault().postSticky(new CounterCartEvent(true)); // Update FAB
                                            //  calculateTotalPrice();
                                            sumAllItemsInCart();
                                            Toast.makeText(getContext(), R.string.delete_item_from_cart, Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        }));
            }
        };
        sumAllItemsInCart();
    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle(R.string.one_more_step);
        alertDialog.setMessage(R.string.enter_address);
        // final EditText edtAddress = new EditText(Cart.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View order_adress_comment = inflater.inflate(R.layout.order_adress_comment, null);

        final MaterialEditText edtAddress = (MaterialEditText) order_adress_comment.findViewById(R.id.edt_address);
        final MaterialEditText edtComment = (MaterialEditText) order_adress_comment.findViewById(R.id.edt_comment);
        RadioButton rdi_home = (RadioButton) order_adress_comment.findViewById(R.id.rdi_home_address);
        RadioButton rdi_other_address = (RadioButton) order_adress_comment.findViewById(R.id.rdi_other_address);

        //Data
        edtAddress.setText(Common.currentUser.getAddress());
        rdi_home.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                edtAddress.setText(Common.currentUser.getAddress());

            }
        });
        rdi_other_address.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                edtAddress.setText("");
                edtAddress.setHint(R.string.enter_your_address);
            }
        });

        alertDialog.setView(order_adress_comment);
        alertDialog.setIcon(R.drawable.ic_cart);

        alertDialog.setPositiveButton("YES", (dialog, which) -> {
            PaymentCOD(edtAddress.getText().toString(),edtComment.getText().toString());
        });
        alertDialog.setNegativeButton("NO", (dialog, which) -> dialog.dismiss());
        alertDialog.show();

    }

    private void PaymentCOD(String address , String comment) {
    compositeDisposable.add(cartDataSource.getAllCart(Common.currentUser.getUid())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
            .subscribe(cartItems -> {
            //on all cart item ready get total price
                cartDataSource.sumPriceInCart(Common.currentUser.getUid())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<Double>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onSuccess(Double totalPrice) {
                            double finalPrice = totalPrice; // for discount later
                                OrderMV ordermv = new OrderMV();
                                ordermv.setUserId(Common.currentUser.getUid());
                                ordermv.setUserName(Common.currentUser.getName());
                                ordermv.setUserPhone(Common.currentUser.getPhone());
                                ordermv.setShippingAddress(address);
                                ordermv.setComment(comment);


                                ordermv.setCartItemList(cartItems);
                                ordermv.setTotalPayment(totalPrice);
                                ordermv.setDiscount(0);

                                //submit to firebase
                                syncLocalTimeWithGlobalTime(ordermv);

                            }

                            @Override
                            public void onError(Throwable e) {
                                if (!e.getMessage().contains("Query returned empty result set")) {

                                    Toast.makeText(getContext(), R.string.order_placed, Toast.LENGTH_SHORT).show();
                                }                            }
                        });
            }, throwable -> {
                Toast.makeText(getContext(), ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();
            })
    );
    }

    private void syncLocalTimeWithGlobalTime(OrderMV ordermv) {
    final DatabaseReference offsetRef = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");
    offsetRef.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            long offset= snapshot.getValue(Long.class);
            long estimateTimeServerTimeMs = System.currentTimeMillis()+offset;
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
            Date resultDate = new Date(estimateTimeServerTimeMs);
            Log.d(TAG, "onDataChange: "+sdf.format(resultDate));
            listener.onLoadTimeSuccess(ordermv,estimateTimeServerTimeMs);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
        listener.onLoadTimeFailed(error.getMessage());
        }
    });
    }

    private void submitOrderToFirebase(OrderMV ordermv) {
        FirebaseDatabase.getInstance()
                .getReference(Common.ORDER_REF)
                .child(Common.createOrderNumber())
                .setValue(ordermv)
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }).addOnCompleteListener(task -> {
                   cartDataSource.cleanCart(Common.currentUser.getUid())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new SingleObserver<Integer>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @SuppressLint("ResourceAsColor")
                                    @Override
                                    public void onSuccess(Integer integer) {
                                        Map<String,String> notifData= new HashMap<>();
                                        notifData.put(Common.NOTI_TITLE,"New Order");
                                        notifData.put(Common.NOTI_CONTENT,"You have new Order From"+Common.currentUser.getPhone());
                                        FCMSendData sendData = new FCMSendData(Common.createTopicOrder(),notifData);

                                        compositeDisposable.add(ifcmService.sendNotification(sendData)
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(fcmResponse -> {
                                                    //after cart cleaned
                                                    Snackbar snackBar = Snackbar.make(getActivity().findViewById(android.R.id.content),
                                                            R.string.order_placed, Snackbar.LENGTH_LONG);

                                                        snackBar.setBackgroundTint(Color.parseColor("#2c3e50"));
                                                    snackBar.show();
                                                    EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                                }, throwable -> {
                                                    Snackbar snackBar = Snackbar.make(getActivity().findViewById(android.R.id.content),
                                                            R.string.order_placed_notfi_failed, Snackbar.LENGTH_LONG);
                                                    snackBar.setBackgroundTint(Color.parseColor("#2c3e50"));
                                                    snackBar.show();

                                                    EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                                })
                                        );

                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Snackbar snackBar = Snackbar.make(getActivity().findViewById(android.R.id.content),
                                                ""+e.getMessage(), Snackbar.LENGTH_LONG);
                                        snackBar.setBackgroundTint(Color.parseColor("#2c3e50"));
                                        snackBar.show();
                                    }
                                });

                });
    }


    private void sumAllItemsInCart() {
        cartDataSource.sumPriceInCart(Common.currentUser.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Double>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Double aDouble) {
                        txtTotalPrice.setText(new StringBuilder(getString(R.string.total)).append(aDouble));
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!e.getMessage().contains("Query returned empty")) {
                            Snackbar snackBar = Snackbar.make(getActivity().findViewById(android.R.id.content),
                                    ""+e.getMessage(), Snackbar.LENGTH_LONG);
                            snackBar.setBackgroundTint(Color.parseColor("#2c3e50"));
                            snackBar.show();
                        }

                    }
                });
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
      //  menu.findItem(R.id.nav_call_now).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.cart_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_clear_cart) {
            cartDataSource.cleanCart(Common.currentUser.getUid())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<Integer>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(Integer integer) {
                            Toast.makeText(getContext(), R.string.clear_cart_success, Toast.LENGTH_SHORT).show();
                            EventBus.getDefault().postSticky(new CounterCartEvent(true));
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        return true;
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
        EventBus.getDefault().postSticky(new HideFABCart(false));
        cartViewModel.onStop();
        if (EventBus.getDefault().isRegistered(this))
          {  EventBus.getDefault().unregister(this);}

            compositeDisposable.clear();
            super.onStop();

    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onUpdateItemInCartEvent(UpdateItemInCart event) {
        if (event.getCartItem() != null) {
            //save state of RV
            recycleViewState = recyclerCart.getLayoutManager().onSaveInstanceState();
            cartDataSource.updateCartItems(event.getCartItem())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<Integer>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(Integer integer) {
                            // calculateTotalPrice();
                            sumAllItemsInCart();
                            //fix error refreshing rv after update
                            // recyclerCart.getLayoutManager().onRestoreInstanceState(recycleViewState);
                        }



                        @Override
                        public void onError(Throwable e) {
                            Snackbar snackBar = Snackbar.make(getActivity().findViewById(android.R.id.content),
                                    "[UPDATE CART]" + e.getMessage(), Snackbar.LENGTH_LONG);
                            snackBar.setBackgroundTint(Color.parseColor("#2c3e50"));
                            snackBar.show();
                        }
                    });

        }

    }

    private void calculateTotalPrice() {
        cartDataSource.sumPriceInCart(Common.currentUser.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Double>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Double price) {

                        try {
                            txtTotalPrice.setText(new StringBuilder("Total :")
                                    .append(Common.formatPrice(price)));
                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!e.getMessage().contains("Query returned empty result set")) {

                            Toast.makeText(getContext(), R.string.order_placed, Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    @OnClick(R.id.btn_place_order)
    public void onViewClicked() {
        showAlertDialog();
    }

    @Override
    public void onLoadTimeSuccess(OrderMV orderMV, long estimateTimeInMs) {
        orderMV.setCreateDate(estimateTimeInMs);
        orderMV.setOrderStatus(0);
        submitOrderToFirebase(orderMV);
    }

    @Override
    public void onLoadTimeFailed(String message) {
        Snackbar snackBar = Snackbar.make(getActivity().findViewById(android.R.id.content),
                ""+message, Snackbar.LENGTH_LONG);
        snackBar.setBackgroundTint(Color.parseColor("#2c3e50"));
        snackBar.show();
    }
}
