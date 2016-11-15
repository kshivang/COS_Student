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
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
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
import org.invincible.cosstudent.misc.StudentModel;
import org.invincible.cosstudent.misc.UserLocalStore;

import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static org.invincible.cosstudent.R.array.section;

/**
 * Created by kshivang on 01/10/16.
 **/
public class HomeScreen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private FirebaseAuth mAuth;
    private UserLocalStore userLocalStore;
    private TextView profileTId, profileName;
    private CircleImageView profileImage;
    private ViewPager mViewPager;
    @Override
    protected void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        setContentView(R.layout.activity_home_screen);

        mAuth = FirebaseAuth.getInstance();
        userLocalStore = new UserLocalStore(HomeScreen.this);
        onClassSection(userLocalStore.getUid());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
            actionBar.setTitle("Home");

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
            NavigationView headerView = (NavigationView) navigationView
                    .inflateHeaderView(R.layout.nav_header_home);
            if (headerView!= null) {
                profileImage = (CircleImageView) headerView.findViewById(R.id.img_profile);
                profileName = (TextView) headerView.findViewById(R.id.text_name);
                profileTId = (TextView) headerView.findViewById(R.id.tid);
            }

            headerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    drawer.closeDrawer(GravityCompat.START);
//                    startActivity(new Intent(HomeScreen.this, ProfileScreen.class));
                }
            });
        }

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.primary_tabs);
        mViewPager = (ViewPager) findViewById(R.id.container);

        final String[] sections = getResources().getStringArray(section);

        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                if (position < sections.length) return SectionFragment.newInstance(sections[position]);
                return null;
            }

            @Override
            public int getCount() {
                return sections.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                if (sections[position] != null) return sections[position];
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
//            case R.id.profile:
////                startActivity(new Intent(HomeScreen.this, SchoolInfoScreen.class));
//                break;
            case R.id.changePassword:
                startActivity(new Intent(HomeScreen.this, ChangePasswordScreen.class));
                break;
            case R.id.help:
                startActivity(new Intent(HomeScreen.this, HelpScreen.class));
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

    private void onLogout() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Logout");
        builder.setMessage("Are your sure to logout?");
        builder.setIcon(ContextCompat.getDrawable(HomeScreen.this, R.drawable.ic_logout));
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