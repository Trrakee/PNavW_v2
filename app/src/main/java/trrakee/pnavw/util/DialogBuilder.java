package trrakee.pnavw.util;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;

import trrakee.pnavw.R;

/**
 * Created by Tushar Sharma on 03/26/2019.
 */
public final class DialogBuilder {

    private DialogBuilder() {
    }

    public static Dialog createSimpleOkErrorDialog(Context context, String title, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton(R.string.dialog_action_ok, null);
        return alertDialog.create();
    }

}
