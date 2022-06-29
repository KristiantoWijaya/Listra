package com.example.listra;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginPage extends AppCompatActivity{

    private Button loginButton;
    private EditText editTextEmail, editTextPassword;
    private TextView textViewRegisterNow;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page1);

        loginButton = (Button) findViewById(R.id.LoginButton);
        editTextEmail = (EditText) findViewById(R.id.Email);
        editTextPassword = (EditText) findViewById(R.id.Password);
        textViewRegisterNow = (TextView) findViewById(R.id.RegisterNow);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();
//check if username and password format is correct
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getting the email and passwor dfrom user input to string
                String email = editTextEmail.getText().toString().trim();
                String pass = editTextPassword.getText().toString().trim();

                if(email.isEmpty()){
                    editTextEmail.setError("Email cannot be empty");
                    editTextEmail.requestFocus();
                    return;
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    editTextEmail.setError("Please provide a valid email");
                    editTextEmail.requestFocus();
                    return;
                }

                if(pass.isEmpty()){
                    editTextPassword.setError("Password cannot be empty");
                    editTextPassword.requestFocus();
                    return;
                }

                if(pass.length() < 6){
                    editTextPassword.setError("Password length must at least be 6 characters");
                    editTextPassword.requestFocus();
                    return;
                }
//signing in the user
                progressBar.setVisibility(View.VISIBLE);
                mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            if (user.isEmailVerified()) {
                                Toast.makeText(LoginPage.this, "Login success", Toast.LENGTH_LONG).show();
                                openMainPage();
                                progressBar.setVisibility(View.GONE);
                            } else {

                                user.sendEmailVerification();
                                Toast.makeText(LoginPage.this, "Check your email to verify your account", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        } else {

                            Toast.makeText(LoginPage.this, "Login failed, check your email and password!", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                        //progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });

        textViewRegisterNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRegisterPage();
            }
        });
    }

    public void openRegisterPage(){
        Intent intent = new Intent(this, RegisterPage.class);
        startActivity(intent);
    }

    public void openMainPage(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}