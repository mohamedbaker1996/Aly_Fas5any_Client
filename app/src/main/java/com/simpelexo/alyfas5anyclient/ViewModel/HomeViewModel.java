package com.simpelexo.alyfas5anyclient.ViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.simpelexo.alyfas5anyclient.Callback.IBestDealsCallbackListener;
import com.simpelexo.alyfas5anyclient.Callback.IPopularCallbackListener;
import com.simpelexo.alyfas5anyclient.Model.BestDeals;
import com.simpelexo.alyfas5anyclient.Model.PopularCategory;
import com.simpelexo.alyfas5anyclient.Utiles.Common;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel implements IPopularCallbackListener, IBestDealsCallbackListener {

    private MutableLiveData <List<PopularCategory>> popularList;
    private MutableLiveData <List<BestDeals>> bestDealList;
    private MutableLiveData <String> messageError;
    private IPopularCallbackListener popularCallbackListener;
    private IBestDealsCallbackListener bestDealsCallbackListener;
    public HomeViewModel() {
        popularCallbackListener = this;
        bestDealsCallbackListener = this;
    }

    public MutableLiveData<List<BestDeals>> getBestDealList() {
        if (bestDealList == null) {
            bestDealList = new MutableLiveData<>();
            messageError = new MutableLiveData<>();
            loadBestDealList();
        }
        return bestDealList;
    }

    private void loadBestDealList() {
        List<BestDeals> tempList = new ArrayList<>();
        DatabaseReference bestDealsRef = FirebaseDatabase.getInstance().getReference(Common.BEST_DEAL_REF);
        bestDealsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot itemSnapShot:snapshot.getChildren())
                {
                    BestDeals model = itemSnapShot.getValue(BestDeals.class);
                    tempList.add(model);
                }
                bestDealsCallbackListener.onBestDealsLoadSuccess(tempList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                bestDealsCallbackListener.onBestDealsLoadFailed(error.getMessage());

            }
        });
    }

    public MutableLiveData<List<PopularCategory>> getPopularList() {
        if (popularList == null) {
            popularList = new MutableLiveData<>();
            messageError = new MutableLiveData<>();
            loadPopularList();
        }
      return popularList;
    }

    private void loadPopularList() {
        List<PopularCategory> tempList = new ArrayList<>();
        DatabaseReference popularRef = FirebaseDatabase.getInstance().getReference(Common.POPULAR_CATEGORY_REF);
        popularRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
            for (DataSnapshot itemSnapShot:snapshot.getChildren())
            {
                PopularCategory model = itemSnapShot.getValue(PopularCategory.class);
                tempList.add(model);
            }
            popularCallbackListener.onPopularLoadSuccess(tempList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            popularCallbackListener.onPopularLoadFailed(error.getMessage());
            }
        });
    }

    public MutableLiveData<String> getMessageError() {
        return messageError;
    }

    @Override
    public void onPopularLoadSuccess(List<PopularCategory> popularCategories) {
        popularList.setValue(popularCategories);
    }

    @Override
    public void onPopularLoadFailed(String message) {
    messageError.setValue(message);
    }

    @Override
    public void onBestDealsLoadSuccess(List<BestDeals> bestDeals) {
    bestDealList.setValue(bestDeals);
    }

    @Override
    public void onBestDealsLoadFailed(String message) {
    messageError.setValue(message);
    }
}
