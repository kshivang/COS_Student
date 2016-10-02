package org.invincible.cosstudent.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.invincible.cosstudent.R;
import org.invincible.cosstudent.misc.UserLocalStore;

import java.util.HashMap;



/**
 * Created by kshivang on 05/09/16.
 **/
public class HelpScreen extends AppCompatActivity {

    private ActionBar actionBar;
    private HashMap<String,Object> map;
    String message = "",subject = "";
    EditText subjectText,messageText;
    MaterialDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);

        setContentView(R.layout.activity_help_screen);
        map = new HashMap<>();

        pDialog =  new MaterialDialog.Builder(this)
                .content(R.string.please_wait)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .progress(true, 0,true).build();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        UserLocalStore userEmailStore = new UserLocalStore(this);
        final String email = userEmailStore.getEmail();
        if (email != null) {
            ((TextView)findViewById(R.id.email)).setText(email);
            map.put("email",email);
        }
        actionBar = getSupportActionBar();

        subjectText = (EditText) findViewById(R.id.subject_value);
        messageText = (EditText) findViewById(R.id.message_content);

        Button button = (Button) findViewById(R.id.post_feedback);
        subjectText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length() == 0){
                    subjectText.setError("Field is required");
                }else{
                    subject =  editable.toString();
                }
            }
        });


        messageText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable == null || editable.length() == 0){
                    message = "";
                }else {
                    message = editable.toString();
                }

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(subject.isEmpty()){
                    subjectText.setError("Field required");
                    return;
                }
                postFeedback();
            }
        });
    }

    private void postFeedback() {
        showProgress(true);
        map.put("message", message);
        map.put("subject", subject);
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("feedback").push().updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                subjectText.setText("");
                subjectText.setError(null);
                messageText.setText(null);
                showProgress(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showSuccess();
                    }
                },1500);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showProgress(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showErrorDialog();
                    }
                },1500);
            }
        });
    }

    private void showSuccess(){
        MaterialDialog.Builder builder = new MaterialDialog.Builder(HelpScreen.this);
        builder.content("We will be contacting you soon");
        builder.positiveText("Ok");
        builder.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
            }
        });
        builder.build().show();
    }

    private void showProgress(boolean show){
        if(show){
            pDialog.show();
        }else{
            pDialog.dismiss();
        }
    }

    private void showErrorDialog(){
        MaterialDialog.Builder builder = new MaterialDialog.Builder(HelpScreen.this);
        builder.content("Error.Please try again");
        builder.positiveText("Ok");
        builder.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                postFeedback();
                dialog.dismiss();
            }
        });
        builder.build().show();
    }

    @Override
    protected void onStart(){
        super.onStart();
        if(actionBar != null) {
            actionBar.setTitle("Help");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
