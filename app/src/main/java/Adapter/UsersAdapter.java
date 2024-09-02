package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatnow.R;
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
        //Picasso for retrieving online image
        //Basically its setting the profile pic as the gmail's profile pic by taking it from online.
        //the default placeholder is the user icon
        Picasso.get().load(list.get(position).getProfilePic()).placeholder(R.drawable.user).into(holder.image);
        holder.userName.setText(list.get(position).getUserName());
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
