package com.example.uRemIndMe;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

public class PhoneAuthenticationDialog extends DialogFragment {

    TextInputEditText phone;
    OtpView otpView;
    Button submit;
    ProgressBar pb;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.phone_auth_dialog,container,false);
        //Setting up toolbar
        Toolbar toolbar = rootview.findViewById(R.id.phone_auth_dialog_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        //Refrencing
        phone = rootview.findViewById(R.id.phone_auth_dialog_phone);
        otpView =rootview.findViewById(R.id.phone_auth_dialog_otp);
        submit = rootview.findViewById(R.id.phone_auth_dialog_submit);
        pb = rootview.findViewById(R.id.phone_auth_dialog_progress);


        submit.setOnClickListener(submitCallback);


        otpView.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override
            public void onOtpCompleted(String otp) {
                if(otp.equals(sentOTP)){
                    loginWithPhoneCredentials(PhoneAuthProvider.getCredential(sentOTP,otp));
                }
            }
        });

        return rootview;
    }
    String sentOTP = "";

    private boolean isEmpty(){
        if(phone.getText().toString().equals("")){
            return true;
        }
        return false;
    }

    private void loginWithPhoneCredentials(PhoneAuthCredential credential){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                if(authResult.getAdditionalUserInfo().isNewUser()){
                    getDialog().dismiss();
                    SignUpInfoDialog dialog = new SignUpInfoDialog();
                    FragmentTransaction ft =getActivity().getSupportFragmentManager().beginTransaction();
                    dialog.show(ft,"hello");

                }else{
                    startActivity(new Intent(getActivity(),Home.class));
                    Toast.makeText(getActivity(), "Account Already Exists", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "The OTP Entered is Wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            loginWithPhoneCredentials(phoneAuthCredential);
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            sentOTP = s;
            otpView.setVisibility(View.VISIBLE);
            pb.setVisibility(View.GONE);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {

        }
    };

    private View.OnClickListener submitCallback  = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            pb.setVisibility(View.VISIBLE);
            String phonenumber = "+91"+phone.getText().toString();
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phonenumber,        // Phone number to verify
                    60,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    getActivity(),               // Activity (for callback binding)
                    mCallbacks);
        }
    };

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if(dialog!=null){
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            int width = ViewGroup.LayoutParams.MATCH_PARENT;

            dialog.getWindow().setLayout(height,width);
        }
    }
}
