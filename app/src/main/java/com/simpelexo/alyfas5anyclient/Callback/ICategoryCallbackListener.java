package com.simpelexo.alyfas5anyclient.Callback;

import com.simpelexo.alyfas5anyclient.Model.Category;

import java.util.List;

public interface ICategoryCallbackListener {
    void onCategoryLoadSuccess(List<Category> categoriesList);
    void onCategoryLoadFailed (String message);
}
