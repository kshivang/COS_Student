package org.invincible.cosstudent.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.invincible.cosstudent.R;
import org.invincible.cosstudent.misc.Constants;
import org.invincible.cosstudent.misc.TextHandler;
import org.invincible.cosstudent.misc.UserLocalStore;


/**
 * Created by kshivang on 04/09/16.
 **/
public class ChangePasswordScreen extends AppCompatActivity {

    private final String TAG = "Change Password";

    private TextInputLayout tilNewPassword;
    private TextInputLayout tilRePassword;
    private TextHandler newPassword;
    private TextWatcher rePassword;
    private EditText etRePassword;
    private Button btChangePassword;
    private View.OnClickListener changePassword;
    private String email;
    private ProgressBar progressBar;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);

        setContentView(R.layout.activity_change_password);

        UserLocalStore userLocalStore = new UserLocalStore(ChangePasswordScreen.this);
        email = userLocalStore.getEmail();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();

        final TextInputLayout tilOldPassword = (TextInputLayout) findViewById(R.id.oldPasswordInput);
        tilNewPassword = (TextInputLayout) findViewById(R.id.newPasswordInput);
        tilRePassword = (TextInputLayout) findViewById(R.id.reEnterNewPasswordInput);
        btChangePassword = (Button) findViewById(R.id.changePassword);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        btChangePassword.setVisibility(View.INVISIBLE);

        newPassword = new TextHandler(ChangePasswordScreen.this,
                Constants.CHECK_PASSWORD, tilNewPassword);

        final EditText etOldPassword = tilOldPassword.getEditText();
        etRePassword = tilRePassword.getEditText();
        final EditText etNewPassword = tilRePassword.getEditText();

        changePassword = new View.OnClickListener() {
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

                if (etNewPassword != null && etOldPassword != null
                        && etRePassword != null){
                    if (tilOldPassword.getEditText().getText().length() > 0 &&
                            tilNewPassword.getEditText().getText().length() > 0 &&
                            tilRePassword.getEditText().getText().toString().
                                    equals(newPassword.getValue())) {

                        onChangePassword(email, etOldPassword.getText().toString(),
                                etNewPassword.getText().toString());
                    } else {
                        if (tilOldPassword.getEditText().getText().length() == 0) {
                            Toast.makeText(ChangePasswordScreen.this, "Type old password first",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else if (tilNewPassword.getEditText().getText().length() == 0) {
                            Toast.makeText(ChangePasswordScreen.this, "Type new password first",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else if (tilRePassword.getEditText().getText().length() == 0) {
                            Toast.makeText(ChangePasswordScreen.this, "Type new re-enter" +
                                    " new password first", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ChangePasswordScreen.this, "Password do not match",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        };

        rePassword = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(etRePassword != null && etNewPassword != null) {
                    if(etRePassword.getText() != null && newPassword.getValue() != null){
                        if(etRePassword.getText().toString().equals(newPassword.getValue())){
                            tilRePassword.setErrorEnabled(false);
                            btChangePassword.setVisibility(View.VISIBLE);
                            btChangePassword.setEnabled(true);
                        } else {
                            tilRePassword.setErrorEnabled(true);
                            tilRePassword.setError("Password do not match");
                            btChangePassword.setEnabled(false);
                            btChangePassword.setVisibility(View.INVISIBLE);
                        }

                        if (etRePassword.getText().length() == 0){
                            tilRePassword.setErrorEnabled(true);
                            btChangePassword.setEnabled(false);
                            btChangePassword.setVisibility(View.INVISIBLE);
                            tilRePassword.setError("Should be same as new password");
                        }
                    } else {
                        tilRePassword.setErrorEnabled(false);
                        btChangePassword.setEnabled(false);
                        btChangePassword.setVisibility(View.INVISIBLE);
                    }
                }
            }
        };



    }

    @Override
    protected void onStart(){
        super.onStart();

        if(actionBar != null) {
            actionBar.setTitle("Change Password");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (newPassword == null)
            newPassword = new TextHandler(ChangePasswordScreen.this,
                    Constants.CHECK_PASSWORD, tilNewPassword);

        if(changePassword != null) {
            btChangePassword.setOnClickListener(changePassword);
        }

        if (etRePassword != null) {
            etRePassword.addTextChangedListener(rePassword);
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        newPassword.onStop();

        if (etRePassword != null && rePassword != null) {
            etRePassword.removeTextChangedListener(rePassword);
        }
    }

    private void onChangePassword(String email, String oldPassword, final String newPassword) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        progressBar.setVisibility(View.VISIBLE);
// Get auth credentials from the user for re-authentication. The example below shows
// username and password credentials but there are multiple possible providers,
// such as GoogleAuthProvider or FacebookAuthProvider.
        AuthCredential credential = EmailAuthProvider
                .getCredential(email, oldPassword);

// Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "TeacherInfo re-authenticated.");
                        if (task.isSuccessful()){
                            user.updatePassword(newPassword)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            progressBar.setVisibility(View.GONE);
                                            if (task.isSuccessful()) {
                                                Toast.makeText(ChangePasswordScreen.this,
                                                        "Password Updated Successfully",
                                                        Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "On Change:" + e.getMessage());
                                            progressBar.setVisibility(View.GONE);
                                            if (e.getMessage().contains("The password is invalid")) {
                                                    Toast.makeText(ChangePasswordScreen.this,
                                                            "Please enter correct current password" , Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(ChangePasswordScreen.this,
                                                        "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "On ReAuthentication:" + e.getMessage());
                        progressBar.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        if (e.getMessage().contains("The password is invalid")) {
                            Toast.makeText(ChangePasswordScreen.this,
                                    "Please enter correct current password" , Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ChangePasswordScreen.this,
                                    "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
