package com.simpelexo.alyfas5anyclient.EventBus;

import com.simpelexo.alyfas5anyclient.Model.PopularCategory;

public class PopularCategoryClick {
    private PopularCategory popularCategoryModel;

    public PopularCategoryClick(PopularCategory popularCategoryModel) {
        this.popularCategoryModel = popularCategoryModel;
    }

    public PopularCategory getPopularCategoryModel() {
        return popularCategoryModel;
    }

    public void setPopularCategoryModel(PopularCategory popularCategoryModel) {
        this.popularCategoryModel = popularCategoryModel;
    }
}
