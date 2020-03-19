package com.example.testing2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    EditText phno1,otp1;
    Button getotp1,submit1;
    FirebaseAuth mAuth;
    String codeSent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();
        getotp1=(Button)findViewById(R.id.getotp);
        submit1=(Button)findViewById(R.id.submit);
        phno1=(EditText)findViewById(R.id.phno);
        otp1=(EditText)findViewById(R.id.otp);

        getotp1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendVerificationCode();
            }
        });

        submit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifySignInCode();
            }
        });
    }
    private void verifySignInCode()
    {
        String code=otp1.getText().toString().trim();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, code);
        signInWithPhoneAuthCredential(credential);
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            Intent i=new Intent(MainActivity.this,Main2Activity.class);
                            startActivity(i);
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                            {
                                Toast.makeText(MainActivity.this, "Invalied Credentials", Toast.LENGTH_SHORT).show();
                            }
                            }
                        }

                });
    }
    private void sendVerificationCode()
    {
        String phno2=phno1.getText().toString().trim();
        if(phno2.length()<10)
        {
            phno1.setError("Enter correct Phone number");
            phno1.requestFocus();
            return;
        }
        if(phno2.isEmpty())
        {
            phno1.setError("Enter Phone number");
            phno1.requestFocus();
            return;
        }
        final ProgressBar pb=(ProgressBar)findViewById(R.id.progressbar);
        pb.setVisibility(View.VISIBLE);

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91"+phno2,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            final ProgressBar pb=(ProgressBar)findViewById(R.id.progressbar);
            pb.setVisibility(View.INVISIBLE);
            otp1.setVisibility(View.VISIBLE);
            submit1.setVisibility(View.VISIBLE);
            super.onCodeSent(s, forceResendingToken);
            codeSent=s;
        }
    };
}
