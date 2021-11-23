package com.example.uRemIndMe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
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

public class HomeActivity extends AppCompatActivity {

    TextInputEditText username,password;
    Button submit;
    TextView signup;
    ImageView google,otp;
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setting up the toolbar

        username = findViewById(R.id.home_username);
        password =findViewById(R.id.home_password);
        submit = findViewById(R.id.login_btn);
        signup = findViewById(R.id.home_new);
        google = findViewById(R.id.home_google);
        pb = findViewById(R.id.progressBarlogin);
        otp = findViewById(R.id.home_otp);

        //Button Listeners

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,SignUp.class));
            }
        });
        submit.setOnClickListener(submitCallback);
//        otp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PhoneAuthenticationDialog dialog = new PhoneAuthenticationDialog();
//                dialog.show(getSupportFragmentManager().beginTransaction(),"hello");
//            }
//        });

   //     google.setOnClickListener(googlebuttonlistener);


    }
    int RC_SIGN_IN =100;
    private View.OnClickListener googlebuttonlistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(Global.webClientId)
                    .requestEmail()
                    .build();
            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(HomeActivity.this, gso);
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    };

    private boolean isEmpty(){
        if(username.getText().equals("")){
            return true;
        }else if(password.getText().equals("")){
            return true;
        }

        return false;

    }

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
                //
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
                    startActivity(new Intent(HomeActivity.this,LoadScreen.class));
                }
            }
        });
    }

    private View.OnClickListener submitCallback = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(isEmpty()){
                Toast.makeText(HomeActivity.this, "Please fill all the entries", Toast.LENGTH_SHORT).show();
            }else{
                String email = username.getText().toString()+"@collegemate.com";
                String pass = password.getText().toString();

                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signInWithEmailAndPassword(email,pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        startActivity(new Intent(HomeActivity.this,LoadScreen.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }
    };
}
