package com.example.blooddonationkotli.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.blooddonationkotli.R;
import com.example.blooddonationkotli.Utils.FirebaseOffline;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 100;
    TextInputLayout edtEmail, edtPassword;
    Button btnLogin;
    LinearLayout googleLogin, twitterLogin;
    TextView errorText, linkCreateAccount;
    FirebaseAuth firebaseAuth;
    Dialog progressDialog;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    DatabaseReference userRefrenc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseOffline.getSync();
        mAuth = FirebaseAuth.getInstance();
        initViews();
        login();
        navigateToRegister();
        createRequest();

    }


    private void initViews() {
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtpassword);
        btnLogin = findViewById(R.id.btnLogin);
//      twitterLogin = findViewById(R.id.twitterLogin);
        errorText = findViewById(R.id.errorText);
        linkCreateAccount = findViewById(R.id.linkCreateaccount);
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new Dialog(this);
        View mView = getLayoutInflater().inflate(R.layout.progress_dialog_wait, null);
        progressDialog.setContentView(mView);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        userRefrenc = FirebaseDatabase.getInstance().getReference();

    }

    private void login() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                String email = edtEmail.getEditText().getText().toString();
                String password = edtPassword.getEditText().getText().toString();
                if (TextUtils.isEmpty(email)) {
                    edtEmail.setError("Enter email");
                } else if (TextUtils.isEmpty(password)) {
                    edtPassword.setError("Enter password");
                } else {
                    edtEmail.setError(null);
                    edtPassword.setError(null);
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                                    String tokenID = FirebaseInstanceId.getInstance().getToken();
                                    userRefrenc.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("tokenId").setValue(tokenID).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                progressDialog.dismiss();
                                                startActivity(new Intent(MainActivity.this, Dashboard.class));
                                                finish();
                                            }
                                        }
                                    });
                                } else {
                                    progressDialog.dismiss();
                                    Snackbar.make(findViewById(R.id.layoutTop), "Please Verify your email", Snackbar.LENGTH_LONG).show();
                                }
                            } else {
                                progressDialog.dismiss();
                                Snackbar.make(findViewById(R.id.layoutTop), "Error! Login Failed Check Email or Password", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }


    private void navigateToRegister() {
        linkCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignUp.class));
                finish();
            }
        });
    }


    public void googleLogin(View view) {
        progressDialog.show();
        signIn();
    }

    private void createRequest() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn() {
        progressDialog.dismiss();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            progressDialog.show();
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (completedTask.isSuccessful()) {
                progressDialog.dismiss();
                FirebaseUser user = mAuth.getCurrentUser();
                startActivity(new Intent(MainActivity.this, Setup_Profile.class));
                finish();
            }
        } catch (ApiException e) {
            Toast.makeText(this, e.getStatusCode(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    public void phoneLogin(View view) {
    }
}