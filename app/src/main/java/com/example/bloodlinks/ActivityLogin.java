package com.example.bloodlinks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ActivityLogin extends AppCompatActivity {

    TextView txtGotoRegister,txtforgotpassword;
    EditText etloginemail,etloginpassword;
    Button btnsignin;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();

        if(firebaseUser!=null){
            finish();
            startActivity(new Intent(ActivityLogin.this,ActivityUser.class));
        }

        setupViews();



        btnsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validate(etloginemail.getText().toString().trim(),etloginpassword.getText().toString().trim());
            }
        });

        txtGotoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivityLogin.this,ActivityRegister.class));
            }
        });

        txtforgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivityLogin.this,ActivityResetPassword.class));
            }
        });

    }

    private void setupViews() {

        txtGotoRegister = findViewById(R.id.txtgotoregister);
        txtforgotpassword = findViewById(R.id.txtforgotpassword);
        etloginemail = findViewById(R.id.etloginemail);
        etloginpassword = findViewById(R.id.etloginpassword);
        btnsignin = findViewById(R.id.btnsignin);
        progressDialog = new ProgressDialog(this);

    }


    private void validate(String mail,String pword){

        if(mail.isEmpty() || pword.isEmpty()){
            Toast.makeText(ActivityLogin.this,"please enter email/password",Toast.LENGTH_SHORT).show();
        }
        else{

            progressDialog.setTitle("Authenticating");
            progressDialog.show();
            firebaseAuth.signInWithEmailAndPassword(mail,pword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                progressDialog.dismiss();
                                checkEmailVerification();

                            }
                            else{
                                progressDialog.dismiss();
                                Toast.makeText(ActivityLogin.this,"incorrect email/password",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }


    }

    private void checkEmailVerification(){

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        Boolean emailflag = firebaseUser.isEmailVerified();

        if(emailflag){

            finish();
            Toast.makeText(ActivityLogin.this,"login successful",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ActivityLogin.this,ActivityUser.class));
        }
        else{

            Toast.makeText(ActivityLogin.this,"verify your email",Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
        }
    }

}
