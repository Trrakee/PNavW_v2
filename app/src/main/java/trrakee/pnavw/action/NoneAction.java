package trrakee.pnavw.action;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import trrakee.pnavw.R;
import trrakee.pnavw.model.NotificationAction;
import trrakee.pnavw.util.Constants;

/**
 * Created by Tushar Sharma on 03/26/2019.
 */
public class NoneAction extends Action {

    protected final String param;
    protected final NotificationAction notification;


    public NoneAction(String param, NotificationAction notification) {
        this.param = param;
        this.notification = notification;
    }


    @Override
    public String execute(Context context) {
        if (isParamRequired() && isParamEmpty()) {
            return context.getString(R.string.action_action_param_is_required);
        }

        //empty
        sendAlarm(context);
        return null;
    }

    @Override
    public boolean isParamRequired() {
        return false;
    }

    private void sendAlarm(Context context) {
        if (isNotificationRequired()) {
            Intent newIntent = new Intent(Constants.ALARM_NOTIFICATION_SHOW);
            newIntent.putExtra("NOTIFICATION", notification);
            LocalBroadcastManager.getInstance(context).sendBroadcast(newIntent);
        }
    }

    private boolean isParamEmpty() {
        return param == null || param.isEmpty();
    }

    private boolean isNotificationRequired() {
        return notification != null && notification.isEnabled();
    }

    @Override
    public String toString() {
        return "NoneAction, action: " + param;
    }
}