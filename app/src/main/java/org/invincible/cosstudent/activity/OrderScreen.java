

package org.invincible.cosstudent.activity;

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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.invincible.cosstudent.R;
import org.invincible.cosstudent.misc.MenuItem;
import org.invincible.cosstudent.misc.Outlet;
import org.invincible.cosstudent.wizard.model.AbstractWizardModel;
import org.invincible.cosstudent.wizard.model.ModelCallbacks;
import org.invincible.cosstudent.wizard.model.MultipleFixedChoicePage;
import org.invincible.cosstudent.wizard.model.Page;
import org.invincible.cosstudent.wizard.model.PageList;
import org.invincible.cosstudent.wizard.ui.MenuFragment;
import org.invincible.cosstudent.wizard.ui.PageFragmentCallbacks;
import org.invincible.cosstudent.wizard.ui.ReviewFragment;

import java.util.ArrayList;
import java.util.List;

public class OrderScreen extends AppCompatActivity implements
        PageFragmentCallbacks,
        ReviewFragment.Callbacks,
        MenuFragment.Callbacks,
        ModelCallbacks {
    private ViewPager mPager;

    private MyPagerAdapter mPagerAdapter;

    private boolean mEditingAfterReview;

    private boolean mEditingAfterMenu;

    private AbstractWizardModel mWizardModel;

    private boolean mConsumePageSelectedEvent;

    private Button mNextButton;
    private Button mPrevButton;

    private List<Page> mCurrentPageSequence;
    private Outlet outlet;
    private ActionBar actionBar;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();

        Intent fromIntent = getIntent();
        outlet = (Outlet)fromIntent.getSerializableExtra("outlet");

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

        FirebaseDatabase.getInstance().getReference().child("restaurant")
                .child(outlet.getKey()).child("menu").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
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

        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
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
//        recalculateCutOffPage();
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
//            if (recalculateCutOffPage()) {
                mPagerAdapter.notifyDataSetChanged();
                updateBottomBar();
//            }
        }
    }

    @Override
    public Page onGetPage(String key) {
        return mWizardModel.findByKey(key);
    }

//    private boolean recalculateCutOffPage() {
//        // Cut off the pager adapter at first required page that isn't completed
//        int cutOffPage = mCurrentPageSequence.size() + 1;
//        for (int i = 0; i < mCurrentPageSequence.size(); i++) {
//            Page page = mCurrentPageSequence.get(i);
//            if (page.isRequired() && !page.isCompleted()) {
//                cutOffPage = i;
//                break;
//            }
//        }
//
//        if (mPagerAdapter.getCutOffPage() != cutOffPage) {
//            mPagerAdapter.setCutOffPage(cutOffPage);
//            return true;
//        }
//
//        return false;
//    }

    public class MyPagerAdapter extends FragmentStatePagerAdapter {
        private int mCutOffPage;
        private Fragment mPrimaryItem;

        public MyPagerAdapter(FragmentManager fm) {
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
//            return Math.min(mCutOffPage + 2, mCurrentPageSequence.size() + 2);
        }

//        public void setCutOffPage(int cutOffPage) {
//            if (cutOffPage < 0) {
//                cutOffPage = Integer.MAX_VALUE;
//            }
//            mCutOffPage = cutOffPage;
//        }

//        public int getCutOffPage() {
//            return mCutOffPage;
//        }
    }

    public class MainMenu extends AbstractWizardModel {
        public MainMenu() {
            super(OrderScreen.this);
        }

        @Override
        protected PageList onNewRootPageList() {
            PageList pageMenu = new PageList();

            for (List<MenuItem> menuItems : menus) {
                ArrayList<String> item = new ArrayList<>();
                for (MenuItem items: menuItems) {
                    item.add(items.getName() + " -  " + getString(R.string.rupee) + items.getPrice());
                }
                String submenuTitle = menuItems.get(0).getMenu();
                pageMenu.add(new MultipleFixedChoicePage(this, submenuTitle)
                        .setChoices(item));
            }

            return pageMenu;
        }
    }
}
