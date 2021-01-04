package com.simpelexo.alyfas5anyclient.Callback;

import com.simpelexo.alyfas5anyclient.Model.PopularCategory;

import java.util.List;

public interface IPopularCallbackListener {
    void onPopularLoadSuccess(List<PopularCategory> popularCategories);
    void onPopularLoadFailed (String message);

}
