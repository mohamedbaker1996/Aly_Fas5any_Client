package com.simpelexo.alyfas5anyclient.ViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.simpelexo.alyfas5anyclient.Callback.ICategoryCallbackListener;
import com.simpelexo.alyfas5anyclient.Model.Category;
import com.simpelexo.alyfas5anyclient.Utiles.Common;

import java.util.ArrayList;
import java.util.List;

public class MenuMVViewModel extends ViewModel implements ICategoryCallbackListener {
    private MutableLiveData<List<Category>> categoryListMutable;
    private MutableLiveData<String> messageError = new MutableLiveData<>() ;
    private ICategoryCallbackListener categoryCallbackListener;


    public MenuMVViewModel() {
    categoryCallbackListener = this;
    }

    public MutableLiveData<List<Category>> getCategoryListMutable() {
        if (categoryListMutable == null) {
            categoryListMutable = new MutableLiveData<>();
            messageError = new MutableLiveData<>();
            loadCategories();
        }
        return categoryListMutable;
    }

    private void loadCategories() {
    List<Category> tempList = new ArrayList<>();
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference(Common.CATEGORY_REF);
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot itemSnapShot: snapshot.getChildren()){
//                    Category categoryModel = itemSnapShot.getValue(Category.class);
//                   categoryModel.setMenu_id(snapshot.getKey());
//                    Common.categorySelected = categoryModel;
//                    tempList.add(categoryModel);
                    Common.categorySelected = itemSnapShot.getValue(Category.class);
                    Common.categorySelected.setMenu_id(itemSnapShot.getKey());
                    tempList.add(Common.categorySelected);
                }
                categoryCallbackListener.onCategoryLoadSuccess(tempList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            categoryCallbackListener.onCategoryLoadFailed(error.getMessage());
            }
        });

    }

    public MutableLiveData<String> getMessageError() {
        return messageError;
    }

    @Override
    public void onCategoryLoadSuccess(List<Category> categoriesList) {
        categoryListMutable.setValue(categoriesList);
    }

    @Override
    public void onCategoryLoadFailed(String message) {
    messageError.setValue(message);
    }
}
