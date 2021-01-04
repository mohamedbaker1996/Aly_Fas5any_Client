package com.simpelexo.alyfas5anyclient.Callback;

import com.simpelexo.alyfas5anyclient.Model.OrderMV;

public interface ILoadTimeFromFirebaseListener {
    void onLoadTimeSuccess(OrderMV orderMV,long estimateTimeInMs);
    void onLoadTimeFailed(String message);
}
