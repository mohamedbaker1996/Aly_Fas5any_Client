package com.simpelexo.alyfas5anyclient.Database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "Cart", primaryKeys = {"uid","foodId","foodSize"})
public class CartItem {

    @NonNull
    @ColumnInfo(name = "foodId")
    private String foodId;

    @ColumnInfo(name = "foodName")
    private String foodName;

    @ColumnInfo (name = "foodImage")
    private String foodImage;

    @ColumnInfo(name = "foodPrice")
    private Double foodPrice;

    @ColumnInfo(name = "foodBasePrice")
    private Double foodBasePrice;

    @ColumnInfo(name = "foodQuantity")
    private Double foodQuantity;

    @ColumnInfo(name = "userPhone")
    private String userPhone;

    @NonNull
    @ColumnInfo(name = "foodSize")
    private String foodSize;

    @NonNull
    @ColumnInfo(name = "uid")
    private String uid;

    @NonNull
    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(@NonNull String foodId) {
        this.foodId = foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getFoodImage() {
        return foodImage;
    }

    public void setFoodImage(String foodImage) {
        this.foodImage = foodImage;
    }

    public Double getFoodPrice() {
        return foodPrice;
    }

    public void setFoodPrice(Double foodPrice) {
        this.foodPrice = foodPrice;
    }

    public Double getFoodBasePrice() {
        return foodBasePrice;
    }

    public void setFoodBasePrice(Double foodBasePrice) {
        this.foodBasePrice = foodBasePrice;
    }

    public Double getFoodQuantity() {
        return foodQuantity;
    }

    public void setFoodQuantity(Double foodQuantity) {
        this.foodQuantity = foodQuantity;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getFoodSize() {
        return foodSize;
    }

    public void setFoodSize(String foodSize) {
        this.foodSize = foodSize;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this)
            return true;
        if(!(obj instanceof CartItem))
            return false;
        CartItem cartItem = (CartItem)obj;

        return cartItem.getFoodId().equals(this.foodId) &&
                cartItem.getFoodSize().equals(this.foodSize);
    }
}
