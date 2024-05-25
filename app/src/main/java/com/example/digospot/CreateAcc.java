package com.example.digospot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.digospot.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class CreateAcc extends AppCompatActivity {

    // Declaration of UI elements and Firebase instances
    private EditText name, email, password, repassword;
    private FirebaseAuth auth;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_acc);

        // Initialize FirebaseAuth and FirebaseDatabase instances
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        // Initialize UI elements
        TextView textView = findViewById(R.id.sign_in);
        textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        Button signUp = findViewById(R.id.signupbtn);
        TextView signIn = findViewById(R.id.sign_in);

        // Initialize input fields
        name = findViewById(R.id.uname);
        email = findViewById(R.id.uemail);
        password = findViewById(R.id.upassword);
        repassword = findViewById(R.id.repassword);

        // Set click listener for "Sign In" text view
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateAcc.this, SignIn.class));
            }
        });

        // Set click listener for "Sign Up" button
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });
    }

    private void createUser() {
        // Retrieve input values
        String userName = name.getText().toString();
        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();
        String userRePassword = repassword.getText().toString();

        // Validate inputs
        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(this, "Name is empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(userEmail)) {
            Toast.makeText(this, "Email is empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(userPassword)) {
            Toast.makeText(this, "Password is empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!userPassword.equals(userRePassword)) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userPassword.length() < 8) {
            Toast.makeText(this, "Password length must be greater than 8 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create user with Firebase Authentication
        auth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Retrieve the user ID and create a UserModel instance
                            String userId = auth.getCurrentUser().getUid();
                            UserModel userModel = new UserModel(userId, userName, userEmail);

                            // Save user data to Firebase Database
                            database.getReference().child("Users").child(userId).setValue(userModel)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(CreateAcc.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(CreateAcc.this, SignIn.class));
                                                finish();
                                            } else {
                                                Toast.makeText(CreateAcc.this, "Failed to register user", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(CreateAcc.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}