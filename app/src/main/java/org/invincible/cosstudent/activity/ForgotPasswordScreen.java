package org.invincible.cosstudent.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.invincible.cosstudent.R;
import org.invincible.cosstudent.misc.Constants;
import org.invincible.cosstudent.misc.TextHandler;


/**
 * Created by kshivang on 04/09/16.
 */
public class ForgotPasswordScreen extends AppCompatActivity {

    Button btSend;
    TextInputLayout tilEmail;
    TextView tvBackToLogin;
    TextHandler emailHandler;
    FirebaseAuth auth;
    View.OnClickListener backToLogin, send;

    @Override
    protected void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);

        setContentView(R.layout.activity_forgot_password_screen);

        btSend = (Button) findViewById(R.id.send);
        tvBackToLogin = (TextView) findViewById(R.id.backToLogin);
        tilEmail = (TextInputLayout) findViewById(R.id.emailInput);
        auth = FirebaseAuth.getInstance();
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        backToLogin = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ForgotPasswordScreen.this, LoginScreen.class));
            }
        };

        final EditText etEmail = tilEmail.getEditText();

        send = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view != null) {
                    InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                btSend.setVisibility(View.GONE);
                if(etEmail != null && etEmail.getText() != null &&
                        etEmail.getText().length() > 0 && !tilEmail.isErrorEnabled()) {
                    progressBar.setVisibility(View.VISIBLE);
                    auth.sendPasswordResetEmail(etEmail.getText().toString())
                            .addOnCompleteListener(ForgotPasswordScreen.this,
                                    new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if (task.isSuccessful()) {
                                        Log.d("forgot password", "Email sent.");
                                        Toast.makeText(ForgotPasswordScreen.this, "New Password " +
                                                "has been sent to " + etEmail.getText().toString(),
                                                Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(ForgotPasswordScreen.this,
                                                LoginScreen.class));
                                    } else {
                                        btSend.setVisibility(View.VISIBLE);
                                    }
                                }
                            })
                    .addOnFailureListener(ForgotPasswordScreen.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            btSend.setVisibility(View.VISIBLE);
                            Log.e("Forgot", e.getMessage());
                            if(e.getMessage().contains("Network Error")) {

                                Toast.makeText(ForgotPasswordScreen.this, "Internet not available," +
                                                " Cross check your internet connectivity and try again ",
                                        Toast.LENGTH_SHORT).show();
                            } else if (e.getMessage().contains("There is no user record corresponding " +
                                    "to this identifier. The user may have been deleted.")){
                                Toast.makeText(ForgotPasswordScreen.this, "Sorry!!! We " +
                                                "didn’t find any account associated with " +
                                                etEmail.getText().toString(),
                                        Toast.LENGTH_SHORT).show();
                            } else if (e.getMessage().contains("INVALID")){
                                Toast.makeText(ForgotPasswordScreen.this, "Check you email address",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ForgotPasswordScreen.this, "" + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    if (tilEmail.isErrorEnabled()) {
                        Toast.makeText(ForgotPasswordScreen.this, "" + tilEmail.getError(),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ForgotPasswordScreen.this, "Enter email address !",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

    }

    @Override
    public void onStart(){
        super.onStart();
        btSend.setOnClickListener(send);
        tvBackToLogin.setOnClickListener(backToLogin);
        emailHandler = new TextHandler(this, Constants.CHECK_EMAIL, tilEmail);
    }

    @Override
    public void onStop() {
        super.onStop();
        btSend.setOnClickListener(null);
        tvBackToLogin.setOnClickListener(null);
        emailHandler.onStop();
    }
}
