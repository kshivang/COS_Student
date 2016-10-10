package org.invincible.cosstudent.wizard.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import org.invincible.cosstudent.R;
import org.invincible.cosstudent.misc.MenuItem;
import org.invincible.cosstudent.wizard.model.MultipleFixedChoicePage;
import org.invincible.cosstudent.wizard.model.Page;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MultipleChoiceFragment extends ListFragment {
    private static final String ARG_KEY = "key";

    private PageFragmentCallbacks mCallbacks;
    private List<MenuItem> mChoices;
    private Page mPage;
    private ListView listView;

    public static MultipleChoiceFragment newInstance(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        MultipleChoiceFragment fragment = new MultipleChoiceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public MultipleChoiceFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        mPage = mCallbacks.onGetPage(args.getString(ARG_KEY));

        MultipleFixedChoicePage fixedChoicePage = (MultipleFixedChoicePage) mPage;

        mChoices = new ArrayList<>();
        if (fixedChoicePage.getOptionCount() != 0)
            mChoices.addAll(fixedChoicePage.getMultipleChoices());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_page, container, false);
        ((TextView) rootView.findViewById(android.R.id.title)).setText(mPage.getTitle());



        listView = (ListView) rootView.findViewById(android.R.id.list);
        setListAdapter(new MultipleChoiceAdapter());

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        ViewGroup myHeader = (ViewGroup)inflater.inflate(R.layout.multiple_choice_layout, listView, false);
        listView.addHeaderView(myHeader, null, false);

        // Pre-select currently selected items.
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> selectedItems = mPage.getData().getStringArrayList(
                        Page.SIMPLE_DATA_KEY);

                ArrayList<Integer> selectedQty = mPage.getData().getIntegerArrayList(
                        Page.QTY_DATA_KEY);

                if (selectedItems == null || selectedItems.size() == 0) {
                    return;
                }

                Set<String> selectedSet = new HashSet<>(selectedItems);

                int j = 0;
                for (int i = 0; i < mChoices.size(); i++) {
                    if (selectedSet.contains(mChoices.get(i).getName())) {
                        listView.setItemChecked(i, true);
                        if (selectedQty != null && selectedQty.size() >= selectedItems.size())
                            mChoices.get(i).setQty(selectedQty.get(j));
                        j++;
                    }
                }
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (!(context instanceof PageFragmentCallbacks)) {
            throw new ClassCastException("Activity must implement PageFragmentCallbacks");
        }

        mCallbacks = (PageFragmentCallbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

//    @Override
//    public void onListItemClick(ListView l, View v, int position, long id) {
//        onItemClick();
//    }

    public void onItemClick() {

//        SparseBooleanArray checkedPositions = listView.getCheckedItemPositions();
        ArrayList<Integer> qtys = new ArrayList<>();
        ArrayList<String> selections = new ArrayList<>();

        for (int i = 0; i < mChoices.size(); i ++) {
            if (listView.isItemChecked(i)) {
                selections.add(mChoices.get(i).getName());
                qtys.add(mChoices.get(i).getQty());
            }
        }

        mPage.getData().putStringArrayList(Page.SIMPLE_DATA_KEY, selections);
        mPage.getData().putIntegerArrayList(Page.QTY_DATA_KEY, qtys);
        mPage.notifyDataChanged();
    }

    public class MultipleChoiceAdapter extends BaseAdapter {
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
            return mChoices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mChoices.get(position).hashCode();
        }

        @Override
        public int getCount() {
            return mChoices.size();
        }

        @Override
        public @NonNull
        View getView(final int i, View convertView, @NonNull ViewGroup parent) {
            View v = convertView;

            if (convertView == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getActivity());
                v = vi.inflate(R.layout.multiple_choice_layout, parent, false);
            }

            MenuItem multipleChoice = mChoices.get(i);

            if (multipleChoice != null) {
                TextView tvChoice = (TextView) v.findViewById(R.id.text1);
                TextView tvPrice = (TextView) v.findViewById(R.id.price);
                final TextView tvQty = (TextView) v.findViewById(R.id.qty);
                final CheckBox cbItemCheck = (CheckBox) v.findViewById(R.id.checkbox);

                if (tvChoice != null && tvPrice != null){
                    tvChoice.setText(multipleChoice.getName());
                    tvPrice.setText(String.format(Locale.ENGLISH, "%s%d",
                            getString(R.string.rupee), multipleChoice.getPrice()));
                }


                if (tvQty != null){
                    tvQty.setText(String.format(Locale.ENGLISH, "%d", multipleChoice.getQty()));
                    tvQty.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onQty(tvQty, i);
                        }
                    });
                }

                if (cbItemCheck != null) {
                    cbItemCheck.setVisibility(View.VISIBLE);
                    cbItemCheck.setChecked(listView.isItemChecked(i));
                    cbItemCheck.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (listView.isItemChecked(i)) {
////                                listView.setItemChecked(i, false);
//                            } else {
                                onQty(tvQty, i);
                            }
                        }
                    });
                    cbItemCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            listView.setItemChecked(i, isChecked);
                            onItemClick();
                        }
                    });
                }

            }
            return v;
        }

        void onQty(final TextView textView, final int i) {
            CharSequence[] orderQuantity = new CharSequence[30];
            for (int j = 1; j <= 30; j ++) {
                orderQuantity[j - 1] = "For " + String.valueOf(j) + " person";
            }
            new AlertDialog.Builder(getContext())
                    .setTitle("Select Quantity")
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            listView.setItemChecked(i, true);
                            dialog.dismiss();
                        }
                    })
                    .setSingleChoiceItems(orderQuantity, mChoices.get(i).getQty() - 1,
                            new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            textView.setText(String.format(Locale.ENGLISH, "%d", which + 1));
                            mChoices.get(i).setQty(which + 1);
                            if (!listView.isItemChecked(i))
                                listView.setItemChecked(i, true);
                            else onItemClick();
                            dialog.dismiss();
                        }
                    }).create().show();
        }

    }
}
