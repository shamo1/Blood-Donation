package com.example.blooddonationkotli.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blooddonationkotli.Model.MesssageModel;
import com.example.blooddonationkotli.Notifications.APIService;
import com.example.blooddonationkotli.Notifications.Client;
import com.example.blooddonationkotli.Notifications.Data;
import com.example.blooddonationkotli.Notifications.REsponce;
import com.example.blooddonationkotli.Notifications.Sender;
import com.example.blooddonationkotli.R;
import com.example.blooddonationkotli.Utils.FirebaseOffline;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {
    String userId, uName, imageUrl, currentUserId;
    DatabaseReference userRefrence, messageRefrence;
    FirebaseAuth mAuth;
    TextView displayName;
    CircleImageView circleImageView;
    EditText edtMessage;
    RecyclerView recyclerViewMessage;
    LinearLayout btnsendSms, makeCall;
    private LinearLayoutManager mLinearLayoutmanager;
    APIService apiService;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        FirebaseOffline.getSync();
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        intView();
        getUserId();
        setUserInfo();
        getMessages();
        getToken();

    }

    private void getToken() {
        DatabaseReference userTokenRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        userTokenRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    token = snapshot.child("tokenId").getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMessages() {
        DatabaseReference messages_Ref = FirebaseDatabase.getInstance().getReference();
        messages_Ref = messages_Ref.child("messages").child(currentUserId).child(userId);

        FirebaseRecyclerOptions<MesssageModel> options =
                new FirebaseRecyclerOptions.Builder<MesssageModel>().setQuery(messages_Ref, MesssageModel.class).build();
        final FirebaseRecyclerAdapter<MesssageModel, MessageViewHolder> adapter = new FirebaseRecyclerAdapter<MesssageModel, MessageViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MessageViewHolder holder, int position, @NonNull MesssageModel model) {

                String whoId = model.getFrom();
                if (whoId.equals(currentUserId)) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.weight = 1.0f;
                    params.gravity = Gravity.END;

                    holder.tvMessage.setLayoutParams(params);
                    holder.tvMessage.setText(model.getMessage());
                    holder.tvMessage.setBackground(ContextCompat.getDrawable(MessageActivity.this, R.drawable.message_sender_drawable));
                    holder.tvMessage.setTextColor(Color.parseColor("#ffffff"));

                } else {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.weight = 1.0f;
                    params.gravity = Gravity.START;

                    holder.tvMessage.setLayoutParams(params);
                    holder.tvMessage.setText(model.getMessage());
                    holder.tvMessage.setBackground(ContextCompat.getDrawable(MessageActivity.this, R.drawable.message_reciever_drawable));
                    holder.tvMessage.setTextColor(Color.parseColor("#000000"));
                }
            }

            @NonNull
            @Override
            public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View mView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custome_message_layout, parent, false);
                return new MessageViewHolder(mView);
            }
        };


        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                int message_cont = adapter.getItemCount();
                int lastPostition = mLinearLayoutmanager.findLastCompletelyVisibleItemPosition();

                if (lastPostition == -1 || (positionStart >= (message_cont - 1) && lastPostition == (positionStart - 1))) {
                    recyclerViewMessage.scrollToPosition(positionStart);
                }

            }
        });
        adapter.startListening();
        recyclerViewMessage.setAdapter(adapter);

    }

    private void intView() {
        userRefrence = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        displayName = findViewById(R.id.tvDiaplayName);
        circleImageView = findViewById(R.id.diaplayImageChatHead);
        messageRefrence = FirebaseDatabase.getInstance().getReference();
        edtMessage = findViewById(R.id.inputmessage);
        recyclerViewMessage = findViewById(R.id.recyclerViewmessages);
        mLinearLayoutmanager = new LinearLayoutManager(this);
        recyclerViewMessage.setLayoutManager(mLinearLayoutmanager);
    }

    private void setUserInfo() {
        displayName.setText(uName);
        Picasso.get().load(imageUrl).into(circleImageView);
    }

    private void getUserId() {
        userId = getIntent().getStringExtra("userId");
        uName = getIntent().getStringExtra("name");
        imageUrl = getIntent().getStringExtra("imageUrl");
        currentUserId = mAuth.getCurrentUser().getUid();
//        userRefrence = userRefrence.child("Users").child(userId);
//        userRefrence.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.hasChildren()) {
//                    uName = snapshot.child("name").getValue(String.class);
//                    imageUrl = snapshot.child("imageUrl").getValue(String.class);
//                    Toast.makeText(MessageActivity.this, uName + imageUrl, Toast.LENGTH_SHORT).show();
//                } else {
//                    Snackbar.make(findViewById(R.id.bottomcontinaer), "No User Profile", Snackbar.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//            }
//        });
    }

    public void btnSendChat(View view) {
        final String message = edtMessage.getText().toString();
        if (!TextUtils.isEmpty(message)) {
            String current_userRef = "messages/" + currentUserId + "/" + userId;
            String recever_ref = "messages/" + userId + "/" + currentUserId;

            DatabaseReference user_ref_push = messageRefrence.child(currentUserId).child(userId).push();
            String pushKey = user_ref_push.getKey();

//            final DatabaseReference rencentChatsenderRef = FirebaseDatabase.getInstance().getReference("Chats").child(currentUserId);
//            final DatabaseReference recentCatRecieverRef = FirebaseDatabase.getInstance().getReference("Chats").child(userId);

            final String mchatSender = "Chats/" + currentUserId;
            final String mchatUser = "Chats/" + userId;
            final DatabaseReference recentChatsRef = FirebaseDatabase.getInstance().getReference();
            Query ifExist = recentChatsRef.child(mchatUser).orderByChild("senderKey").equalTo(currentUserId);
            ifExist.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("senderKey", currentUserId);
                        recentChatsRef.child(mchatUser).push().setValue(hashMap);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            Query ifExistuser = recentChatsRef.child(mchatSender).orderByChild("senderKey").equalTo(userId);
            ifExistuser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("senderKey", userId);
                        recentChatsRef.child(mchatSender).push().setValue(hashMap);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            Map messagemap = new HashMap<>();
            messagemap.put("message", message);
            messagemap.put("type", "text");
            messagemap.put("from", currentUserId);


            HashMap<String, Object> chat_map = new HashMap<>();
            chat_map.put(current_userRef + "/" + pushKey, messagemap);
            chat_map.put(recever_ref + "/" + pushKey, messagemap);

            messageRefrence.updateChildren(chat_map, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    edtMessage.setText("");
                    sendNotifications(token, "New Message", edtMessage.getText().toString());
                    if (error != null) {
                        Log.d("CHAT_LOG", error.getMessage());
                    }
                }
            });

        }
        String refreshToken = FirebaseInstanceId.getInstance().getToken();
        updateToken(refreshToken);
    }

    private void updateToken(String refreshToken) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).child("tokenId").setValue(refreshToken);
    }

    private void sendNotifications(String usertoken, String title, String mesage) {
        Data data = new Data(title, mesage);
        Sender sender = new Sender(data, usertoken);
        apiService.sendNotification(sender).enqueue(new Callback<REsponce>() {
            @Override
            public void onResponse(Call<REsponce> call, Response<REsponce> response) {
                if (response.code() == 200) {
                    if (response.body().success != 1) {
                        Toast.makeText(MessageActivity.this, "Notificaiton Not send", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<REsponce> call, Throwable t) {

            }
        });
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;
        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvChatMessage);
        }
    }
}