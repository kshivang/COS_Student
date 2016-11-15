package org.invincible.cosstudent.wizard.model;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

import org.invincible.cosstudent.misc.MenuItem;
import org.invincible.cosstudent.wizard.ui.SingleChoiceFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * A page offering the user a number of mutually exclusive choices.
 */
public class SingleFixedChoicePage extends Page {
    private ArrayList<MenuItem> mChoices = new ArrayList<>();

    SingleFixedChoicePage(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
    }

    @Override
    public Fragment createFragment() {
        return SingleChoiceFragment.create(getKey());
    }

    public String getOptionAt(int position) {
        return mChoices.get(position).getName();
    }

    public List<MenuItem> getMultipleChoices() {
        return mChoices;
    }

    public int getOptionCount() {
        return mChoices.size();
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem(getTitle(), mData.getStringArrayList(SIMPLE_DATA_KEY), getKey(),
                mData.getIntegerArrayList(QTY_DATA_KEY)));
    }

    @Override
    public boolean isCompleted() {
        return !TextUtils.isEmpty(mData.getString(SIMPLE_DATA_KEY));
    }

    public SingleFixedChoicePage setChoices(List<MenuItem> choices) {
        mChoices.addAll(choices);
        return this;
    }

//    public SingleFixedChoicePage setChoices(String... choices) {
//        for (String choice: choices)
//            mChoices.add(new MultipleChoice(choice, 1, 0));
//        return this;
//    }

    public SingleFixedChoicePage setValue(String value) {
        mData.putString(SIMPLE_DATA_KEY, value);
        return this;
    }
}
