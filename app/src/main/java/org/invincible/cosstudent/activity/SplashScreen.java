package org.invincible.cosstudent.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import org.invincible.cosstudent.R;
import org.invincible.cosstudent.misc.UserLocalStore;
import org.invincible.cosstudent.appIntro.BriefIntro;

/**
 * Created by kshivang on 01/10/16.
 */

public class SplashScreen extends AppCompatActivity{

    private Boolean isAnimationFinished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Animation bounceAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
        findViewById(R.id.company_title).setAnimation(bounceAnim);

        bounceAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animationsFinished();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void animationsFinished() {
        isAnimationFinished = true;
        //transit to another activity here
        //or do whatever you want
        UserLocalStore userLocalStore = new UserLocalStore(this);
        if (userLocalStore.isFirstRun()) {
            startActivity(new Intent(SplashScreen.this, BriefIntro.class));
            userLocalStore.setFirstRun(false);
        } else {
            Intent intent;
            if(userLocalStore.isLoggedIn()) {
                intent = new Intent(SplashScreen.this, HomeScreen.class);
            } else {
                intent = new Intent(SplashScreen.this, LoginScreen.class);
            }
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isAnimationFinished) {
            finish();
        }
    }
}
