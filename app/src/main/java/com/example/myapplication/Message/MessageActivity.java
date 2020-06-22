package com.example.myapplication.Message;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.User.User;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity  {

    private String userID, myID, attempt;
    private TextView username, title, title2, textView;
    private CircleImageView profile_pic;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference chatRef;
    private ImageButton btn_send_chat;
    private EditText et_chat;
    private Chat chat;

    private MessageAdapter messageAdapter;
    private List<Chat> chatList;
    private RecyclerView recyclerView;
    private Dialog dialog;
    private EditText salary;
    private Button send, cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        userID = getIntent().getStringExtra("chatReceiverID");
        attempt = getIntent().getStringExtra("chatAttempt");
        //jobID = getIntent().getStringExtra("chatJobID");
        myID = firebaseAuth.getCurrentUser().getUid();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.custom_chat_bar, null);
        actionBar.setCustomView(actionBarView);
        actionBar.setTitle("");

        username = findViewById(R.id.chat_bar_username);
        profile_pic = findViewById(R.id.chat_bar_profile_picture);

        DatabaseReference profile_databaseReference = firebaseDatabase.getReference("User").child(userID);
        profile_databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User userAcc = dataSnapshot.getValue(User.class);
                username.setText(userAcc.getUsername());
                String imageURL = userAcc.getImageURL();
                Picasso.get().load(imageURL).into(profile_pic);
                readMessage(myID, userID, imageURL);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MessageActivity.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        dialog = new Dialog(this);

        if(attempt.equals("true")){
            dialog.setContentView(R.layout.pop_up_negotiate);
            title = (TextView) dialog.findViewById(R.id.title);
            title2 = (TextView) dialog.findViewById(R.id.title2);
            textView = (TextView) dialog.findViewById(R.id.textView21);
            salary = (EditText) dialog.findViewById(R.id.etNegotiateSalary);
            send = (Button) dialog.findViewById(R.id.btnSend);
            cancel = (Button) dialog.findViewById(R.id.btnCancel);

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String message1 = salary.getText().toString();

                    if (TextUtils.isEmpty(message1)) {
                        Toast.makeText(MessageActivity.this, "Please type a message...", Toast.LENGTH_SHORT).show();
                    } else {

                        sendMessage(myID, userID,String.format("Negotiate : RM" + "%.2f",Double.parseDouble(message1)));
                        Toast.makeText(MessageActivity.this, "Message is successfully sent.", Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                }
            });

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));;
            dialog.getWindow().setGravity(Gravity.BOTTOM);
            dialog.show();
        }

        et_chat = (EditText) findViewById(R.id.et_chat);
        btn_send_chat = (ImageButton) findViewById(R.id.btn_send_chat);
        btn_send_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message = et_chat.getText().toString();

                if (TextUtils.isEmpty(message)) {
                    Toast.makeText(MessageActivity.this, "Please type a message...", Toast.LENGTH_SHORT).show();
                } else {

                    sendMessage(myID, userID, message);
                    Toast.makeText(MessageActivity.this, "Message is sent successfully.", Toast.LENGTH_SHORT).show();

                }
                et_chat.setText("");
            }
        });

        recyclerView = findViewById(R.id.chat_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


    }

    private void sendMessage(String senderID, String receiverID, String message){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        String chat_ID = ref.push().getKey();

        chat = new Chat(chat_ID, senderID, receiverID, message);
        ref.child("Chat").child(chat_ID).setValue(chat);
    }

    private void readMessage(final String myID, final String userID, final String imageURL){
        chatList = new ArrayList<>();
        chatRef = FirebaseDatabase.getInstance().getReference("Chat");
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if(chat.getChat_senderID().equals(myID)&& chat.getChat_receiverID().equals(userID) ||
                            chat.getChat_senderID().equals(userID)&& chat.getChat_receiverID().equals(myID)){
                        chatList.add(chat);
                    }
                    messageAdapter = new MessageAdapter(MessageActivity.this, chatList, imageURL);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //get the current date and time
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
