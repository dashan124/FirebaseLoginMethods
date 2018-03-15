package com.example.dashan.firebase;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener{
FirebaseAuth mAuth;
EditText editTextEmail,Edittextpassword;
ProgressBar probar;

FirebaseAuth.AuthStateListener mAuthListener;
private SignInButton signin;
private Button mobileLogin;
//private Button facebookLogin;


private GoogleApiClient googleApiClient;
private static final int REQ_CODE=9001;

private String TAG="TAG";
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
       /* if(mAuth.getCurrentUser()!=null){
            finish();
            startActivity(new Intent(this,UserProfile.class));

        }*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Edittextpassword=(EditText)findViewById(R.id.edittextpassowrd);
        editTextEmail=(EditText) findViewById(R.id.editTextEmail);


        findViewById(R.id.textViewSignup).setOnClickListener(this);
        findViewById(R.id.login_button).setOnClickListener(this);
        findViewById(R.id.forgotpass).setOnClickListener(this);
        mobileLogin=(Button) findViewById(R.id.mobile_login_btn);
        mobileLogin.setOnClickListener(this);
        //facebookLogin=(Button) findViewById(R.id.facebook_login);

        signin=(SignInButton) findViewById(R.id.google_login);


       // findViewById(R.id.google_login).setOnClickListener(this);
        mAuth=FirebaseAuth.getInstance();

        signin.setOnClickListener(this);
        //MobileLogin.setOnClickListener(this);
        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null){
                    Intent i=new Intent(MainActivity.this,UserProfile.class);
                    startActivity(i);
                }

            }
        };
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient=new GoogleApiClient.Builder(this).enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
              Toast.makeText(MainActivity.this,"Something Went Wrong",Toast.LENGTH_SHORT).show();
            }
        }).addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();


    }
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, REQ_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQ_CODE){
            GoogleSignInResult result=Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){
                GoogleSignInAccount account=result.getSignInAccount();
                if(account!=null) {
                    //Take all data You Want
                    String identifier = account.getId()+"";
                    String displayName = account.getDisplayName()+"";
                    //After You got your data add this to clear the priviously selected mail
                    googleApiClient.clearDefaultAccountAndReconnect();
                    firebaseAuthWithGoogle(account);
                }
            }else Log.e("handleSignInResult","Failed ; "+result.getStatus());
            }
            else{
                Toast.makeText(MainActivity.this,"Authication  Went Wrong",Toast.LENGTH_SHORT).show();
            }

    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            //FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            //Snackbar.make(findViewById(R.id.ac), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }


    private void Login(){

        String email=editTextEmail.getText().toString().trim();
        String password=Edittextpassword.getText().toString().trim();
        if(email.isEmpty()){
            editTextEmail.setError("Email is Required");
            editTextEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Please enter a valid Email");
            editTextEmail.requestFocus();
            return;
        }
        if(password.isEmpty()){
            Edittextpassword.setError("Password is Required");
            Edittextpassword.requestFocus();
            return;
        }
        if(password.length()<6){
            Edittextpassword.setError("password is Too short");
            Edittextpassword.requestFocus();
            return;
        }
       // probar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    finish();
                Intent i=new Intent(getApplicationContext(),UserProfile.class);
                    Toast.makeText(getApplicationContext(), "User Login Successful", Toast.LENGTH_SHORT).show();
                //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
                else{
                    try {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
}


    @Override
    public void onClick(View v) {

    switch (v.getId()){
        case R.id.textViewSignup:{
            //startActivity(new Intent(this,Signup.class));
            finish();
            Intent i=new Intent(this,Signup.class);
            startActivity(i);
            break;
        }
        case R.id.login_button:{
           // finish();
            Login();
            break;
        }
        case R.id.forgotpass:{
            finish();
            Intent i=new Intent(this,ResetPassowrd.class);
            startActivity(i);
            break;
        }
        case R.id.google_login:{
            //finish();
            signIn();
            break;
        }

        case R.id.mobile_login_btn:{
            finish();
            Intent i=new Intent(this,MobileNumberLogin.class);
            startActivity(i);
            break;
        }
    }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }
}
