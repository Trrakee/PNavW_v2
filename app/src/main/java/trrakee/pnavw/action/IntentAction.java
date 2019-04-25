package trrakee.pnavw.action;

import android.content.Context;
import android.content.Intent;

import trrakee.pnavw.model.NotificationAction;

/**
 * Created by Tushar Sharma on 03/26/2019.
 */
public class IntentAction extends NoneAction {

    IntentAction(String param, NotificationAction notification) {
        super(param, notification);
    }

    @Override
    public String execute(Context context) {
        Intent newIntent = new Intent(param);
        context.sendBroadcast(newIntent);
        return super.execute(context);
    }

    @Override
    public boolean isParamRequired() {
        return true;
    }

    @Override
    public String toString() {
        return "IntentAction, action: " + param;
    }
}