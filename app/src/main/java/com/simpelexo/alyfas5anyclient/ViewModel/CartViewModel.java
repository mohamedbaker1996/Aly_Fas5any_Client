package com.simpelexo.alyfas5anyclient.ViewModel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.simpelexo.alyfas5anyclient.Database.CartDataSource;
import com.simpelexo.alyfas5anyclient.Database.CartDatabase;
import com.simpelexo.alyfas5anyclient.Database.CartItem;
import com.simpelexo.alyfas5anyclient.Database.LocalCartDataSource;
import com.simpelexo.alyfas5anyclient.Utiles.Common;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class CartViewModel extends ViewModel {

    private CompositeDisposable compositeDisposable ;
    private CartDataSource cartDataSource;
    private MutableLiveData<List<CartItem>> mutableLiveDataCartItems;

    public CartViewModel() {
        compositeDisposable = new CompositeDisposable();
    }

    public void initCartDataSource(Context context){
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(context).cartDAO());
    }
    public void onStop(){
        compositeDisposable.clear();
    }

    public MutableLiveData<List<CartItem>> getMutableLiveDataCartItems() {
        if (mutableLiveDataCartItems == null)
            mutableLiveDataCartItems = new MutableLiveData<>();
        getAllCartItems();
        return mutableLiveDataCartItems;
    }

    private void getAllCartItems() {
    compositeDisposable.add(cartDataSource.getAllCart(Common.currentUser.getUid())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(cartItems -> {
            mutableLiveDataCartItems.setValue(cartItems);
        }, throwable -> {
            mutableLiveDataCartItems.setValue(null);
        })
    );
    }
}
