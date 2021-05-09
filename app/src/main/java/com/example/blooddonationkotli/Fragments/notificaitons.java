package com.example.blooddonationkotli.Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blooddonationkotli.Activities.MessageActivity;
import com.example.blooddonationkotli.Model.NotificaiotnsModel;
import com.example.blooddonationkotli.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class notificaitons extends Fragment {
    RecyclerView recyclerView;
    Dialog dialog;
    String url;
    private String uName;
    TextView tvClear;
    DatabaseReference removeRefrence;
    SweetAlertDialog pprogDialog;
    private String imageUrl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_notificaitons, container, false);
        initViews(mView);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getnotfCount();
        getNotificatoins();
        removeNotificaitons();
    }

    private void getnotfCount() {
        DatabaseReference count = FirebaseDatabase.getInstance().getReference("notifications");
        count.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    tvClear.setVisibility(View.VISIBLE);
                } else {
                    tvClear.setText("No Notifications");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void removeNotificaitons() {
        tvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE);
                sweetAlertDialog.setTitleText("Are you sure");
                sweetAlertDialog.setContentText("Your notifications will be removed");
                sweetAlertDialog.setConfirmText("Yes! Remove");
                sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(final SweetAlertDialog sweetAlertDialog) {

                        removeRefrence = FirebaseDatabase.getInstance().getReference("notifications");
                        removeRefrence.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            sweetAlertDialog.dismissWithAnimation();
                                            Snackbar.make(tvClear, "Notifications Removed", Snackbar.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                });
                sweetAlertDialog.show();
            }
        });

    }

    private void getNotificatoins() {
        pprogDialog.show();


        DatabaseReference dbrefd = FirebaseDatabase.getInstance().getReference("notifications");
        FirebaseRecyclerOptions<NotificaiotnsModel> options = new FirebaseRecyclerOptions.Builder<NotificaiotnsModel>()
                .setQuery(dbrefd.child(FirebaseAuth.getInstance().getCurrentUser().getUid()), NotificaiotnsModel.class)
                .build();


        FirebaseRecyclerAdapter<NotificaiotnsModel, NotificationViewHolder> adapter = new FirebaseRecyclerAdapter<NotificaiotnsModel, NotificationViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final NotificationViewHolder holder, int position, @NonNull final NotificaiotnsModel model) {
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");
                userRef.child(model.getFrom()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChildren()) {
                            uName = snapshot.child("name").getValue(String.class);
                            imageUrl = snapshot.child("imageUrl").getValue(String.class);
                            holder.tvName.setText(uName);
                            Picasso.get().load(imageUrl).into(holder.circleImageView);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

                holder.btnShowchat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Animation slide = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in);
                        holder.btnChat.startAnimation(slide);
                        holder.btnChat.setVisibility(View.VISIBLE);
                    }
                });

                holder.btnChat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), MessageActivity.class);
                        intent.putExtra("userId", model.getFrom());
                        intent.putExtra("name", uName);
                        intent.putExtra("imageUrl", imageUrl);
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View mView = LayoutInflater.from(getContext()).inflate(R.layout.custom_notification_layout, parent, false);
                return new NotificationViewHolder(mView);
            }
        };
        pprogDialog.dismissWithAnimation();
        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }

    private void initViews(View mView) {
        tvClear = mView.findViewById(R.id.tvClearAll);
        recyclerView = mView.findViewById(R.id.recyclerViewNotification);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        pprogDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pprogDialog.show();
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView tvName;
        LinearLayout btnChat, btnShowchat;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.userprofileImage);
            tvName = itemView.findViewById(R.id.tvuserNameNotificaiton);
            btnChat = itemView.findViewById(R.id.btnChat);
            btnShowchat = itemView.findViewById(R.id.btnShowChat);
        }
    }
}