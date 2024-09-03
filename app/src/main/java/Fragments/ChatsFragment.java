package Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatnow.R;
import com.example.chatnow.databinding.FragmentChatsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import Adapter.UsersAdapter;
import Model.Users;


public class ChatsFragment extends Fragment {
    FragmentChatsBinding binding;
    ArrayList<Users> list=new ArrayList<>();
    FirebaseDatabase database;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentChatsBinding.inflate(inflater, container, false);

        database=FirebaseDatabase.getInstance();

        //Getting the values and setting it in the recyclerView
        UsersAdapter usersAdapter=new UsersAdapter(list,getContext());
        binding.chatsrecyclerView.setAdapter(usersAdapter);

        //Setting the LayoutManager
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        binding.chatsrecyclerView.setLayoutManager(linearLayoutManager);

        //Getting the values from the node User of the database
        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                //Getting the values one by one using the DataSnapShot Class and setting it in the list
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Users users=dataSnapshot.getValue(Users.class);
                    users.setUserId(dataSnapshot.getKey());
                    if(!users.getUserId().equals(FirebaseAuth.getInstance().getUid()))
                    list.add(users);
                }
                usersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return binding.getRoot();
    }
}