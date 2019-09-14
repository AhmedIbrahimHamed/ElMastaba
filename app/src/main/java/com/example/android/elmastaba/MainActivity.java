package com.example.android.elmastaba;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.android.elmastaba.fragments.AboutUsFragment;
import com.example.android.elmastaba.fragments.HomeFragment;
import com.example.android.elmastaba.fragments.MyChatRoomsFragment;
import com.example.android.elmastaba.fragments.MyProfileFragment;
import com.example.android.elmastaba.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity
implements NavigationView.OnNavigationItemSelectedListener{

    private FirebaseAuth mFirebaseAuth;
    private int mFragmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(

                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //displaying Home as the first fragment to appear to user.
        if (savedInstanceState == null){
            displaySelectedScreen(R.id.nav_home);
        }

        //Setting the name and email of the user to navigation drawer.
        setNameAndEmail(navigationView);

    }

    private void setNameAndEmail(NavigationView nvView){
        mFirebaseAuth = FirebaseAuth.getInstance();

        final TextView mUserNameTextView =  nvView.getHeaderView(0).findViewById(R.id.username_drawer);
        final TextView mUserEmailTextView =  nvView.getHeaderView(0).findViewById(R.id.email_drawer);

        //Get Current User from the firebase Auth.
        final FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        DatabaseReference mUserDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mFirebaseUser.getUid());
        mUserDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                //update name and email of the drawer by the current user information
                mUserNameTextView.setText(user.getmName());
                mUserEmailTextView.setText(mFirebaseUser.getEmail());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    // handel Item Selected in nav
    private void displaySelectedScreen(int itemId) {
        Fragment fragment = new HomeFragment();
        switch (itemId) {
            case R.id.nav_home:
                fragment = new HomeFragment();
                break;
            case R.id.nav_my_chat_rooms:
                fragment = new MyChatRoomsFragment();
                break;
            case R.id.nav_my_profile:
                fragment = new MyProfileFragment();
                break;
            case R.id.nav_about_us:
                fragment = new AboutUsFragment();
                break;
            case R.id.nav_logout:
                mFirebaseAuth.signOut();
                Intent intent = new Intent(this, SignInActivity.class);
                startActivity(intent);
                finish();
        }

        //replacing the fragment
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
        //Changing title of the activity.
        //saving the fragmentId.
        mFragmentId = itemId;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //calling the method displaySelectedScreen and passing the id of selected menu
        displaySelectedScreen(item.getItemId());
        //make this method blank
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(getString(R.string.fragment_id), mFragmentId);
        super.onSaveInstanceState(outState);
    }
}
