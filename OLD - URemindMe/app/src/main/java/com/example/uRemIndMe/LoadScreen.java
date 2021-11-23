package com.example.uRemIndMe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class LoadScreen extends AppCompatActivity {
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_screen);

        //Refrencing
        pb = findViewById(R.id.loadscreen_pb);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Global.userFieldRef = db.collection("commonData").document("userFields");
        Global.pollRef = db.collection("commonData").document("polls");
        Global.userFieldRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Global.branches = (List<String>) task.getResult().get("branches");
                Global.batches = (List<String>) task.getResult().get("batches");

                if (isUserExists()){
                    loadDataAndStartActivity();
                    Toast.makeText(LoadScreen.this, "user Exists", Toast.LENGTH_SHORT).show();
                }else{
                    startActivity(new Intent(LoadScreen.this,HomeActivity.class));
                    finish();
                }

            }
        });



        //Check User Existence



    }

    public void loadDataAndStartActivity(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Global.userRef = db.collection("users").document(FirebaseAuth.getInstance().getUid());
        Global.userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult()!=null){
                    Global.documentData = task.getResult().toObject(Global.UserData.class);
                    loadpolls();
                    startActivity(new Intent(LoadScreen.this,Home.class));
                    finish();
                }else{
                    Toast.makeText(LoadScreen.this, "Please connect to Internet", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    private void loadpolls(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Global.pollRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult() !=null){
                    Global.polls = (ArrayList<Global.ModalClasses.PollModal>) task.getResult().get(Global.documentData.userInfo.batch.toString());
                }
            }
        });

    }

    public boolean isUserExists(){
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            return true;
        }
        return false;
    }
}
