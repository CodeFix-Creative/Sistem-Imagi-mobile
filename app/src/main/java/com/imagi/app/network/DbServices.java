package com.imagi.app.network;

import android.content.Context;

import com.imagi.app.util.Constant;


public class DbServices {

    public Context mContext;

    public  DbServices(Context ctx){
        this.mContext = ctx;
    }

    public void logout() {
        mContext.getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(Constant.SP_TOKEN, null).putString(Constant.SP_USER, null).apply();
    }


}
