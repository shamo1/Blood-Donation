package com.example.blooddonationkotli.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blooddonationkotli.Model.MyfavModel;
import com.example.blooddonationkotli.Model.Recentchatmodel;
import com.example.blooddonationkotli.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    RecyclerView recyclerViewRencetnChats, recyclerviewQuieckChat;
    DatabaseReference recentChatRef;
    FirebaseAuth mAuth;
    private String imageUrl;
    LinearLayoutManager mLinearLayoutmanager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        intiViews();
        getRencetCahtUsers();
        getQuickChats();
    }

    private void getQuickChats() {
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Favrouts").child(mAuth.getCurrentUser().getUid());
        FirebaseRecyclerOptions<MyfavModel> options = new FirebaseRecyclerOptions.Builder<MyfavModel>().setQuery(dbref,
                MyfavModel.class
        ).build();
        FirebaseRecyclerAdapter<MyfavModel, MyFavViewHolder> adaptor = new FirebaseRecyclerAdapter<MyfavModel, MyFavViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyFavViewHolder holder, int position, @NonNull MyfavModel model) {
                final String userId = model.getuId();
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
                getuserInfo(userRef, userId, holder.userImages);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        _getPostUserData(userId);
                    }
                });
            }

            @NonNull
            @Override
            public MyFavViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View userView = LayoutInflater.from(ChatActivity.this).inflate(R.layout.custom_myfav_layout, parent, false);
                return new MyFavViewHolder(userView);
            }
        };

        adaptor.startListening();
        recyclerviewQuieckChat.setAdapter(adaptor);
    }

    private void _getPostUserData(final String userId) {
        DatabaseReference userInfoRef = FirebaseDatabase.getInstance().getReference("Users");
        userInfoRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String imgUrl = snapshot.child("imageUrl").getValue(String.class);
                    Intent intent = new Intent(ChatActivity.this, MessageActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("name", name);
                    intent.putExtra("imageUrl", imgUrl);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void getRencetCahtUsers() {
        FirebaseRecyclerOptions<Recentchatmodel> options = new FirebaseRecyclerOptions.Builder<Recentchatmodel>().setQuery(recentChatRef,
                Recentchatmodel.class).build();
        final FirebaseRecyclerAdapter<Recentchatmodel, RecentChatViewHolder> adaptor = new FirebaseRecyclerAdapter<Recentchatmodel, RecentChatViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final RecentChatViewHolder holder, int position, @NonNull Recentchatmodel model) {
                final String userId = model.getSenderKey();
                getUserDetail(userId, holder.imageProfile, holder.tvuName);
                getLastMessage(userId, holder.tvLastMessage);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ChatActivity.this, MessageActivity.class);
                        intent.putExtra("userId", userId);
                        intent.putExtra("name", holder.tvuName.getText());
                        intent.putExtra("imageUrl", imageUrl);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public RecentChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View mView = LayoutInflater.from(ChatActivity.this).inflate(R.layout.custom_recent_chat, parent, false);
                return new RecentChatViewHolder(mView);
            }
        };

        adaptor.startListening();
        recyclerViewRencetnChats.setAdapter(adaptor);
    }

    private void getLastMessage(String userId, final TextView tvLastMessage) {
        DatabaseReference lastMessageRef = FirebaseDatabase.getInstance().getReference("messages");
        Query lastMessageQuery = lastMessageRef.child(mAuth.getCurrentUser().getUid()).child(userId).limitToLast(1);
        lastMessageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                tvLastMessage.setText(snapshot.child("message").getValue(String.class));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUserDetail(final String userId, final CircleImageView imageProfile, final TextView tvuName) {
        DatabaseReference userINforef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        userINforef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    imageUrl = snapshot.child("imageUrl").getValue(String.class);
                    tvuName.setText(snapshot.child("name").getValue(String.class));
                    Picasso.get().load(imageUrl).into(imageProfile);
                    final String name = snapshot.child("name").getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getuserInfo(DatabaseReference userRef, String userId, final CircleImageView userImages) {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    imageUrl = snapshot.child("imageUrl").getValue(String.class);
                    Picasso.get().load(imageUrl).into(userImages);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void intiViews() {
        recyclerViewRencetnChats = findViewById(R.id.recyclerViewRecentChat);
        mLinearLayoutmanager = new LinearLayoutManager(this);
        recyclerViewRencetnChats.setLayoutManager(mLinearLayoutmanager);
        recyclerviewQuieckChat = findViewById(R.id.recyclerviewQuickChat);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true);
        recyclerviewQuieckChat.setLayoutManager(layoutManager);
        layoutManager.setReverseLayout(false);

        mAuth = FirebaseAuth.getInstance();
        recentChatRef = FirebaseDatabase.getInstance().getReference("Chats").child(mAuth.getCurrentUser().getUid());
    }

    class RecentChatViewHolder extends RecyclerView.ViewHolder {
        TextView tvuName, tvLastMessage;
        CircleImageView imageProfile;

        public RecentChatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvuName = itemView.findViewById(R.id.rencetchatUserName);
            tvLastMessage = itemView.findViewById(R.id.tvrecentmessage);
            imageProfile = itemView.findViewById(R.id.recentChatImage);
        }
    }

    class MyFavViewHolder extends RecyclerView.ViewHolder {
        CircleImageView userImages;

        public MyFavViewHolder(@NonNull View itemView) {
            super(itemView);
            userImages = itemView.findViewById(R.id.quickChatimage);
        }
    }
}