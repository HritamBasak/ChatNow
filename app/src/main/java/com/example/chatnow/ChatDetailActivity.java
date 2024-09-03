package com.example.chatnow;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.chatnow.databinding.ActivityChatDetailBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import Adapter.ChatAdapter;
import Model.MessageModel;

public class ChatDetailActivity extends AppCompatActivity {
    ActivityChatDetailBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityChatDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getSupportActionBar().hide();
        database=FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();
        final String senderId=auth.getUid();
        String recieveId=getIntent().getStringExtra("userId");
        String userName=getIntent().getStringExtra("userName");
        String profilePic=getIntent().getStringExtra("profilePic");

        binding.userName.setText(userName);
        Picasso.get().load(profilePic).placeholder(R.drawable.user).into(binding.profileImage);

        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ChatDetailActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        final ArrayList<MessageModel>messageModels=new ArrayList<>();
        final ChatAdapter chatAdapter=new ChatAdapter(messageModels,this,recieveId);
        binding.chatsrecyclerView.setAdapter(chatAdapter);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        binding.chatsrecyclerView.setLayoutManager(linearLayoutManager);

        final String senderRoom=senderId+recieveId;
        final String recieveRoom=recieveId+senderId;

        database.getReference().child("chats")
                .child(senderRoom)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                //Cleared so that the previous message along with new message will not be sent when sending the new message
                                messageModels.clear();
                                //Getting the data from the database and adding it to the messageModels arraylist
                                for(DataSnapshot snapshot1:snapshot.getChildren())
                                {
                                    MessageModel model=snapshot1.getValue(MessageModel.class);
                                    model.setMessageId(snapshot1.getKey());
                                    messageModels.add(model);
                                }
                                chatAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


        //When pressing the Sender Button it will send the message to the database
        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.etMessage.getText().toString().isEmpty())
                {
                    binding.etMessage.setError("Enter Message");
                    return;
                }
                String message=binding.etMessage.getText().toString();
                final MessageModel model=new MessageModel(senderId,message);
                model.setTimestamp(new Date().getTime());
                //After sending the message the message edit text becomes empty
                binding.etMessage.setText("");

                //Created two child of the child node "chats"
                //SenderRoom and RecieverRoom
                database.getReference().child("chats")
                        .child(senderRoom)
                        .push()
                        .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                database.getReference().child("chats")
                                        .child(recieveRoom)
                                        .push()
                                        .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                            }
                                        });
                            }
                        });
            }
        });
    }
}