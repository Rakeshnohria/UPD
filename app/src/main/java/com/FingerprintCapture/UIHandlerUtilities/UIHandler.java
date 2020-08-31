package com.FingerprintCapture.UIHandlerUtilities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.FingerprintCapture.R;

public class UIHandler {

    public static AlertDialog.Builder dialog(Context context, String title, String message) {
        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false);
    }


    public static void showDialog(Context pContext, String pTitle, String pMessage) {
        AlertDialog.Builder dialogBuilder = UIHandler.dialog(pContext, pTitle, pMessage);
        dialogBuilder.setPositiveButton(pContext.getResources().getString(R.string.ok_message), (dialog, i) -> dialog.dismiss());
        dialogBuilder.show();
    }
    private static AlertDialog mAlertDialog;
    public static void showingAlert(String pMessage, final Context pContext) {
        if (pContext != null) {
            if (mAlertDialog == null || !mAlertDialog.isShowing()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(pContext);
                builder.setTitle(R.string.alert_dialog_title)
                        .setMessage(pMessage)
                        .setCancelable(true)
                        .setPositiveButton(pContext.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                mAlertDialog=null;
                            }
                        });
                mAlertDialog = builder.create();
                mAlertDialog.show();
            }
        }
    }

}
