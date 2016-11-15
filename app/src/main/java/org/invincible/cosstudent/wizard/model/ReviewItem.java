package org.invincible.cosstudent.wizard.model;

import java.util.ArrayList;

/**
 * Represents a single line item on the final review page.
 *
 */
public class ReviewItem {
    private static final int DEFAULT_WEIGHT = 0;

    private int mWeight;
    private String mTitle;
    private ArrayList<String> mDisplayValue;
    private ArrayList<Integer> mQuantity;
    private String mPageKey;

    ReviewItem(String title, ArrayList<String> displayValue, String pageKey, ArrayList<Integer> quantity) {
        this(title, displayValue, pageKey, quantity, DEFAULT_WEIGHT);
    }

    private ReviewItem(String title, ArrayList<String> displayValue, String pageKey, ArrayList<Integer> quantity, int weight) {
        mTitle = title;
        mDisplayValue = displayValue;
        mPageKey = pageKey;
        mWeight = weight;
        mQuantity = quantity;
    }

    public ArrayList<String> getDisplayValue() {
        return mDisplayValue;
    }

    public void setDisplayValue(ArrayList<String> displayValue) {
        mDisplayValue = displayValue;
    }

    public String getPageKey() {
        return mPageKey;
    }

    public void setPageKey(String pageKey) {
        mPageKey = pageKey;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public int getWeight() {
        return mWeight;
    }

    public void setWeight(int weight) {
        mWeight = weight;
    }

    public ArrayList<Integer> getmQuantity() {
        return mQuantity;
    }

    public void setmQuantity(ArrayList<Integer> mQuantity) {
        this.mQuantity = mQuantity;
    }
}
