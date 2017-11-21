package com.makhovyk.android.githubsearch.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String ACCESS_TOKEN = "accessToken";
    private static final String ACCESS_CODE = "accessCode";
    private static final String NAME = "name";
    private static final String EMAIL = "email";
    private static final String PHOTO = "photo";
    private static final String PREFER_NAME = "UserSession";

    private SharedPreferences mSharedPreferences;
    private Context mContext;
    private SharedPreferences.Editor mEditor;

    public SessionManager(Context context) {

        mContext = context;
        mSharedPreferences = mContext.getSharedPreferences(PREFER_NAME,Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    public void logOut(){
        mEditor.clear();
        mEditor.commit();
    }

    public void setAccessToken(String token){
        mEditor.putString(ACCESS_TOKEN, token);
        mEditor.commit();
    }

    public void setAccessCode(String code){
        mEditor.putString(ACCESS_CODE, code);
        mEditor.commit();
    }

    public void setName(String name){
        mEditor.putString(NAME,name);
        mEditor.commit();
    }
    public void setEmail(String email){
        mEditor.putString(EMAIL,email);
        mEditor.commit();
    }
    public void setPhoto(String photo){
        mEditor.putString(PHOTO,photo);
        mEditor.commit();
    }

    public String getName() {
        return mSharedPreferences.getString(NAME,null);
    }

    public String getEmail() {
        return mSharedPreferences.getString(EMAIL,null);
    }

    public String getPhoto() {
        return mSharedPreferences.getString(PHOTO,null);
    }

    public String getAccessToken(){
        return mSharedPreferences.getString(ACCESS_TOKEN,null);
    }

    public String getAccessCode(){
        return mSharedPreferences.getString(ACCESS_CODE,null);
    }

    public boolean isLoggedIn(){
        return getAccessToken() != null;
    }
}
