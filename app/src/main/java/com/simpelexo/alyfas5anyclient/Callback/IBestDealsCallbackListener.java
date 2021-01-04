package com.simpelexo.alyfas5anyclient.Callback;

import com.simpelexo.alyfas5anyclient.Model.BestDeals;

import java.util.List;

public interface IBestDealsCallbackListener {
    void onBestDealsLoadSuccess(List<BestDeals> bestDeals);
    void onBestDealsLoadFailed (String message);
}
