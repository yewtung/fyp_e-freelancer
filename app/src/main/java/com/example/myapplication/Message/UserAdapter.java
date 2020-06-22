package com.example.myapplication.Message;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.User.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context context;
    private List<User> userList;
    private String last_message;

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, viewGroup, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final User user = userList.get(i);
        viewHolder.user_username.setText(user.getUsername());
        Picasso.get().load(user.getImageURL()).into(viewHolder.user_profile_picture);
        lastMessage(user.getId(), viewHolder.user_lastMessage);

        final String id = user.getId();
        DatabaseReference jobRef = FirebaseDatabase.getInstance().getReference("Job").child(id);
        jobRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentChat = new Intent(context, MessageActivity.class);
                intentChat.putExtra("chatAttempt", "false");
                intentChat.putExtra("chatReceiverID", id);
                intentChat.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intentChat);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView user_username, user_lastMessage;
        private CircleImageView user_profile_picture;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            user_username = itemView.findViewById(R.id.user_username);
            user_profile_picture = itemView.findViewById(R.id.user_profile_picture);
            user_lastMessage = itemView.findViewById(R.id.user_lastMessage);
        }
    }
    private void lastMessage (final String userID, final TextView lastMsg){
        last_message = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chat");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if(chat.getChat_receiverID().equals(firebaseUser.getUid())&& chat.getChat_senderID().equals(userID) ||
                            chat.getChat_receiverID().equals(userID)&& chat.getChat_senderID().equals(firebaseUser.getUid())){
                        last_message = chat.getMessage();
                    }
                }
                switch (last_message){
                    case "default":
                        lastMsg.setText("No message.");
                        break;
                    default:
                        lastMsg.setText(last_message);
                        break;
                }
                last_message = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
