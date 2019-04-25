package com.example.cc.firebasechat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 100;

    SignInButton signInButton;
    GoogleApiClient googleApiClient;

    EditText mEmail, mPassword;

    ProgressDialog pd;
    SharedPreferences sp;


    //Firebase
    FirebaseDatabase mDatabase;


    FirebaseAuth.AuthStateListener mAuthListner;


    DatabaseReference mReference;
    StorageReference mstorage;

    FirebaseAuth firebaseAuth;

    LoginButton loginButton;
    Button mLogin;

    CallbackManager mCallbackManager;
    String strEmail,strPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sp=getSharedPreferences("login",MODE_PRIVATE);

        if(sp.contains("email") && sp.contains("password")){
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();   //finish current activity
        }


        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase=FirebaseDatabase.getInstance();


        mReference = FirebaseDatabase.getInstance().getReference("Users");  //We get the reference of our database
        mstorage= FirebaseStorage.getInstance().getReference();



        mLogin = (Button) findViewById(R.id.login);
        mEmail = (EditText) findViewById(R.id.login_et_email);
        mPassword = (EditText) findViewById(R.id.login_et_password);

        firebaseAuth = FirebaseAuth.getInstance();
        pd=new ProgressDialog(this);

        signInButton = (SignInButton) findViewById(R.id.googleSingin);

        singIngso();
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                google_sing_in();
            }
        });


        setUpFacebook();

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

                emailSingin();
            }
        });


        mAuthListner=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
          FirebaseUser user=firebaseAuth.getCurrentUser();
          if (user!=null)
          {
              Log.d(TAG,"onAuthStateChanged :singed in");
          }
          else
          {
              Log.d(TAG,"onAuthStatChanged :singed out");
          }


            }
        };
    }



    private void emailSingin()
    {


        strEmail=mEmail.getText().toString();
        strPassword=mPassword.getText().toString();

        pd.setMessage("Please Wait....");
        pd.show();





        if (strEmail.equals("")||strPassword.equals(""))
        {

            Toast.makeText(this, "Please enter Email and password", Toast.LENGTH_SHORT).show();
            pd.dismiss();
        }
        else {

            firebaseAuth.signInWithEmailAndPassword(strEmail, strPassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                              //  intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                Toast.makeText(LoginActivity.this, "Login Succefully", Toast.LENGTH_SHORT).show();
                                pd.dismiss();

                            } else {

                                Toast.makeText(LoginActivity.this, "Please enter correct email\n and password", Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }

                        }
                    });
        }

    }


    private void setUpFacebook() {

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.facebook_login_button);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // ...
            }
        });
// ...
//        @Override
//        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        // Pass the activity result back to the Facebook SDK
//        mCallbackManager.onActivityResult(requestCode, resultCode, data);
//    }
    }


    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                        // ...
                    }
                });
    }


    /*

    Google sing in


     */
    private void singIngso() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    private void google_sing_in() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if(result.isSuccess())
            {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);

            }


        } else {

            // Google Sign In failed, update UI appropriately
            Log.w(TAG, "Google sign in failed");
            // ...
        }
    }


    private void firebaseAuthWithGoogle(final GoogleSignInAccount account) {

        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            mReference.child("Users")   //GoogleSinging se jo data ayega usko server pr leker jayenge
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {  //Facebook ka bhi aise hi hoga

                                            String userId=firebaseAuth.getCurrentUser().getUid();
                                            DatabaseReference gDatabase=mReference.child(userId);  //Yaani userid ke ander data create hoga

                                            gDatabase.child("name").setValue(account.getDisplayName());  //Phele hum key value pair me data ko khud dalte the per isme apne aap get ker lenge
                                            gDatabase.child("email").setValue(account.getEmail());
                                            gDatabase.child("username").setValue(account.getGivenName());
                                            gDatabase.child("status").setValue("available");
                                            gDatabase.child("userId").setValue(userId);

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });



                            startActivity(new Intent(LoginActivity.this, MainActivity.class));

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Auth Failed", Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthListner);

    }

}
