package com.example.dashan.firebase;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class UserProfile extends AppCompatActivity {
ImageView imageView;
EditText editText;
TextView textView;
FirebaseAuth mAuth;
ProgressBar probar;
Button Logout,saveinfo;
Uri uriprofileimage;
String ProfileImageURL;
private static final int CHOOSE_IMAGE=101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        mAuth=FirebaseAuth.getInstance();
        editText=(EditText) findViewById(R.id.edittextDisplayname);
        imageView=(ImageView) findViewById(R.id.image_view);
        textView=(TextView) findViewById(R.id.textviewverified);
        probar=(ProgressBar) findViewById(R.id.probaruser);
        Logout=(Button) findViewById(R.id.logout);
       // saveinfo=(Button) findViewById(R.id.buttonSave);
        imageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
            ShowImageChooser();
            }
        });
        Logout.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                 finish();
                FirebaseAuth.getInstance().signOut();
                //Auth.GoogleSignInApi.signOut()
                startActivity(new Intent(getApplicationContext(),MainActivity.class));

            }
        });
        loadUserInformation();
        findViewById(R.id.buttonSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            saveuser();
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    private void loadUserInformation() {
        final FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            if (user.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(user.getPhotoUrl().toString())
                        .into(imageView);
            }

            if (user.getDisplayName() != null) {
                editText.setText(user.getDisplayName());
            }

            if (user.isEmailVerified()) {
                textView.setText("Email Verified");
            } else {
                textView.setText("Email Not Verified (Click to Verify)");
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(UserProfile.this, "Verification Email Sent", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }
    }
private void saveuser(){
        String dispname=editText.getText().toString().trim();
        if(dispname.isEmpty()){
            editText.setError("name Reuqired");
            editText.requestFocus();
            return;
        }
    FirebaseUser user=mAuth.getCurrentUser();
        if(user!=null && ProfileImageURL!=null){
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(dispname)
                    .setPhotoUri(Uri.parse(ProfileImageURL))
                    .build();

            user.updateProfile(profile)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(UserProfile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CHOOSE_IMAGE && resultCode==RESULT_OK && data!=null && data.getData()!=null){
         uriprofileimage=data.getData();
            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),uriprofileimage);
                imageView.setImageBitmap(bitmap);
                uploadimageTofirebase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void uploadimageTofirebase(){
        StorageReference profileimage= FirebaseStorage.getInstance().getReference("profilepics/" + System.currentTimeMillis() + ".jpg");
        if (uriprofileimage != null) {
            //progressBar.setVisibility(View.VISIBLE);
            profileimage
                    .putFile(uriprofileimage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                           // progressBar.setVisibility(View.GONE);
                            ProfileImageURL= taskSnapshot.getDownloadUrl().toString();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //progressBar.setVisibility(View.GONE);
                            Toast.makeText(UserProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
    private void ShowImageChooser(){
        Intent i=new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i,"Select profile Image"),CHOOSE_IMAGE);
    }
}
