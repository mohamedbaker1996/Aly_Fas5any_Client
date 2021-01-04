package com.simpelexo.alyfas5anyclient.Callback;

import com.simpelexo.alyfas5anyclient.Model.OrderMV;

import java.util.List;

public interface ILoadOrderCallbackListener {
    void onLoadOrderSuccess(List<OrderMV> orderMVList);
    void onLoadOrderFailed(String message);

}
