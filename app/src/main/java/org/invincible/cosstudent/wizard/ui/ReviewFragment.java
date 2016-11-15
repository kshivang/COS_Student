package org.invincible.cosstudent.wizard.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.invincible.cosstudent.R;
import org.invincible.cosstudent.wizard.model.AbstractWizardModel;
import org.invincible.cosstudent.wizard.model.ModelCallbacks;
import org.invincible.cosstudent.wizard.model.Page;
import org.invincible.cosstudent.wizard.model.ReviewItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ReviewFragment extends ListFragment implements ModelCallbacks {
    private Callbacks mCallbacks;
    private AbstractWizardModel mWizardModel;
    private List<ReviewItem> mCurrentReviewItems;

    private ReviewAdapter mReviewAdapter;

    public ReviewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReviewAdapter = new ReviewAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_page, container, false);

        TextView titleView = (TextView) rootView.findViewById(android.R.id.title);
        titleView.setText(R.string.review);
        titleView.setTextColor(ContextCompat.getColor(getContext(), R.color.review_green));

        ListView listView = (ListView) rootView.findViewById(android.R.id.list);
        setListAdapter(mReviewAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        return rootView;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);

        if (!(activity instanceof Callbacks)) {
            throw new ClassCastException("Activity must implement fragment's callbacks");
        }

        mCallbacks = (Callbacks) activity;

        mWizardModel = mCallbacks.onGetModel();
        mWizardModel.registerListener(this);
        onPageTreeChanged();
    }

    @Override
    public void onPageTreeChanged() {
        onPageDataChanged(null);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;

        mWizardModel.unregisterListener(this);
    }

    @Override
    public void onPageDataChanged(Page changedPage) {
        ArrayList<ReviewItem> reviewItems = new ArrayList<>();
        for (Page page : mWizardModel.getCurrentPageSequence()) {
            page.getReviewItems(reviewItems);
        }
        Collections.sort(reviewItems, new Comparator<ReviewItem>() {
            @Override
            public int compare(ReviewItem a, ReviewItem b) {
                return a.getWeight() > b.getWeight() ? +1 : a.getWeight() < b.getWeight() ? -1 : 0;
            }
        });
        mCurrentReviewItems = reviewItems;

        if (mReviewAdapter != null) {
            mReviewAdapter.notifyDataSetInvalidated();
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        int mPosition = 0;
        int itemCount = 0;

        for (ReviewItem reviewItem: mCurrentReviewItems) {
            if (reviewItem.getDisplayValue() != null) {
                int currentItemSize = reviewItem.getDisplayValue().size();
//                if (position >= itemCount && position <= itemCount + currentItemSize) {
                if (position >= itemCount && position < itemCount + currentItemSize) {
                    mCallbacks.onEditScreenAfterReview(reviewItem.getPageKey());
                    return;
                }
//                itemCount = itemCount + currentItemSize + 1;
                itemCount = itemCount + currentItemSize;
            }
            mPosition++;
        }
        mCallbacks.onEditScreenAfterReview(mCurrentReviewItems.get(mPosition - 1).getPageKey());
    }

    public interface Callbacks {
        AbstractWizardModel onGetModel();
        void onEditScreenAfterReview(String pageKey);
    }

    private class ReviewAdapter extends BaseAdapter {
        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }

        @Override
        public Object getItem(int position) {
            int mPosition = 0;
            int itemCount = 0;

            for (ReviewItem reviewItem: mCurrentReviewItems) {
                int currentItemSize = reviewItem.getDisplayValue().size();
                if (position >= itemCount && position < itemCount + currentItemSize){
                    return mCurrentReviewItems.get(mPosition);
                }
//                itemCount = itemCount + currentItemSize + 1;
                itemCount = itemCount + currentItemSize;
                mPosition++;
            }
            return mCurrentReviewItems.get(mPosition - 1);
        }

        @Override
        public long getItemId(int position) {
            int mPosition = 0;
            int itemCount = 0;

            for (ReviewItem reviewItem: mCurrentReviewItems) {
                if (reviewItem.getDisplayValue() != null) {
                    int currentItemSize = reviewItem.getDisplayValue().size();
                    if (position >= itemCount && position < itemCount + currentItemSize) {
                        return mCurrentReviewItems.get(mPosition).hashCode();
                    }
//                    itemCount = itemCount + currentItemSize + 1;
                    itemCount = itemCount + currentItemSize;
                }
                mPosition++;
            }
            return mCurrentReviewItems.get(mPosition - 1).hashCode();
        }

        @Override
        public int getCount() {
            int count = 0;
            if (mCurrentReviewItems!= null) {
//                count = count + mCurrentReviewItems.size();
                for(ReviewItem reviewItem: mCurrentReviewItems) {
                    if (reviewItem.getDisplayValue() != null)
                        count = count + reviewItem.getDisplayValue().size();
                }
            } else {
                count = 0;
            }
            return count;
        }

        @Override
        public View getView(int position, View view, ViewGroup container) {
            int itemCount = 0;

            View rootView = view;
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            if (rootView == null) {
                rootView = inflater.inflate(R.layout.list_item_review, container, false);
            }
            for (ReviewItem reviewItem: mCurrentReviewItems) {
                int currentItemSize = 0;
                ArrayList<String> value = reviewItem.getDisplayValue();
                ArrayList<Integer> quantity = reviewItem.getmQuantity();
                if (value != null) {
                    currentItemSize = value.size();
                }
//                if (position == itemCount) {
//                    ((TextView) rootView.findViewById(android.R.id.text1)).
//                            setText(reviewItem.getTitle());
//                }
//                else if (position > itemCount && position <= itemCount + currentItemSize){
                if (position >= itemCount && position < itemCount + currentItemSize){
//                    int currentPosition = position - itemCount - 1;
                    int currentPosition = position - itemCount;
                    if (value != null && value.size() >= currentPosition) {
                        ((TextView) rootView.findViewById(android.R.id.text1)).
                                setText(value.get(currentPosition));
                        ((TextView) rootView.findViewById(android.R.id.text2)).
                                setText(String.format(Locale.ENGLISH, "Qty : %d",
                                        quantity.get(currentPosition)));
                    }
                }
//                itemCount = itemCount + currentItemSize + 1;
                itemCount = itemCount + currentItemSize;
            }
            return rootView;
        }
    }
}
