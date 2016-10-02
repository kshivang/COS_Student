package org.invincible.cosstudent.misc;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import org.invincible.cosstudent.BuildConfig;

/**
 * Created by kshivang on 05/09/16.
 **/
public class UserLocalStore {

    private static final String KEY_EMAIL = "email";
    private static final String KEY_UID = "uid";
    private static final String KEY_CAMPUS_ID = "campus_id";
    private static final String KEY_FIRST_RUN = "firstrun";
    private static final String KEY_LOGGED_IN = "loggedIn";
    private static final String KEY_STUDENT = "student";

    private SharedPreferences userLocalDatabase;

    public UserLocalStore(Context context) {
        userLocalDatabase = context.getSharedPreferences(BuildConfig.APPLICATION_ID, 0);
    }

    public void setFirstRun(boolean firstRun) {
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putBoolean(KEY_FIRST_RUN,firstRun);
        spEditor.apply();
    }

    public void setStudentModel(StudentModel studentModel) {
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        Gson gson = new Gson();
        String studentInfo = gson.toJson(studentModel, StudentModel.class);
        spEditor.putString(KEY_STUDENT, studentInfo);
        spEditor.apply();
    }

    public StudentModel getStudentModel() {
        String profileInfo = userLocalDatabase.getString(KEY_STUDENT,"");
        Gson gson = new Gson();
        try {
            return gson.fromJson(profileInfo, StudentModel.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void setID(String uId, String campusID) {
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putString(KEY_UID, uId);
        spEditor.putString(KEY_CAMPUS_ID, campusID);
        spEditor.apply();
    }

    public void clearUserData() {
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.clear();
        spEditor.apply();
    }

    public String getUid() {
        return userLocalDatabase.getString(KEY_UID, "");
    }

    public String getCampusID() {
        return userLocalDatabase.getString(KEY_CAMPUS_ID, "");
    }

    public void setLoggedIn(boolean b) {
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putBoolean("loggedIn",b);
        spEditor.apply();
    }


    public String getEmail() {
        return userLocalDatabase.getString(KEY_EMAIL, "");
    }

    public boolean isFirstRun() {
        return userLocalDatabase.getBoolean(KEY_FIRST_RUN,true);
    }

    public boolean isLoggedIn() {
        return userLocalDatabase.getBoolean(KEY_LOGGED_IN,false);
    }
}
