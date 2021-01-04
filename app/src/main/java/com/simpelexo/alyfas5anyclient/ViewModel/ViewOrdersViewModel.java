package com.simpelexo.alyfas5anyclient.ViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.simpelexo.alyfas5anyclient.Model.OrderMV;

import java.util.List;

public class ViewOrdersViewModel extends ViewModel {
 private MutableLiveData<List<OrderMV>> mutableLiveDataOrderList;

    public ViewOrdersViewModel() {
        mutableLiveDataOrderList = new MutableLiveData<>();
    }

    public MutableLiveData<List<OrderMV>> getMutableLiveDataOrderList() {
        return mutableLiveDataOrderList;
    }

    public void setMutableLiveDataOrderList(List<OrderMV> orderMVList) {
        mutableLiveDataOrderList.setValue(orderMVList);
    }
}
