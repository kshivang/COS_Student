package org.invincible.cosstudent.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.invincible.cosstudent.Fragment.SectionFragment;
import org.invincible.cosstudent.R;
import org.invincible.cosstudent.misc.UserLocalStore;
import org.invincible.cosstudent.misc.StudentModel;

import java.util.Locale;

/**
 * Created by kshivang on 01/10/16.
 **/
public class HomeScreen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private FirebaseAuth mAuth;
    private UserLocalStore userLocalStore;
    private StudentModel studentModel;
    private TextView profileTId, profileName;
    private ImageView profileImage;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        setContentView(R.layout.activity_home_screen);

        mAuth = FirebaseAuth.getInstance();
        studentModel = new StudentModel();
        userLocalStore = new UserLocalStore(HomeScreen.this);
        onClassSection(userLocalStore.getUid());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
            actionBar.setTitle("");

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);

            //Navigation Bar HeaderView
            View headerView = navigationView.inflateHeaderView(R.layout.nav_header_home);
            profileImage = (ImageView) headerView.findViewById(R.id.img_home_profile);
            profileName = (TextView) headerView.findViewById(R.id.text_home_name);
            profileTId = (TextView) headerView.findViewById(R.id.tid);

            headerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    drawer.closeDrawer(GravityCompat.START);
//                    startActivity(new Intent(HomeScreen.this, ProfileScreen.class));
                }
            });
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.primary_tabs);
        mViewPager = (ViewPager) findViewById(R.id.container);

        final String[] section = getResources().getStringArray(R.array.section);

        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                if (section[position] != null)
                    return SectionFragment.newInstance(section[position]);
                return null;
            }

            @Override
            public int getCount() {
                return section.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                if (section[position] != null) return section[position];
                return null;
            }
        };

        mViewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        drawer.closeDrawer(GravityCompat.START);
        navigationView.setCheckedItem(R.id.nav_header_home);

        switch (item.getItemId()) {
            case R.id.home:
                mViewPager.setCurrentItem(0, true);
//                startActivity(new Intent(HomeScreen.this, ProfileScreen.class));
                break;
            case R.id.profile:
//                startActivity(new Intent(HomeScreen.this, SchoolInfoScreen.class));
                break;
            case R.id.changePassword:
                break;
            case R.id.help:
//                startActivity(new Intent(HomeScreen.this, HelpScreen.class));
                break;
            case R.id.logout:
                onLogout();
                break;
        }
        return true;
    }


    private void onClassSection(String uid){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("students").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot != null){
                            StudentModel studentModel = dataSnapshot
                                    .getValue(StudentModel.class);
                            if (studentModel != null) {
                                userLocalStore.setStudentModel(studentModel);
                                updateHeader(studentModel);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    public StudentModel getStudentModel() {
        return studentModel;
    }

    private void onLogout() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Logout");
        builder.setMessage("Are your sure to logout?");
        builder.setIcon(getResources().getDrawable(R.drawable.ic_logout));
        builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mAuth.signOut();
                userLocalStore.clearUserData();
                userLocalStore.setFirstRun(false);
                finish();
                startActivity(new Intent(HomeScreen.this, LoginScreen.class));
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }


    private void updateHeader(StudentModel studentModel){
        if(studentModel == null) return;
        profileTId.setText(String.format(Locale.ENGLISH,"%d", studentModel.getRoll()));
        profileName.setText(String.format("%s", studentModel.getName()));
        Picasso.with(HomeScreen.this).load(studentModel.getImage_url())
                .error(R.drawable.placeholder_profile)
                .placeholder(R.drawable.placeholder_profile)
                .into(profileImage);
    }

}
