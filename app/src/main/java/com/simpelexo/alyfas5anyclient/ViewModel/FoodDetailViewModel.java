package com.simpelexo.alyfas5anyclient.ViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.simpelexo.alyfas5anyclient.Model.Comment;
import com.simpelexo.alyfas5anyclient.Model.FoodModel;
import com.simpelexo.alyfas5anyclient.Utiles.Common;

public class FoodDetailViewModel extends ViewModel  {

    private MutableLiveData<FoodModel> mutableLiveDataFood;

    private MutableLiveData<Comment> mutableLiveDataComment;

    public void setCommentModel(Comment commentModel){
        if (mutableLiveDataComment != null) {
            mutableLiveDataComment.setValue(commentModel);
        }
    }

    public MutableLiveData<Comment> getMutableLiveDataComment() {
        return mutableLiveDataComment;
    }

    public FoodDetailViewModel() {
         mutableLiveDataComment = new MutableLiveData<>();
    }

    public MutableLiveData<FoodModel> getMutableLiveDataFood() {
        if (mutableLiveDataFood == null)
            mutableLiveDataFood = new MutableLiveData<>();
            mutableLiveDataFood.setValue(Common.selectedFood);


        return mutableLiveDataFood;
    }

    public void setFoodModel(FoodModel foodModel) {
        if (mutableLiveDataFood != null) {
            mutableLiveDataFood.setValue(foodModel);
        }
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
