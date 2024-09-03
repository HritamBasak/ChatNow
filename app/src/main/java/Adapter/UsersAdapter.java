package Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatnow.ChatDetailActivity;
import com.example.chatnow.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import Model.Users;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder>
{
    ArrayList<Users> list;
    Context context;
    public UsersAdapter(ArrayList<Users> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.showusersample,parent,false);
        return new ViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Users users=list.get(position);
        //Picasso for retrieving online image
        //Basically its setting the profile pic as the gmail's profile pic by taking it from online.
        //the default placeholder is the user icon
        Picasso.get().load(list.get(position).getProfilePic()).placeholder(R.drawable.user).into(holder.image);
        holder.userName.setText(list.get(position).getUserName());

        //Code for appearing the last message in the ChatAdapter
        FirebaseDatabase.getInstance().getReference()
                        .child("chats")
                                .child(FirebaseAuth.getInstance().getUid()+users.getUserId())
                                .orderByChild("timestamp")//Order the message in descending order i.e. the last message will be the first node
                                .limitToLast(1)//Only the last message sent
                                .addValueEventListener(new ValueEventListener() {//Changes addListenerForSingleValueEvent to addValueEventListener else the last message is shown if and only if we log in again
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    //If it has the last message then set the last message text to the last message
                                        if(snapshot.hasChildren())
                                        {
                                            for(DataSnapshot snapshot1:snapshot.getChildren())
                                            {
                                                holder.lastMsg.setText(snapshot1.child("message").getValue().toString());
                                            }
                                        }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, ChatDetailActivity.class);
                intent.putExtra("userId",users.getUserId());
                intent.putExtra("profilePic",users.getProfilePic());
                intent.putExtra("userName",users.getUserName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView image;
        TextView userName,lastMsg;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image=itemView.findViewById(R.id.profile_image);
            userName=itemView.findViewById(R.id.userNamelist);
            lastMsg=itemView.findViewById(R.id.lastmessage);
        }
    }
}
