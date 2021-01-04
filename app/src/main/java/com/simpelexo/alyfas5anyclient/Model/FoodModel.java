package com.simpelexo.alyfas5anyclient.Model;

import java.util.List;

public class FoodModel {
    private String Key ;
    private String name, image, id ,description;
    private Long price ;
  //  private List<AddOn> addOn;
    private List<SizeModel> size;
    private Double ratingValue;
    private Long ratingCount;

    //for Cart
    private SizeModel userSelectedSize;

    public FoodModel() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

//    public List<AddOn> getAddOn() {
//        return addOn;
//    }
//
//    public void setAddOn(List<AddOn> addOn) {
//        this.addOn = addOn;
//    }

    public List<SizeModel> getSize() {
        return size;
    }

    public void setSize(List<SizeModel> size) {
        this.size = size;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public Double getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(Double ratingValue) {
        this.ratingValue = ratingValue;
    }

    public Long getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Long ratingCount) {
        this.ratingCount = ratingCount;
    }



    public SizeModel getUserSelectedSize() {
        return userSelectedSize;
    }

    public void setUserSelectedSize(SizeModel userSelectedSize) {
        this.userSelectedSize = userSelectedSize;
    }
}
