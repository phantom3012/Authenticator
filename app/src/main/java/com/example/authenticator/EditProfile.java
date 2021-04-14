package com.example.authenticator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

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
        System.out.println(fullName+" " + email+" " +hobbies);
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
                    progressBarEdit.setVisibility(View.GONE);
                    return;
                }
                EditUser editUser=new EditUser(up_fullName,up_emailadd,up_hobbies);
                Map<String,Object>postValues=editUser.toMap();
                dbr.child(UID).updateChildren(postValues, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        Toast.makeText(EditProfile.this, "Updated!", Toast.LENGTH_SHORT).show();
                        progressBarEdit.setVisibility(View.GONE);
                        startActivity(new Intent(EditProfile.this,ProfileActivity.class));
                    }
                });
            }
        });
        //Log.i(TAG,"onCreate: "+fullName+" "+email+" "+hobbies );
    }
}