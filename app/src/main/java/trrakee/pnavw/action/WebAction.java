package trrakee.pnavw.action;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import trrakee.pnavw.R;
import trrakee.pnavw.model.NotificationAction;

/**
 * Created by Tushar Sharma on 03/26/2019.
 */
public class WebAction extends NoneAction {

    WebAction(String param, NotificationAction notification) {
        super(param, notification);
    }

    @Override
    public String execute(Context context) {
        try {
            Uri uri = Uri.parse(param);
            Intent newIntent = new Intent(android.content.Intent.ACTION_VIEW, uri);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(newIntent);
        } catch (Exception e) {
            return context.getString(R.string.action_urlaction_error);
        }
        return super.execute(context);
    }

    @Override
    public boolean isParamRequired() {
        return true;
    }

    @Override
    public String toString() {
        return "WebAction, url: " + param;
    }
}
