package Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatnow.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import Model.MessageModel;

public class ChatAdapter extends  RecyclerView.Adapter {

    ArrayList<MessageModel> messageModels;
    Context context;
    String recId;
    int SENDER_VIEW_TYPE=1;
    int RECIEVER_VIEW_TYPE=2;

    public ChatAdapter(ArrayList<MessageModel> messageModels, Context context,String redId) {
        this.messageModels = messageModels;
        this.context = context;
        this.recId=redId;
    }

    //As we used RecyclerView.Adapter we have to make one more methor getItemViewType
    public int getItemViewType(int position)
    {
        //Identifying if it's sender or reciever
        if(messageModels.get(position).getUid().equals(FirebaseAuth.getInstance().getUid()))
        {
            return SENDER_VIEW_TYPE;
        }
        else
        {
            return RECIEVER_VIEW_TYPE;
        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //If it's sender then the sample_sender layout is being inflated else the sample reciever layout is inflated.
        if(viewType==SENDER_VIEW_TYPE)
        {
            View view= LayoutInflater.from(context).inflate(R.layout.sample_sender,parent,false);
            return new SenderViewHolder(view);
        }
        else
        {
            View view= LayoutInflater.from(context).inflate(R.layout.sample_reciever,parent,false);
            return new RecieverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            MessageModel messageModel=messageModels.get(position);

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    new AlertDialog.Builder(context)
                            .setTitle("Delete")
                            .setMessage("Are you sure you want to delete this message?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    FirebaseDatabase database=FirebaseDatabase.getInstance();
                                    String senderRoom=FirebaseAuth.getInstance().getUid()+recId;
                                    database.getReference().child("chats").child(senderRoom)
                                            .child(messageModel.getMessageId())
                                            .setValue(null);

                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();


                    return false;
                }
            });
            if(holder.getClass()==SenderViewHolder.class)
            {
                //Setting the message Text of the sender to the textview
                ((SenderViewHolder)holder).senderMsg.setText(messageModel.getMessage());
            }
            else {
                //Setting the message Text of the reciever to the textview
                ((RecieverViewHolder)holder).recieverMsg.setText(messageModel.getMessage());
            }
    }

    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView senderMsg,senderTime;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMsg=itemView.findViewById(R.id.senderText);
            senderTime=itemView.findViewById(R.id.senderTime);
        }
    }
    public class RecieverViewHolder extends RecyclerView.ViewHolder {
        TextView recieverMsg,recieverTime;
        public RecieverViewHolder(@NonNull View itemView) {
            super(itemView);
            recieverMsg=itemView.findViewById(R.id.recieverText);
            recieverTime=itemView.findViewById(R.id.recieverTime);
        }
    }
}
