package com.dabois.utilities;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    private static SharedPreferences sharedPreferences;

    public PreferenceManager(Context context){
        sharedPreferences = context.getSharedPreferences(Constants.KEY_PREF,Context.MODE_PRIVATE);

    }



    public void putBoolean(String key,Boolean val){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key,val);
        editor.apply();
    }

    public boolean getBoolean(String key){
        return sharedPreferences.getBoolean(key,false);
    }

    public void putString(String key,String val){
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(key,val);
        editor.apply();
    }

    public static String getString(String key){
        return sharedPreferences.getString(key,null);
    }
    public static void Clear(){
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.clear();
        editor.apply();

    }
}
