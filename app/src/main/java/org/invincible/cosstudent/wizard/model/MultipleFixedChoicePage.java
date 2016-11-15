package org.invincible.cosstudent.wizard.model;

import android.support.v4.app.Fragment;

import org.invincible.cosstudent.wizard.ui.MultipleChoiceFragment;

import java.util.ArrayList;

/**
 * A page offering the user a number of non-mutually exclusive choices.
 */
public class MultipleFixedChoicePage extends SingleFixedChoicePage {
    public MultipleFixedChoicePage(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
    }

    @Override
    public Fragment createFragment() {
        return MultipleChoiceFragment.newInstance(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        StringBuilder sb = new StringBuilder();

        ArrayList<String> selectionValue = mData.getStringArrayList(Page.SIMPLE_DATA_KEY);
        ArrayList<Integer> selectionQuantity = mData.getIntegerArrayList(Page.QTY_DATA_KEY);

//        if (selectionValue != null && selectionValue.size() > 0) {
//            for (String selection : selectionValue) {
//                if (sb.length() > 0) {
//                    sb.append(", ");
//                }
//                sb.append(selection);
//            }
//        }

        dest.add(new ReviewItem(getTitle(), selectionValue, getKey(), selectionQuantity));
    }

    @Override
    public boolean isCompleted() {
        ArrayList<String> selections = mData.getStringArrayList(Page.SIMPLE_DATA_KEY);
        return selections != null && selections.size() > 0;
    }
}
