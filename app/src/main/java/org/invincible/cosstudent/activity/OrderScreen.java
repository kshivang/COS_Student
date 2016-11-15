

package org.invincible.cosstudent.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.invincible.cosstudent.R;
import org.invincible.cosstudent.misc.MenuItem;
import org.invincible.cosstudent.misc.Outlet;
import org.invincible.cosstudent.misc.UserLocalStore;
import org.invincible.cosstudent.wizard.model.AbstractWizardModel;
import org.invincible.cosstudent.wizard.model.ModelCallbacks;
import org.invincible.cosstudent.wizard.model.MultipleFixedChoicePage;
import org.invincible.cosstudent.wizard.model.Page;
import org.invincible.cosstudent.wizard.model.PageList;
import org.invincible.cosstudent.wizard.ui.MenuFragment;
import org.invincible.cosstudent.wizard.ui.PageFragmentCallbacks;
import org.invincible.cosstudent.wizard.ui.ReviewFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class OrderScreen extends AppCompatActivity implements
        PageFragmentCallbacks,
        ReviewFragment.Callbacks,
        MenuFragment.Callbacks,
        ModelCallbacks {
    private ViewPager mPager;

    private MyPagerAdapter mPagerAdapter;

    private boolean mEditingAfterReview;

    private AbstractWizardModel mWizardModel;

    private boolean mConsumePageSelectedEvent;

    private Button mNextButton;
    private Button mPrevButton;

    private List<Page> mCurrentPageSequence;
    private Outlet outlet;
    private ActionBar actionBar;

    private RelativeLayout rlProgress_view;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();

        Intent fromIntent = getIntent();
        outlet = (Outlet)fromIntent.getSerializableExtra("outlet");

        rlProgress_view = (RelativeLayout) findViewById(R.id.progress_bar_holder);

        onServerRequest(savedInstanceState);

        mNextButton = (Button) findViewById(R.id.next_button);
        mPrevButton = (Button) findViewById(R.id.prev_button);

    }

    @Override
    protected void onStart(){
        super.onStart();

        if(actionBar != null) {
            actionBar.setTitle(outlet.getName());
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    private List<List<MenuItem>> menus = new ArrayList<>();

    private void onServerRequest(final Bundle savedInstanceState) {
        rlProgress_view.setVisibility(View.VISIBLE);

        FirebaseDatabase.getInstance().getReference().child("restaurant")
                .child(outlet.getKey()).child("menu")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    rlProgress_view.setVisibility(View.GONE);
                    int i = 0;
                    for (DataSnapshot menu : dataSnapshot.getChildren()) {
                        menus.add(i, new ArrayList<MenuItem>());
                        for (DataSnapshot item : menu.child("submenu").getChildren()) {
                            MenuItem menuItem = item.getValue(MenuItem.class);
                            menuItem.setMenu(menu.child("name").getValue(String.class));
                            menus.get(i).add(menuItem);
                        }
                        i++;
                    }
                    afterMenuFetch(savedInstanceState);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                rlProgress_view.setVisibility(View.GONE);
            }
        });
    }

    public void afterMenuFetch (Bundle savedInstanceState) {

        mWizardModel = new MainMenu();
//        mWizardModel = new SandwichWizardModel(this);

        if (savedInstanceState != null) {
            mWizardModel.load(savedInstanceState.getBundle("model"));
        }

        mWizardModel.registerListener(this);

        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);

        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (mConsumePageSelectedEvent) {
                    mConsumePageSelectedEvent = false;
                    return;
                }

                mEditingAfterReview = false;
                updateBottomBar();
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPager.getCurrentItem() == mCurrentPageSequence.size() + 1) {
                    onPlaceOrder();
                } else {
                    if (mEditingAfterReview) {
                        mPager.setCurrentItem(mPagerAdapter.getCount() - 1);
                    } else{
                        mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                    }
                }
            }
        });

        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPager.setCurrentItem(mPager.getCurrentItem() - 1);
            }
        });

        onPageTreeChanged();
        updateBottomBar();
    }

    @Override
    public void onPageTreeChanged() {
        mCurrentPageSequence = mWizardModel.getCurrentPageSequence();
        mPagerAdapter.notifyDataSetChanged();
        updateBottomBar();
    }

    private void updateBottomBar() {
        int position = mPager.getCurrentItem();
        if (position == mCurrentPageSequence.size() + 1) {
            mNextButton.setText(R.string.finish);
            mNextButton.setBackgroundResource(R.drawable.finish_background);
        } else {
            mNextButton.setText(mEditingAfterReview
                    ? R.string.review
                    : R.string.next);
            mNextButton.setBackgroundResource(R.drawable.selectable_item_background);
            mNextButton.setEnabled(position != mCurrentPageSequence.size() + 1);
        }

        mPrevButton.setVisibility(position <= 0 ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWizardModel.unregisterListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle("model", mWizardModel.save());
    }

    @Override
    public AbstractWizardModel onGetModel() {
        return mWizardModel;
    }

    @Override
    public void onEditScreenAfterMenu(String key) {
        for (int i = mCurrentPageSequence.size() - 1; i >= 0; i--) {
            if (mCurrentPageSequence.get(i).getKey().equals(key)) {
                mConsumePageSelectedEvent = true;
                mEditingAfterReview = false;

                mPager.setCurrentItem(i + 1);
                updateBottomBar();
                break;
            }
        }
    }

    @Override
    public void onEditScreenAfterReview(String key) {
        for (int i = mCurrentPageSequence.size() - 1; i >= 0; i--) {
            if (mCurrentPageSequence.get(i).getKey().equals(key)) {
                mConsumePageSelectedEvent = true;
                mEditingAfterReview = true;
                mPager.setCurrentItem(i + 1);
                updateBottomBar();
                break;
            }
        }
    }

    @Override
    public void onPageDataChanged(Page page) {
        if (page.isRequired()) {
            mPagerAdapter.notifyDataSetChanged();
            updateBottomBar();
        }
    }

    @Override
    public Page onGetPage(String key) {
        return mWizardModel.findByKey(key);
    }

    private HashMap<String, Object> onBill() {
        HashMap<String, Object> bill =  new HashMap<>();
        HashMap<Integer, Object> itemOrder = new HashMap<>();
        bill.put("status", "paid");
        int totalPrice = 0;
        for (int i = 0; i < mCurrentPageSequence.size(); i++) {
            String object = mCurrentPageSequence.get(i).getData().getString(Page.SIMPLE_DATA_KEY);
            if (object != null && object.indexOf("-") > 0) {
                String name = object.substring(0, object.indexOf("-"));
                Integer price = Integer.parseInt(object.substring(object.indexOf(getString(R.string.rupee) + 1)));
                totalPrice = totalPrice + price;
                HashMap<String, Object> item = new HashMap<>();
                item.put("name", name);
                item.put("price", price);
                item.put("qty", 1);
                itemOrder.put(i, item);
            }
        }
        bill.put("total_price", totalPrice);
        bill.put("bill", itemOrder);
        return bill;
    }


    HashMap<String, Object> bill = new HashMap<>();
    private void onPlaceOrder() {
        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("ddMMyyyyHHmmss", Locale.ENGLISH);
        String formattedDate = df.format(c.getTime());

        bill.put(formattedDate, onBill());

        final UserLocalStore userLocalStore = new UserLocalStore(this);
         new android.support.v7.app.AlertDialog
                 .Builder(OrderScreen.this)
                 .setNegativeButton("Recheck", new DialogInterface.OnClickListener() {
                     @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                 })
                 .setTitle("Confirm Order")
                 .setMessage("Are you sure you want to place order?")
                 .setPositiveButton("Place", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(final DialogInterface dialog, int which) {
                         dialog.dismiss();
                         FirebaseDatabase.getInstance().getReference()
                                 .child("billing").child(outlet.getKey())
                                 .child(userLocalStore.getUid())
                                 .updateChildren(bill).addOnSuccessListener(new OnSuccessListener<Void>() {
                             @Override
                             public void onSuccess(Void aVoid) {
                                 dialog.dismiss();
                                 finish();
                             }
                         });
                     }
                }).create().show();
    }

    public class MyPagerAdapter extends FragmentStatePagerAdapter {
        private Fragment mPrimaryItem;

        MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            if (i == 0) return new MenuFragment();

            if (i > mCurrentPageSequence.size()) return new ReviewFragment();

            return mCurrentPageSequence.get(i -1).createFragment();
        }

        @Override
        public int getItemPosition(Object object) {
            // TODO: be smarter about this
            if (object == mPrimaryItem) {
                // Re-use the current fragment (its position never changes)
                return POSITION_UNCHANGED;
            }
            return POSITION_NONE;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            mPrimaryItem = (Fragment) object;
        }

        @Override
        public int getCount() {
            if (mCurrentPageSequence == null) {
                return 0;
            }
            return mCurrentPageSequence.size() + 2;
        }
    }

    public class MainMenu extends AbstractWizardModel {
        MainMenu() {
            super(OrderScreen.this);
        }

        @Override
        protected PageList onNewRootPageList() {
            PageList pageMenu = new PageList();

            for (List<MenuItem> menuItems : menus) {
                pageMenu.add(new MultipleFixedChoicePage(this, menuItems.get(0).getMenu())
                        .setChoices(menuItems));
            }

            return pageMenu;
        }
    }
}
