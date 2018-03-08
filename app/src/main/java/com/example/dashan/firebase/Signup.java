package com.example.dashan.firebase;

import android.app.Activity;
import android.content.Intent;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import org.w3c.dom.Text;

public class Signup extends Activity implements View.OnClickListener{
EditText editTextemail,editTextpassword,editTextconfpass;
private FirebaseAuth mAuth;
//Button signup,SignIn;
ProgressBar progressBar;
private static final String TAG="Mylogs";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        editTextemail=(EditText) findViewById(R.id.editsignemail);
        editTextpassword=(EditText) findViewById(R.id.editTextPassword);
       // signup =(Button) findViewById(R.id.buttonSignUp);
        //SignIn=(Button) findViewById(R.id.login_button);
        editTextconfpass=(EditText) findViewById(R.id.conformpassword);
        progressBar=(ProgressBar) findViewById(R.id.progressbar);
        mAuth=FirebaseAuth.getInstance();
        //progressBar.setVisibility(View.GONE);
        findViewById(R.id.buttonSign_up).setOnClickListener(this);
        findViewById(R.id.textViewLogin_ja).setOnClickListener(this);



    }
private void Registeruser(){
        String email=editTextemail.getText().toString().trim();
        String password=editTextpassword.getText().toString().trim();
        String confpass=editTextconfpass.getText().toString().trim();
        System.out.println("In to method reguser:");
        if(email.length()==0){
            editTextemail.setError("Email is Required Field");
            editTextemail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextemail.setError("please enter a valid Email");
            editTextemail.requestFocus();
            return;
        }
        if(password.length()==0){
            editTextpassword.setError("password is required");
            editTextpassword.requestFocus();
            return;
        }
        if(password.length()<6){
            editTextpassword.setError("minimum length should be greater than 6");
            editTextpassword.requestFocus();
            return;
        }
        if(confpass.equals(password)) {
            System.out.println("conform");
            //progressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    try {
                        if (task.isSuccessful()) {
                            finish();
                            Toast.makeText(getApplicationContext(), "User Register Successful", Toast.LENGTH_SHORT).show();
                            // finish();
                            progressBar.setVisibility(View.INVISIBLE);
                            Intent i = new Intent(getApplicationContext(), UserProfile.class);
                            startActivity(i);
                            //startActivity(new Intent(Signup.this, UserProfile.class));
                        } else {

                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(getApplicationContext(), "You are already registered", Toast.LENGTH_SHORT).show();
                                // return;

                            } else {
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                //return;
                            }

                        }
                    }catch (Exception e){
                        Log.e(TAG,"Error in signUp");
                    }
                }
            });
        }
        else{
            editTextconfpass.setError("password should be same");
            editTextconfpass.requestFocus();
           // return;
        }
}
@Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.buttonSign_up: {
                //Thread th = new Thread() {
                //   @Override
              // System.out.println("calling method reguser");
                Registeruser();


                //th.start();

                break;
            }

            case R.id.textViewLogin_ja:{
                finish();
                startActivity(new Intent(getApplicationContext()
                        ,MainActivity.class));
                break;
            }
        }
    }
}
