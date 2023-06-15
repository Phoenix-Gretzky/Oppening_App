package com.example.openingapp.GlobalClasses;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import com.example.openingapp.R;

public class ViewDialog {

    private final Context context;
    private Dialog dialog;
    GlobalClassForFunctions objectForGlobalClassForFunction;

    //..we need the context else we can not create the dialog so get context in constructor
    public ViewDialog(Context context) {
        this.context = context;
    }



    // retun true if dialog is showing
    public boolean IsShowing(){
        if(dialog!=null)
            return dialog.isShowing();
        return false;
    }

    public void showDialog() {


        try {
            dialog = new Dialog(context);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            //...set cancelable false so that it's never get hidden
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);

            //...that's the layout i told you will inflate later
            dialog.setContentView(R.layout.loading_layout);

            objectForGlobalClassForFunction = GlobalClassForFunctions.getInstance();

            dialog.show();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    //..also create a method which will hide the dialog when some work is done
    public void hideDialog() {

        try {
            if (dialog != null) {
                dialog.hide();
                dialog.dismiss();
                dialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
