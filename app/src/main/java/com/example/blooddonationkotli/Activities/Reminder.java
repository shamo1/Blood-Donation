package com.example.blooddonationkotli.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blooddonationkotli.Model.ReminderModel;
import com.example.blooddonationkotli.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Reminder extends AppCompatActivity {
    CalendarView calendarView;
    int day, mMonth, mHour, mMinutes, mSeconds, mYear;
    TextInputLayout edtTitle, edtReminderDate, edtReminderDesc;
    Button btnCreateReminder;
    ProgressBar progbarReminder;
    private String selectedDate;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        initViews();
        getDateCalander();
        getReminderList();


    }

    private void getReminderList() {
        DatabaseReference remidnerRef = FirebaseDatabase.getInstance().getReference("Reminders");
        remidnerRef = remidnerRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        FirebaseRecyclerOptions<ReminderModel> options = new FirebaseRecyclerOptions.Builder<ReminderModel>()
                .setQuery(remidnerRef, ReminderModel.class).build();
        FirebaseRecyclerAdapter<ReminderModel, ReminderViewHolder> reminderAdaptor = new FirebaseRecyclerAdapter<ReminderModel, ReminderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ReminderViewHolder holder, int position, @NonNull final ReminderModel model) {
                holder.tvRemidnerTitle.setText(model.getTitle());
                holder.tvReminderDate.setText(model.getDate());
                holder.tvReminderDesc.setText(model.getDescription());
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(Reminder.this, SweetAlertDialog.WARNING_TYPE);
                        sweetAlertDialog.setTitleText("Confirmation");
                        sweetAlertDialog.setContentText("Are your Sure to delete reminder");
                        sweetAlertDialog.setConfirmText("Ok");
                        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Reminders").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                dbref.child(model.getKey()).removeValue();
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        });
                        sweetAlertDialog.show();
                        return false;
                    }
                });
            }

            @NonNull
            @Override
            public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View mView = LayoutInflater.from(Reminder.this).inflate(R.layout.custom_reminder_layout, parent, false);
                return new ReminderViewHolder(mView);
            }
        };
        reminderAdaptor.startListening();
        recyclerView.setAdapter(reminderAdaptor);
    }

    private void getDateCalander() {
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                calendarView.setDate(calendarView.getDate());
                Calendar mcurrentTime = Calendar.getInstance();
                final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                day = dayOfMonth;
                mMonth = month + 1;
                mYear = year;
                selectedDate = day + "/" + mMonth + "/" + mYear;
//                mTimePicker = new TimePickerDialog(Reminder.this, new TimePickerDialog.OnTimeSetListener() {
//                    private TimePicker view;
//                    private int hourOfDay;
//                    private int minute;
//
//                    @Override
//                    public void onTimeSet(Ti mePicker view, int hourOfDay, int minute) {
//                        this.view = view;
//                        this.hourOfDay = hourOfDay;
//                        this.minute = minute;
//
//                        mHour = hour;
//                        mMinutes = minute;
//                    }
//
//                }, hour, minute, true);//Yes 24 hour time
//                mTimePicker.setTitle("Select Time");
//                mTimePicker.show();
                final AlertDialog.Builder reminderDialog = new AlertDialog.Builder(Reminder.this);
                View mView = getLayoutInflater().inflate(R.layout.custom_reminder_alert, null);
                reminderDialog.setView(mView);
                Dialog dialog = reminderDialog.create();
                dialog.show();
                getReminderDilogViews(mView);

                edtReminderDate.getEditText().setText(selectedDate);
                btnCreateReminder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isnotEmipt() == true) {
                            progbarReminder.setVisibility(View.VISIBLE);
                            DatabaseReference reminderRef = FirebaseDatabase.getInstance().getReference("Reminders");
                            String key = reminderRef.push().getKey();
                            ReminderModel reminderModel = new ReminderModel();
                            reminderModel.setTitle(edtTitle.getEditText().getText().toString());
                            reminderModel.setDate(edtReminderDate.getEditText().getText().toString());
                            reminderModel.setDescription(edtReminderDesc.getEditText().getText().toString());
                            reminderModel.setKey(key);
                            reminderRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(key).setValue(reminderModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progbarReminder.setVisibility(View.GONE);
                                    clearFields();
                                }
                            });
                        } else {
                            Toast.makeText(Reminder.this, "Please Fill the Required Fields", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    private void clearFields() {
        edtTitle.getEditText().setText("");
        edtReminderDate.getEditText().setText("");
        edtTitle.getEditText().setText("");
    }

    private void getReminderDilogViews(View mView) {
        edtTitle = mView.findViewById(R.id.reminderTitle);
        edtReminderDate = mView.findViewById(R.id.reminderDate);
        edtReminderDesc = mView.findViewById(R.id.reminderDescription);
        btnCreateReminder = mView.findViewById(R.id.btnCreateReminder);
        progbarReminder = mView.findViewById(R.id.progbarReminderAlert);
    }

    public boolean isnotEmipt() {
        if (!edtTitle.getEditText().getText().toString().isEmpty()) {
            return true;
        }
        if (!edtReminderDate.getEditText().getText().toString().isEmpty()) {
            return true;
        }
        if (!edtReminderDesc.getEditText().getText().toString().isEmpty()) {
            return true;
        } else {
            return false;
        }

    }

    private void initViews() {
        calendarView = findViewById(R.id.calanderViewReminder);
        recyclerView = findViewById(R.id.recyclerviewReminder);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public class ReminderViewHolder extends RecyclerView.ViewHolder {
        TextView tvRemidnerTitle, tvReminderDate, tvReminderDesc;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRemidnerTitle = itemView.findViewById(R.id.reminderLayoutTitle);
            tvReminderDate = itemView.findViewById(R.id.reminderLayoutDate);
            tvReminderDesc = itemView.findViewById(R.id.remidnerLayoutDescription);
        }
    }
}