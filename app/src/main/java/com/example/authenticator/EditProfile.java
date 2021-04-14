package com.example.authenticator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class EditProfile extends AppCompatActivity {
   // private static final String TAG = "TAG";
    private EditText editfullName;
    private EditText editemailadd;
    private EditText edithobbies;
    private Button updatedeets;
    private ProgressBar progressBarEdit;
    private DatabaseReference dbr;
    private FirebaseUser fAuth;
    private String UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Intent data=getIntent();
        String fullName=data.getStringExtra("fullnametag");
        String email=data.getStringExtra("email");
        String hobbies=data.getStringExtra("hobbies");

        editfullName= findViewById(R.id.editfullname);
        editemailadd= findViewById(R.id.editemail);
        edithobbies= findViewById(R.id.edithobbies);
        updatedeets= findViewById(R.id.updatedetailsbutton);
        progressBarEdit= findViewById(R.id.editprogress);
        dbr= FirebaseDatabase.getInstance().getReference("Users");
        fAuth=FirebaseAuth.getInstance().getCurrentUser();
        UID=fAuth.getUid();

        editfullName.setText(fullName);
        editemailadd.setText(email);
        edithobbies.setText(hobbies);

        String up_fullName=editfullName.getText().toString();
        String up_emailadd=editemailadd.getText().toString();
        String up_hobbies=edithobbies.getText().toString();

        updatedeets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarEdit.setVisibility(View.VISIBLE);
                if(up_fullName.isEmpty()||up_emailadd.isEmpty()||up_hobbies.isEmpty()){
                    Toast.makeText(EditProfile.this, "At least one field is blank!", Toast.LENGTH_SHORT).show();
                    return;
                }
                HashMap<String,Object> postValues=new HashMap<>();
                postValues.put("fullName",up_fullName);
                postValues.put("email",up_emailadd);
                postValues.put("hobbies",up_hobbies);
                dbr.child(UID).updateChildren(postValues).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBarEdit.setVisibility(View.GONE);
                        Toast.makeText(EditProfile.this, "Updated successfully!!", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(EditProfile.this,ProfileActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfile.this, "Could not update! The error: "+e.getMessage(), Toast.LENGTH_LONG).show();
                        progressBarEdit.setVisibility(View.GONE);
                    }
                });

            }
        });
        //Log.i(TAG,"onCreate: "+fullName+" "+email+" "+hobbies );
    }
}