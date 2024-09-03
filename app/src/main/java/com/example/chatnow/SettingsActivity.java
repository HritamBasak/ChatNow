package com.example.chatnow;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chatnow.databinding.ActivitySettingsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

import Model.Users;

public class SettingsActivity extends AppCompatActivity {
    ActivitySettingsBinding binding;
    FirebaseStorage storage;
    FirebaseAuth auth;
    FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getSupportActionBar().hide();
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SettingsActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        storage=FirebaseStorage.getInstance();
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();

        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    //Setting the picture into the user logo
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users users=snapshot.getValue(Users.class);
                        Picasso.get().load(users.getProfilePic())
                                .placeholder(R.drawable.user)
                                .into(binding.profileImage);

                        binding.etStatus.setText(users.getStatus());
                        binding.etUserName.setText(users.getUserName());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status,username;
                status=binding.etStatus.getText().toString();
                username=binding.etUserName.getText().toString();

                //Storing the User Name and About in the HashMap
                HashMap<String,Object> obj=new HashMap<>();
                obj.put("userName",username);
                obj.put("status",status);

                //Updates the User Name and About in the Firebase Database
                database.getReference().child("Users")
                        .child(FirebaseAuth.getInstance().getUid())
                        .updateChildren(obj);
                Toast.makeText(SettingsActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
            }
        });



        //On Selecting the plus icon,it will go the Gallery Activity and select the image,vdo or the desired thing
        binding.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent,33);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data.getData()!=null)//If the image is selected
        {
             Uri sFile=data.getData();
             binding.profileImage.setImageURI(sFile);
            
             //Storing the taken Image in the Firebase Storage
             final StorageReference reference=storage.getReference().child("profile picture")
                     .child(FirebaseAuth.getInstance().getUid());
             
             //Storing the image in the Firebase Database
             reference.putFile(sFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                 @Override
                 public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                     reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                         @Override
                         public void onSuccess(Uri uri) {
                             //Setting the profile pic and overlapping it with the current one
                             database.getReference().child("Users")
                                     .child(FirebaseAuth.getInstance().getUid())
                                     .child("profilePic").setValue(uri.toString());
                             Toast.makeText(SettingsActivity.this, "Uploaded Image", Toast.LENGTH_SHORT).show();
                         }
                     });
                 }
             });
        }
    }
}