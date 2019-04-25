package com.example.cc.firebasechat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Account_Setting_activity extends AppCompatActivity {

    private static final String TAG = "Account_Setting_activit";

    ImageView mBack, mUpdate;
    CircleImageView mProfile_image;
    TextView mName, mUsername, mMobile, mCountry, mstatus;
    TextView mChange_image;

    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener mAuthListner;

    DatabaseReference mReference;
    StorageReference mstorage;
    Context mContext = Account_Setting_activity.this;

    FirebaseDatabase mDatabase;

    String user_id;

    FirebaseUser user;

    private static final int request_code = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account__setting_activity);

        firebaseAuth = FirebaseAuth.getInstance();

        user=firebaseAuth.getCurrentUser();

        if (user!=null)
        {
            user_id=user.getUid();

        }



        mDatabase = FirebaseDatabase.getInstance();
        mReference = FirebaseDatabase.getInstance().getReference();
        mstorage = FirebaseStorage.getInstance().getReference();


        user_id = firebaseAuth.getUid();
        System.out.println(user_id);

        mBack = findViewById(R.id.imageview_back_btn);
        mUpdate = findViewById(R.id.update_done);
        mProfile_image = findViewById(R.id.update_profile_image);
        mName = (TextView) findViewById(R.id.name);
        mUsername = findViewById(R.id.Username);
        mMobile = findViewById(R.id.Mobile);
        mCountry = findViewById(R.id.country);
        mstatus = findViewById(R.id.change_status);
        mChange_image = findViewById(R.id.change_image);


        get_User_profile_data();

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Back to Main Activity");
                finish();
            }
        });

        mProfile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, request_code);
            }
        });

        mChange_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, request_code);
            }
        });


    }

    /*
     * get user data from firebase server
     *
     *
     * */

    private void get_User_profile_data() {

        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                String name=dataSnapshot.child("Usres").child(user_id).getValue(String.class);

                mName.setText(name);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        }
    @Override
    protected void onStart() {
        super.onStart();
//        firebaseAuth.addAuthStateListener(mAuthListner);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (mAuthListner != null) {
//            firebaseAuth.removeAuthStateListener(mAuthListner);
//        }
    }
}
