package com.example.uRemIndMe;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

public class SignUpInfoDialog extends DialogFragment {

    TextInputEditText name;
    Button submit;
    Button year,batch,branch;
    RadioGroup gender;

    int yearid = -1;
    int branchid = -1;
    int batchid = -1;
    int genderid = -1;

    TextView branchshow,yearshow,batchshow;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.dialog_signup_info,container,false);
        //Setting up toolbar

        Toolbar toolbar = rootview.findViewById(R.id.signup_info_dialog_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
                FirebaseAuth.getInstance().signOut();
            }
        });

        //Refrencing
        name= rootview.findViewById(R.id.signup_info_dialog_name);
        submit = rootview.findViewById(R.id.signup_info_dialog_submit);
        year = rootview.findViewById(R.id.signup_info_dialog_year);
        batch = rootview.findViewById(R.id.signup_info_dialog_batch);
        branch = rootview.findViewById(R.id.signup_info_dialog_branches);
        gender = rootview.findViewById(R.id.signup_info_dialog_gender);

        branchshow = rootview.findViewById(R.id.dialog_signup_info_branch_show);
        yearshow = rootview.findViewById(R.id.dialog_signup_info_year_show);
        batchshow = rootview.findViewById(R.id.dialog_signup_info_batch_show);

        //Loading Data




        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.signup_info_dialog_male: genderid = 1; break;
                    case R.id.signup_info_dialog_female: genderid = 2; break;
                }
            }
        });

        year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final CharSequence[] years = getResources().getStringArray(R.array.year_info);
                builder.setItems(years, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        yearid = which;
                        yearshow.setText(years[which]);
                    }
                });
                builder.show();
            }
        });
        branch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final CharSequence[] branches = Global.branches.toArray(new CharSequence[0]);
                builder.setItems(branches, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        branchid = which;
                        branchshow.setText(branches[which]);
                    }
                });
                builder.show();
            }
        });
        batch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final CharSequence[] batches = Global.batches.toArray(new CharSequence[0]);
                builder.setItems(batches, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        batchid = which;
                        batchshow.setText(batches[which]);
                    }
                });

                builder.show();
            }
        });






        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(getArguments()!=null){
                    getDialog().dismiss();
                    Global.documentData.userInfo.year = yearid;
                    Global.documentData.userInfo.batch = batchid;
                    Global.documentData.userInfo.name = name.getText().toString();
                    Global.documentData.userInfo.branch = branchid;
                    Global.documentData.userInfo.gender = genderid;

                    p.showData();


                }else{
                    FirebaseAuth mAuth =FirebaseAuth.getInstance();
                    FirebaseUser user = mAuth.getCurrentUser();
                    FirebaseFirestore db= FirebaseFirestore.getInstance();

                    Global.ModalClasses.UserInfoModal data = new Global.ModalClasses.UserInfoModal("",user.getUid(),"",name.getText().toString(),genderid,branchid,batchid,yearid);
                    Map<String,Global.ModalClasses.UserInfoModal>payLoad = new HashMap<>();
                    payLoad.put("userInfo",data);
                    db.collection("users").document(user.getUid()).set(payLoad).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            startActivity(new Intent(getActivity(),LoadScreen.class));
                        }
                    });
                }


            }
        });

        //Checking Edit profile

        if(getArguments() !=null){
            setupValues();
        }


        return rootview;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }


    private boolean isEmtpy(){

        if(yearid == -1){
            return true;
        }else if(branchid == -1){
            return true;
        }else if(batchid == -1){
            return true;
        }else if(genderid == -1){
            return true;
        }

        return false;

    }
    private Profile p;

    public SignUpInfoDialog(){

    }

    public SignUpInfoDialog(Profile profile){
        this.p = profile;
    }

    private void setupValues(){
        Global.ModalClasses.UserInfoModal data = Global.documentData.userInfo;
        name.setText(data.name);
        yearshow.setText(getResources().getStringArray(R.array.year_info)[data.year]);
        branchshow.setText(Global.branches.get(data.branch));
        batchshow.setText(Global.batches.get(data.batch));

    }
}
