package com.mobileComputing.groupProject.models;

public class Category {

    private String categoryName;
    private int hexCode;

    public Category (String categoryName, int hexCode) {
        this.categoryName = categoryName;
        this.hexCode = hexCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getHexCode() {
        return hexCode;
    }
}
