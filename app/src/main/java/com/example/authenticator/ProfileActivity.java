package com.example.authenticator;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


public class ProfileActivity extends AppCompatActivity {
    private Button logout, getLocation;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    private ImageView profileImage;
    private Button changeDp, changeProfile;
    private TextView latitude,longitude;
    private StorageReference storageReference;
    private ProgressBar progressBar;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private String lat, lon;

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        logout = findViewById(R.id.logout);
        profileImage = findViewById(R.id.profileimage);
        changeDp = findViewById(R.id.changedp);
        changeProfile = findViewById(R.id.editprofilebutton);
        progressBar = findViewById(R.id.checkloading);
        getLocation = findViewById(R.id.location);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            }
        });
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference();
        userID = user.getUid();
        TextView nameview = findViewById(R.id.nameview);
        TextView emailview = findViewById(R.id.emailview);
        TextView hobbiesview = findViewById(R.id.hobbiesview);
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userprofile = snapshot.getValue(User.class);
                if (userprofile != null) {
                    String fullName = userprofile.fullName;
                    String email = userprofile.email;
                    String hobbies = userprofile.hobbies;
                    nameview.setText(fullName);
                    emailview.setText(email);
                    hobbiesview.setText(hobbies);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Something went wrong. Here, have an F", Toast.LENGTH_LONG).show();
            }
        });
        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                    ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
                } else {
                    openDialogAndGetLocation();
                }
            }
        });

        StorageReference profileRef = storageReference.child("users/" + userID + "/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });

        changeDp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 1000);
            }
        });

        changeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), EditProfile.class);
                intent.putExtra("fullnametag", nameview.getText().toString());
                intent.putExtra("email", emailview.getText().toString());
                intent.putExtra("hobbies", hobbiesview.getText().toString());
                startActivity(intent);
            }
        });
    }

    private void openDialogAndGetLocation() {
        builder = new AlertDialog.Builder(this);
        final View locationPopup = getLayoutInflater().inflate(R.layout.locationpopup, null);
        latitude = locationPopup.findViewById(R.id.latitude);
        longitude = locationPopup.findViewById(R.id.longitude);

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Location permission required", Toast.LENGTH_SHORT).show();
            return;
        }
        LocationServices.getFusedLocationProviderClient(ProfileActivity.this).requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                LocationServices.getFusedLocationProviderClient(ProfileActivity.this).removeLocationUpdates(this);
                if(locationResult!=null&&locationResult.getLocations().size()>0){
                    int latestLocationIndex=locationResult.getLocations().size()-1;
                    double latitude=locationResult.getLocations().get(latestLocationIndex).getLatitude();
                    double longitude=locationResult.getLocations().get(latestLocationIndex).getLongitude();
                    lat=Double.toString(latitude);
                    lon=Double.toString(longitude);
                }
            }
        }, Looper.getMainLooper());

        latitude= locationPopup.findViewById(R.id.latitude);
        longitude= locationPopup.findViewById(R.id.longitude);
        latitude.setText("Latitude:"+lat);
        longitude.setText("Longitude:"+lon);
        builder.setView(locationPopup);
        alertDialog=builder.create();
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1000){
            if(resultCode== Activity.RESULT_OK){
                progressBar.setVisibility(View.VISIBLE);
                Uri imageUri=data.getData();
                uploadImageToFirebase(imageUri);
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        StorageReference fileReference= storageReference.child("users/"+userID+"/profile.jpg");
        fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(ProfileActivity.this, "Profile picture updated successfully!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profileImage);

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this, "Unable to update profile picture!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }



}