package com.pizzacheese.pizzacheesemanager.Types;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.Serializable;

/**
 * Created by Tal on 10/08/2017.
 */

public class Product implements Serializable{
    private boolean preferred;
    private String objectType;
    private String id;
    private String name;
    private String type;
    private String pictureId;
    private double price;
    private boolean available;
   // private boolean canHaveToppings;
    private String uri;

    public Product(String id,String name,double price,String type){
        this.id=id;
        this.name=name;
        this.price=price;
        this.type=type;
    }



    public Product(){

    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String toString(){
        return name+" : "+Double.toString(price)+"₪";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public String getPictureId() {
        return pictureId;
    }

    public void setPictureId(String pictureId) {
        this.pictureId = pictureId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isPreferred() {
        return preferred;
    }

    public void setPreferred(boolean preferred) {
        this.preferred = preferred;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public void setBM(Bitmap bm){

    }
}
