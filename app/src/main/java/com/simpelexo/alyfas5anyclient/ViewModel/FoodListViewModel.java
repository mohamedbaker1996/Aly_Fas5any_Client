package com.simpelexo.alyfas5anyclient.ViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.simpelexo.alyfas5anyclient.Model.FoodModel;
import com.simpelexo.alyfas5anyclient.Utiles.Common;

import java.util.List;

public class FoodListViewModel extends ViewModel  {

    private MutableLiveData<List<FoodModel>> mutableLiveDataFoodList;

    public FoodListViewModel() {
    }

    public MutableLiveData<List<FoodModel>> getMutableLiveDataFoodList() {
        if (mutableLiveDataFoodList == null)
            mutableLiveDataFoodList = new MutableLiveData<>();
            mutableLiveDataFoodList.setValue(Common.categorySelected.getFoods());
          return mutableLiveDataFoodList;
    }



/*  private void loadFoods() {
        List<Category> tempList = new ArrayList<>();
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference(Common.FOODS_REF);
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot itemSnapShot: snapshot.getChildren()){
                    Category categoryModel = itemSnapShot.getValue(Category.class);
                    String  keyValue = snapshot.getKey();
                    assert categoryModel != null;
                    categoryModel.setCategoryID(keyValue);
                    tempList.add(categoryModel);
                }
                categoryCallbackListener.onCategoryLoadSuccess(tempList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                categoryCallbackListener.onCategoryLoadFailed(error.getMessage());
            }
        });

    }*/



}
