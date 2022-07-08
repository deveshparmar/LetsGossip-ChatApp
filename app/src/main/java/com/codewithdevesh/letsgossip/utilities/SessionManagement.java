package com.codewithdevesh.letsgossip.utilities;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManagement {
    private static SharedPreferences sharedPreferences;
    public SessionManagement() {
    }
    public static void init(Context context){
        if(sharedPreferences == null){
            sharedPreferences = context.getSharedPreferences("SessionName", Context.MODE_PRIVATE);
        }
    }
    public static void saveUserPhoneNo(String phoneNo){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("UserNumber",phoneNo);
        editor.apply();
    }
    public static String getUserPhoneNo(){
        return sharedPreferences.getString("UserNumber","");
    }

    public static void saveUserName(String username){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("UserName",username);
        editor.apply();
    }
    public static String getUserName(){
        return sharedPreferences.getString("UserName","");
    }
    public static void saveUserBio(String bio){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("UserBio",bio);
        editor.apply();
    }
    public static String getUserBio(){
        return sharedPreferences.getString("UserBio","");
    }
    public static void saveUserPic(String pic){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("UserPic",pic);
        editor.apply();
    }
    public static String getUserPic(){
        return sharedPreferences.getString("UserPic","");
    }

    public static void saveUserId(String id){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("UserId",id);
        editor.apply();
    }
    public static String getUserId(){
        return sharedPreferences.getString("UserId","");
    }
}
