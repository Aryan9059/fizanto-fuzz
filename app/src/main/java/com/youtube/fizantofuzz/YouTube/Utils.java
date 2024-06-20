package com.youtube.fizantofuzz.YouTube;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.WindowManager;
import com.youtube.fizantofuzz.R;

public class Utils {

    public static ProgressDialog showLoader(Context context){
            ProgressDialog dialog = new ProgressDialog(context);
            try {
                dialog.show();
            } catch (WindowManager.BadTokenException ignored) {

            }
            dialog.setCancelable(false);
            dialog.getWindow()
                    .setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(R.layout.dialog_progress_load);
            return dialog;
    }

}
