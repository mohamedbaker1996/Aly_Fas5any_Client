package com.simpelexo.alyfas5anyclient.Model;

import java.util.List;

public class Category {
    private String Name;
    private String Image;
    private String menu_id;
    private transient String CategoryID;

    List<FoodModel> foods;


    public Category() {
    }

    public Category(String name, String image) {
        Name = name;
        Image = image;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getCategoryID() {
        return CategoryID;

    }

    public String getMenu_id() {
        return menu_id;
    }

    public void setMenu_id(String menu_id) {
        this.menu_id = menu_id;
    }

    public List<FoodModel> getFoods() {
        return foods;
    }

    public void setFoods(List<FoodModel> foods) {
        this.foods = foods;
    }

    public void setCategoryID(String categoryID) {
        CategoryID = categoryID;
    }
}
