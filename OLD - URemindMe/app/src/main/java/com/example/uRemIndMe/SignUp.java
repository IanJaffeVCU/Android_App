package com.example.uRemIndMe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignUp extends AppCompatActivity {

    ImageButton google,phone;
    Button submit;

    TextInputEditText username,password;
    ContentLoadingProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Setting up Toolbar
//        Toolbar toolbar = findViewById(R.id.signup_toolbar);
//        setSupportActionBar(toolbar);

        //Refrencing
        username = findViewById(R.id.signup_username);
        password = findViewById(R.id.register_password);
        google = findViewById(R.id.signup_google);
        phone = findViewById(R.id.signup_phone);
        submit = findViewById(R.id.signup_submit);
        pb = findViewById(R.id.prog);

        submit.setOnClickListener(submitCallback);
        google.setOnClickListener(googleCallback);
        phone.setOnClickListener(phoneCallback);


    }

    private View.OnClickListener submitCallback = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(isEmpty()){
                Toast.makeText(SignUp.this, "Please fill all the entries", Toast.LENGTH_SHORT).show();
            }else{
                String email = username.getText().toString()+"@collegemate.com";
                String pass = password.getText().toString();
                final FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.createUserWithEmailAndPassword(email,pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        SignUpInfoDialog dialog = new SignUpInfoDialog();
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        dialog.show(ft,"hello");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUp.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }
    };

    private boolean isEmpty(){
        if(username.getText().toString().equals("")){
            return  true;
        }else if(password.getText().toString().equals("")){
            return true;
        }
        return false;
    }
    int RC_SIGN_IN = 100;
    private View.OnClickListener googleCallback = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(Global.webClientId)
                    .requestEmail()
                    .build();
            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(SignUp.this, gso);
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    };

    private View.OnClickListener phoneCallback = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PhoneAuthenticationDialog dialog = new PhoneAuthenticationDialog();
            dialog.show(getSupportFragmentManager().beginTransaction(),"hello");
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account){
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        pb.setVisibility(View.VISIBLE);
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                pb.setVisibility(View.GONE);
                if(task.getResult().getAdditionalUserInfo().isNewUser()){
                    SignUpInfoDialog dialog = new SignUpInfoDialog();
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    dialog.show(ft,"hello");
                }else{
                    startActivity(new Intent(SignUp.this,Home.class));
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();

    }
}
