package com.codewithdevesh.letsgossip.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codewithdevesh.letsgossip.R;
import com.codewithdevesh.letsgossip.model.UserModel;
import com.codewithdevesh.letsgossip.utilities.SessionManagement;
import com.codewithdevesh.letsgossip.utilities.Utils;
import com.codewithdevesh.letsgossip.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

import soup.neumorphism.NeumorphButton;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private DatabaseReference reference;
    private FirebaseUser user;
    private String phoneNo;
    private Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this, R.layout.activity_login);
        Utils.setStatusBarColor(this,R.color.light_background);
        SessionManagement.init(LoginActivity.this);
        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        listeners();

    }

    private void listeners() {
        if(user!=null){
            Intent i = new Intent(getApplicationContext(), UserInfoActivity.class);
            startActivity(i);
            finish();
            overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
        }
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setCancelable(false);
        binding.bttnSendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateNumber()) {
                    return;

                } else {
                    String countryCode = binding.ccp.getSelectedCountryCode();
                    String number = binding.etPhoneNo.getText().toString();
                    phoneNo = "+"+countryCode + number;

                    Log.e("TAG","phoneNo"+phoneNo);
                    startPhoneNumberVerification(phoneNo);

                    dialog = new Dialog(LoginActivity.this);
                    dialog.setContentView(R.layout.dialog_otp);
                    dialog.setCancelable(false);
                    dialog.show();

                    EditText etOtp = dialog.findViewById(R.id.et_otp);
                    NeumorphButton button = dialog.findViewById(R.id.bttn_verifyOtp);
                    TextView resendOtp = dialog.findViewById(R.id.tv_resendOTP);

                        Log.e("TAG","working");
                        etOtp.setError(null);
                        resendOtp.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                               resendVerificationCode(phoneNo,mResendToken);
                            }
                        });
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String x = etOtp.getText().toString();
                                if(x.isEmpty()|| x.length()<6){
                                    etOtp.setError("otp cant be blank or <6");
                                    return;
                                }else {
                                    etOtp.setError(null);
                                    Log.e("TAG", "button working");
                                    progressDialog.setMessage("Verifying...");
                                    progressDialog.show();
                                    verifyPhoneNumberWithCode(mVerificationId, x);
                                }
                            }
                        });
                    }

                }


        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredentials(phoneAuthCredential);
                Toast.makeText(getApplicationContext(), "Verification Successful", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(getApplicationContext(), "Verification Failed:" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                mVerificationId = s;
                mResendToken = forceResendingToken;
                binding.ccp.setEnabled(false);
                binding.etPhoneNo.setEnabled(false);
                progressDialog.dismiss();
            }
        };
    }

    public void resendVerificationCode(String phoneNumber,
                                       PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                LoginActivity.this,           //a reference to an activity if this method is in a custom service
                mCallbacks,
                token);        // resending with token got at previous call's `callbacks` method `onCodeSent`
        // [END start_phone_auth]
    }


    private void verifyPhoneNumberWithCode(String verificationId,String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,code);
        signInWithPhoneAuthCredentials(credential);
    }

    private boolean validateNumber(){
        String x = binding.etPhoneNo.getText().toString();
        if(x.isEmpty() || x.length()<10){
            binding.etPhoneNo.setError("Enter valid Phone No");
            return false;
        }else{
            binding.etPhoneNo.setError(null);
            return true;
        }
    }

    private void startPhoneNumberVerification(String phoneNumber){
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredentials(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Log.e("TAG","phone num"+ phoneNo);
                            SessionManagement.saveUserPhoneNo(phoneNo);
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
                            reference.child(phoneNo).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    UserModel model = snapshot.getValue(UserModel.class);
                                    String num = model.getPhoneNo();
                                    String name = model.getName();
                                    String bio = model.getBio();
                                    String pic = model.getPhotoUri();

                                    SessionManagement.saveUserPhoneNo(num);
                                    SessionManagement.saveUserName(name);
                                    SessionManagement.saveUserBio(bio);
                                    SessionManagement.saveUserPic(pic);

                                    if(num.equals(phoneNo)){
                                        startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                                    }else{
                                        startActivity(new Intent(LoginActivity.this,UserInfoActivity.class));
                                    }
                                    overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                        } else {
                            progressDialog.dismiss();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(getApplicationContext(), "Error in code", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        dialog.dismiss();
    }
}