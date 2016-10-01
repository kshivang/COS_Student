package org.invincible.cosstudent.appIntro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import org.invincible.cosstudent.activity.LoginScreen;
import org.invincible.cosstudent.R;
import org.invincible.cosstudent.SampleSlide;


public class BriefIntro extends BaseAppIntro {


    @Override
    public void init(Bundle savedInstanceState) {
        addSlide(SampleSlide.newInstance(R.layout.slide1));
        addSlide(SampleSlide.newInstance(R.layout.slide2));
        addSlide(SampleSlide.newInstance(R.layout.slide3));
        addSlide(SampleSlide.newInstance(R.layout.slide4));
        setFlowAnimation();
    }

    private void loadMainActivity(){
        Intent intent = new Intent(this, LoginScreen.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSkipPressed() {
        loadMainActivity();
        Toast.makeText(getApplicationContext(), R.string.welcome, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNextPressed() {

    }
    @Override
    public void onDonePressed() {
        loadMainActivity();
    }

    @Override
    public void onSlideChanged() {

    }

    public void getStarted(View v){
        loadMainActivity();
    }

}
