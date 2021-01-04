package com.simpelexo.alyfas5anyclient.ui.Fragment.HomeCycle;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.simpelexo.alyfas5anyclient.Adapters.OrdersAdapter;
import com.simpelexo.alyfas5anyclient.Callback.ILoadOrderCallbackListener;
import com.simpelexo.alyfas5anyclient.Model.OrderMV;
import com.simpelexo.alyfas5anyclient.R;
import com.simpelexo.alyfas5anyclient.Utiles.Common;
import com.simpelexo.alyfas5anyclient.ViewModel.ViewOrdersViewModel;
import com.simpelexo.alyfas5anyclient.ui.Fragment.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;


public class ViewOrderFragment extends BaseFragment implements ILoadOrderCallbackListener {
    @BindView(R.id.recycler_order)
    RecyclerView recyclerOrder;
    private Unbinder unbinder;
    private ViewOrdersViewModel ViewOrdersViewModel;
    AlertDialog dialog;
    private ILoadOrderCallbackListener listener;

    public ViewOrderFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewOrdersViewModel =
                ViewModelProviders.of(this).get(ViewOrdersViewModel.class);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_order, container, false);
        setUpActivity();
        unbinder = ButterKnife.bind(this, view);
        initView(view);
        loadOrdersFromFirebase();
        ViewOrdersViewModel.getMutableLiveDataOrderList().observe(this,orderMVList -> {
            OrdersAdapter adapter = new OrdersAdapter(getContext(),orderMVList);
            recyclerOrder.setAdapter(adapter);
        });
        return view;
    }

    private void loadOrdersFromFirebase() {
    List<OrderMV> orderMVList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference(Common.ORDER_REF)
                .orderByChild("userId")
                .equalTo(Common.currentUser.getUid())
                .limitToLast(100)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot orderSnapshot:snapshot.getChildren())
                        {
                            OrderMV orderMV = orderSnapshot.getValue(OrderMV.class);
                            orderMV.setOrderNumber(orderSnapshot.getKey());
                            orderMVList.add(orderMV);
                        }
                        listener.onLoadOrderSuccess(orderMVList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                listener.onLoadOrderFailed(error.getMessage());
                    }
                });
    }

    private void initView(View view) {
        listener = this;
    dialog = new SpotsDialog.Builder().setCancelable(false).setContext(getContext()).build();

    recyclerOrder.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerOrder.setLayoutManager(layoutManager);
        recyclerOrder.addItemDecoration(new DividerItemDecoration(getContext(),layoutManager.getOrientation()));
    }

    @Override
    public void onBack() {
        super.onBack();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onLoadOrderSuccess(List<OrderMV> orderMVList) {
    dialog.dismiss();
        ViewOrdersViewModel.setMutableLiveDataOrderList(orderMVList);
    }

    @Override
    public void onLoadOrderFailed(String message) {
        dialog.dismiss();
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
