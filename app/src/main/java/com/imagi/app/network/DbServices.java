package com.imagi.app.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.imagi.app.model.User;
import com.imagi.app.util.Constant;


public class DbServices {

    public Context mContext;

    public  DbServices(Context ctx){
        this.mContext = ctx;
    }

    public void logout() {
//        mContext.getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE)
//                .edit().putString(Constant.SP_USER, null).apply();
//
//        mContext.getSharedPreferences(Constant.SP_TOKEN_USER, Context.MODE_PRIVATE)
//                .edit()
//                .putString(Constant.SP_TOKEN, null).apply();
        mContext.deleteSharedPreferences(Constant.SP_TOKEN_USER);
        mContext.deleteSharedPreferences(Constant.SP_NAME);

    }

    public String findToken(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constant.SP_TOKEN_USER, 0);
                return sharedPreferences.getString(Constant.SP_TOKEN, null);
    }

    public String findBearerToken(){
        return "Bearer " + findToken();
    }

    public User getUser() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE);
        if(sharedPreferences.contains(Constant.SP_USER)){
            Log.d("DATA_USER", "AVAILABLE");
            Log.d("DATA_USER", sharedPreferences.getString(Constant.SP_USER, null).toString());

        }


        String str = mContext.getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE)
                .getString(Constant.SP_USER, null);

        try{
            return new Gson().fromJson(str, User.class);
        }catch (Exception e){
            Log.e(Constant.TAG, str);
            return new User();
        }
    }


}
