package com.example.cc.firebasechat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Tag;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    ImageView back_btn;
    CircleImageView profile_image;
    EditText mName, mEmail, mUserName, mPassword;
    Button mRegister;


    RadioGroup mGender;
    RadioButton mGenderOption;

//    String strEmail,strPassword;


    FirebaseDatabase mDatabase;
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener mAuthListner;

    StorageReference mstorage;
    DatabaseReference mReference;
    ProgressDialog progressDialog;

    String strGender;

    String strProfile_image;



    private static final int request_code=5;

    Uri imageUri;

    String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        progressDialog=new ProgressDialog(this);


        firebaseAuth = FirebaseAuth.getInstance();


        mReference = FirebaseDatabase.getInstance().getReference("Users");  //We get the reference of our database
        mstorage= FirebaseStorage.getInstance().getReference();


        profile_image = (CircleImageView) findViewById(R.id.profile_image);
        mEmail = (TextInputEditText) findViewById(R.id.et_email);
        mUserName = (TextInputEditText) findViewById(R.id.et_name);
        mName = (TextInputEditText) findViewById(R.id.et_username);
        mPassword = (TextInputEditText) findViewById(R.id.et_password);
        mRegister = (Button) findViewById(R.id.singup);
        back_btn = (ImageView) findViewById(R.id.back_image);
        mGender = (RadioGroup) findViewById(R.id.radio_gp);
        mGenderOption=(RadioButton)findViewById(R.id.radio_male);


//
//        mGenderOption = (RadioButton) findViewById(mGender.getCheckedRadioButtonId());
//
//        strGender=mGenderOption.getText().toString();


        mGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId)
                {
                    case R.id.radio_male:
                        strGender="Male";

                        Toast.makeText(RegisterActivity.this, "Selected Male", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.radio_female:
                        strGender="Female";
                        Toast.makeText(RegisterActivity.this, "Selected Female", Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent=new Intent();
               intent.setAction(Intent.ACTION_GET_CONTENT);
               intent.setType("image/*");
               startActivityForResult(intent,request_code);
            }
        });


        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = mUserName.getText().toString();
                String strEmail = mEmail.getText().toString();
                String username = mName.getText().toString();
                String strPass = mPassword.getText().toString();


                progressDialog.setMessage("Please Wait...");
                progressDialog.show();


                if (TextUtils.isEmpty(name))
                {
                    Toast.makeText(RegisterActivity.this, "Please Enter Name", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                }
                else if (TextUtils.isEmpty(strEmail))
                {
                    Toast.makeText(RegisterActivity.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
                else if (TextUtils.isEmpty(username))
                {
                    Toast.makeText(RegisterActivity.this, "Please Enter Username", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }

                else {

                    id = mReference.push().getKey();

                    Users users = new Users(name, strEmail, username, strPass, id, strGender, profile_image);

                    assert id != null;
                    mReference.child(id).setValue(users);

                    Toast.makeText(RegisterActivity.this, "Users Data Added", Toast.LENGTH_SHORT).show();


                    firebaseAuth.createUserWithEmailAndPassword(strEmail, strPass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {

                                        Toast.makeText(RegisterActivity.this, "Registration succefully...", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                        upload_image();
                                    } else {

                                        Toast.makeText(RegisterActivity.this, "Registration failed...", Toast.LENGTH_SHORT).show();

                                    }

                                }
                            });

                }


            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==request_code&&resultCode==RESULT_OK&&data!=null&&data.getData()!=null)
        {
            imageUri=data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                profile_image.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }


    private void upload_image()
    {
        if (imageUri!=null)
        {
            StorageReference imagePath=mstorage.child(getString(R.string.users)).child(id).child(imageUri.getLastPathSegment()); // User image ko kha se kha tak yaani kis folder tak leker jana he
            imagePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(RegisterActivity.this, "Uploaded image", Toast.LENGTH_SHORT).show();
               String image_firebase_uri=mstorage.getDownloadUrl().toString();

               strProfile_image=image_firebase_uri;
             //  mReference.child(getString(R.string.users)).child(id).child("profile_image").setValue(strProfile_image);
                    mReference.child(id).child("Profile_Image").setValue(strProfile_image);




                }
            });
        }
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radio_male:
                if (checked)
                    strGender = "Male";
                break;
            case R.id.radio_female:
                if (checked)
                    strGender = "Female";
                break;
        }
    }
//            public void setupfirebaseAuthentication()
//            {
//                firebaseAuth=FirebaseAuth.getInstance();
//
//               mAuthListner=new FirebaseAuth.AuthStateListener() {
//                   @Override
//                   public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//
//                       FirebaseUser user=firebaseAuth.getCurrentUser();
//
//                       if (user!=null)
//                       {
//                           String  userid=firebaseAuth.getCurrentUser().getUid();
//                           mReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                               @Override
//                               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                               }
//
//                               @Override
//                               public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                               }
//                           });
//
//
//
//
//
//                       }
//
//                   }
//               };
//            }


    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onStop() {
        super.onStop();


    }
}
