package com.example.bloodlinks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ActivityResetPassword extends AppCompatActivity {


    EditText etresetpassword;
    Button btnreset;

    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        etresetpassword = findViewById(R.id.etresetpassword);
        btnreset = findViewById(R.id.btnreset);
        firebaseAuth = FirebaseAuth.getInstance();

        btnreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String useremail = etresetpassword.getText().toString().trim();

                if(useremail.isEmpty()){

                    Toast.makeText(ActivityResetPassword.this,"provide registered email",Toast.LENGTH_SHORT).show();
                }
                else{

                    firebaseAuth.sendPasswordResetEmail(useremail)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){

                                        Toast.makeText(ActivityResetPassword.this,"password reset email has been sent",Toast.LENGTH_SHORT).show();
                                        finish();
                                        startActivity(new Intent(ActivityResetPassword.this,ActivityLogin.class));
                                    }
                                    else{

                                        Toast.makeText(ActivityResetPassword.this,"wrong email",Toast.LENGTH_SHORT).show();


                                    }



                                }
                            });

                }


            }
        });



    }
}
