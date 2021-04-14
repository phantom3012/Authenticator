package com.example.authenticator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterUser extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private TextView banner, registerUser;
    private EditText editTextFullName,editTextEmail,editTextPassword,editTextHobbies;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth=FirebaseAuth.getInstance();

        banner=(TextView)findViewById(R.id.banner);
        banner.setOnClickListener(this);
        registerUser= (Button)findViewById(R.id.registeruser);
        registerUser.setOnClickListener(this);

        editTextFullName=(EditText)findViewById(R.id.fullname);
        editTextEmail=(EditText)findViewById(R.id.emailregister);
        editTextPassword=(EditText)findViewById(R.id.passwordregister);
        editTextHobbies=(EditText)findViewById(R.id.hobbiesregister);

        progressBar=(ProgressBar)findViewById(R.id.progressBar2);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.banner:
                startActivity(new Intent(this,MainActivity.class));
                break;
            case R.id.registeruser:
                registerUser();
                break;

        }
    }

    private void registerUser() {
        String fullName=editTextFullName.getText().toString().trim();
        String email=editTextEmail.getText().toString().trim();
        String password=editTextPassword.getText().toString().trim();
        String hobby=editTextHobbies.getText().toString().trim();

        if(fullName.isEmpty()){
            editTextFullName.setError("Full Name required!");
            editTextFullName.requestFocus();
            return;
        }
        if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Valid e-mail required!");
            editTextEmail.requestFocus();
            return;
        }
        if(password.isEmpty() || password.length()<8)  {
            editTextPassword.setError("Password of >=8 characters required!");
            editTextPassword.requestFocus();
            return;
        }
        if(hobby.isEmpty()){
            editTextHobbies.setError("Hobby(ies) is/are required!");
            editTextHobbies.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    User user=new User(fullName,email,password,hobby);
                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(RegisterUser.this, "User has been registered successfully. Click the Title to log in", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(RegisterUser.this, "Failed to register! Try again!", Toast.LENGTH_SHORT).show();
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }

            }
        });
    }
}