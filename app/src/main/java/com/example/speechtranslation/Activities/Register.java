package com.example.speechtranslation.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.speechtranslation.MainActivity;
import com.example.speechtranslation.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private Button btnRegister;
    private FirebaseAuth mAuth;
    private ProgressBar pbReg;
    private TextView tvRegister;

    private Handler handler;

    private Runnable runnable;

    private String[] strings = {"Register", "등록하다", "登録", "Đăng ký", "登記"} ;
    private int currentIndex = 0;

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
  
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        etEmail = findViewById(R.id.emailReg);
        etPassword = findViewById(R.id.passwordReg);
        btnRegister = findViewById(R.id.btnRegister);
        pbReg = findViewById(R.id.pbRegistration);
        tvRegister = findViewById(R.id.tvRegister);
        btnRegister.setBackgroundColor(getResources().getColor(R.color.black));
        textAnim();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pbReg.setVisibility(View.VISIBLE);
                String email = String.valueOf(etEmail.getText());
                String password = String.valueOf(etPassword.getText());

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(Register.this, "Email field should not be blank!", Toast.LENGTH_SHORT).show();
                    return;
                }
                 if (TextUtils.isEmpty(password)){
                     Toast.makeText(Register.this, "Password field should not be blank!", Toast.LENGTH_SHORT).show();
                     return;
                 }
               mAuth.createUserWithEmailAndPassword(email, password)
                       .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                           @Override
                           public void onComplete(@NonNull Task<AuthResult> task) {
                               pbReg.setVisibility(View.GONE);
                               if (task.isSuccessful()) {
                                   Toast.makeText(Register.this, "Registration successful",
                                            Toast.LENGTH_SHORT).show();
                                   FirebaseAuth.getInstance().signOut();
                                   Intent intent = new Intent(Register.this, Login.class);
                                   startActivity(intent);
                                   finish();
                               } else {
                                   // If sign in fails, display a message to the user.
                                   Toast.makeText(Register.this, "Authentication failed.",
                                           Toast.LENGTH_SHORT).show();

                               }
                           }  
                       });
            }
        });
    }
    private  void textAnim(){
        // Initialize the handler and runnable
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                // Update the text    
                tvRegister.setText(strings[currentIndex]);

                // Increment the index
                currentIndex = (currentIndex + 1) % strings.length;

                // Delay for 1 second and then repeat
                handler.postDelayed(this, 1000);
            }
        };

        // Start the periodic update
        handler.postDelayed(runnable, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Stop the periodic update when the activity is destroyed
        handler.removeCallbacks(runnable);
    }



}