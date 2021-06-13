package com.imagi.app.util

import android.app.AlertDialog
import android.content.Context

class AppUtils {

    companion object{

        fun showAlert(ctx:Context, text:String){
            AlertDialog.Builder(ctx).setMessage(text)
                .setPositiveButton("OK", {dialogInterface, i->})
                .create().show()
        }

    }
}