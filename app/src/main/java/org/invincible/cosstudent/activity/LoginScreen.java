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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.invincible.cosstudent.R;
import org.invincible.cosstudent.misc.UserLocalStore;
import org.invincible.cosstudent.misc.Constants;
import org.invincible.cosstudent.misc.StudentModel;
import org.invincible.cosstudent.misc.TextHandler;

/**
 * Created by kshivang on 01/10/16.
 * Login Screen
 */
public class LoginScreen extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Button btLogin;
    private TextView tvForgotPassword;
    private View.OnClickListener login, forgotPassword;
    private TextInputLayout tilEmail, tilPassword;
    private TextHandler emailHandler;
    private ProgressBar progressBar;
    private UserLocalStore userLocalStore;
    private final String TAG = "Login Screen";
    private Boolean homeActive = false;

    @Override
    protected void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);

        setContentView(R.layout.activity_login_screen);

        btLogin = (Button) findViewById(R.id.login);
        tvForgotPassword = (TextView) findViewById(R.id.forgot_password);
        tilEmail = (TextInputLayout) findViewById(R.id.emailInput);
        tilPassword = (TextInputLayout) findViewById(R.id.passwordInput);
        progressBar = (ProgressBar)  findViewById(R.id.progress_bar);

        userLocalStore = new UserLocalStore(LoginScreen.this);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null)  {
                    fetchID(user.getUid());

                    userLocalStore.setLoggedIn(true);
                }
            }
        };


        login = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * if soft keyboard is on the screen, hide it
                 */
                if (view != null) {
                    InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                btLogin.setVisibility(View.GONE);
                EditText etPassword = tilPassword.getEditText();
                if (emailHandler.getValue() != null && etPassword != null &&
                        etPassword.getText() != null && etPassword.getText().length() != 0) {
                    signIn(emailHandler.getValue(), etPassword.getText().toString());
                } else {
                    btLogin.setVisibility(View.VISIBLE);
                    Toast.makeText(LoginScreen.this,
                            "Make sure that you entered your email and password correctly",
                            Toast.LENGTH_SHORT).show();
                }

            }
        };

        forgotPassword = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * if soft keyboard is on the screen, hide it
                 */
                if (view != null) {
                    InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                startActivity(new Intent(LoginScreen.this, ForgotPasswordScreen.class));
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        emailHandler = new TextHandler(this, Constants.CHECK_EMAIL, tilEmail);
        btLogin.setOnClickListener(login);
        tvForgotPassword.setOnClickListener(forgotPassword);
        mAuth.addAuthStateListener(mAuthListener);
        homeActive = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        homeActive = false;
    }

    @Override
    public void onStop() {
        super.onStop();
        emailHandler.onStop();

        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (tilPassword != null && tilPassword.getEditText() != null)
            tilPassword.getEditText().setText(null);
        if (tilEmail != null && tilEmail.getEditText() != null)
            tilEmail.getEditText().setText(null);
    }

    private void signIn(final String email, String password) {
        Log.d(TAG, "signIn:" + email);

        progressBar.setVisibility(View.VISIBLE);

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            btLogin.setVisibility(View.VISIBLE);
                        }
                        // [START_EXCLUDE]
                        // [END_EXCLUDE]
                    }
                }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                progressBar.setVisibility(View.GONE);
                Log.e("Login", e.getMessage());
                if (e.getMessage().contains("Network Error")) {

                    Toast.makeText(LoginScreen.this, "Internet not available," +
                                    " Cross check your internet connectivity and try again ",
                            Toast.LENGTH_SHORT).show();
                }else if(e.getMessage().contains("There is no user record corresponding to this identifier.")){
                    Toast.makeText(LoginScreen.this, R.string.auth_failed,
                            Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(LoginScreen.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        // [END sign_in_with_email]
    }

    private void fetchID(final String userId) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("students").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        StudentModel studentModel = dataSnapshot.getValue(StudentModel.class);
                        progressBar.setVisibility(View.GONE);
                        if (studentModel == null) {
                            Log.e(TAG, "StudentInfo " + userId + " is unexpectedly null");
                            Toast.makeText(LoginScreen.this,
                                    "Error: could not fetch Your Info.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Write new post
                            userLocalStore.setID(userId, dataSnapshot
                                    .child("campus_id").getValue(String.class));
                            userLocalStore.setStudentModel(studentModel);
                        }
                        // Finish this Activity, back to the stream
                        finish();
                        if (!homeActive) {
                            homeActive = true;
                            startActivity(new Intent(LoginScreen.this, HomeScreen.class));
                        }
                        // [END_EXCLUDE]
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        // [START_EXCLUDE]
                        // [END_EXCLUDE]
                    }
                });
    }
}
