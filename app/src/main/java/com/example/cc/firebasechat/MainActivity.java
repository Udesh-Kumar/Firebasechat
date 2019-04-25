package com.example.cc.firebasechat;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    Context context = MainActivity.this;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase mDatabase;
    FirebaseAuth.AuthStateListener mAuthListner;
    DatabaseReference mReference;
    StorageReference mstorage;
    TabLayout tabLayout;
    android.support.v7.widget.Toolbar toolbar;
    ViewPager viewPager;
    ImageView mSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();


        mSearch = findViewById(R.id.image_search);
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tablayout);


        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SearchActivity.class));
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.settings) {
           startActivity(new Intent(context,Account_Setting_activity.class));
        }

        if (item.getItemId() == R.id.log_out) {
            user_logout();
        }
        return true;
    }

    private void user_logout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(context, HomeActivity.class));
        finish();
        Log.d(TAG, "user logout : User signout");

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            user_logout();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();

    }
}

