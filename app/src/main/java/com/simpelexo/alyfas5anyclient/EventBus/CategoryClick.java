package com.simpelexo.alyfas5anyclient.EventBus;

import com.simpelexo.alyfas5anyclient.Model.Category;

public class CategoryClick {
    private boolean success;
    private Category categoryModel;

    public CategoryClick(boolean success, Category categoryModel) {
        this.success = success;
        this.categoryModel = categoryModel;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Category getCategoryModel() {
        return categoryModel;
    }

    public void setCategoryModel(Category categoryModel) {
        this.categoryModel = categoryModel;
    }
}
