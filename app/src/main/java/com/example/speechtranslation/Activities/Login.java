package com.example.speechtranslation.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
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

public class Login extends AppCompatActivity {
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private Button btnLogin;
    private ProgressBar pbLogin;
    private TextView hlLogin;
    private TextView tvLogin;
    private Handler handler;
    private Runnable runnable;
    private String[] strings = {"Login", "로그인", "ログイン", "Đăng nhập", "登錄"} ;
    private int currentIndex = 0;
    private FirebaseAuth mAuth;

    @Override
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
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        etEmail = findViewById(R.id.email);
        etPassword = findViewById(R.id.password);
        pbLogin = findViewById(R.id.pbLogin);
        hlLogin = findViewById(R.id.hlLogin);
        btnLogin = findViewById(R.id.btnLogin);
        tvLogin = findViewById(R.id.tvLogin);
//        etPassword.setHintTextColor(getResources().getColor(R.color.white));
        btnLogin.setBackgroundColor(getResources().getColor(R.color.black));
        textAnim();


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pbLogin.setVisibility(View.VISIBLE);
                String email = String.valueOf(etEmail.getText());
                String password = String.valueOf(etPassword.getText());

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(Login.this, "Email field should not be blank!", Toast.LENGTH_SHORT).show();
                    pbLogin.setVisibility(View.GONE);

                    return;
                }
                if (TextUtils.isEmpty(password)){
                    Toast.makeText(Login.this, "Password field should not be blank!", Toast.LENGTH_SHORT).show();
                    pbLogin.setVisibility(View.GONE);
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                pbLogin.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(Login.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    pbLogin.setVisibility(View.GONE);
                                }
                            }
                        });

            }
        });

        hlLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this , Register.class);
                startActivity(intent);
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
                tvLogin.setText(strings[currentIndex]);

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
