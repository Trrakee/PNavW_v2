package trrakee.pnavw.action;


import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import trrakee.pnavw.model.NotificationAction;
import trrakee.pnavw.util.Constants;

/**
 * Created by Tushar Sharma on 03/26/2019.
 */
public class LocationAction extends NoneAction {
    LocationAction(String param, NotificationAction notification) {
        super(param, notification);
    }

    @Override
    public String execute(Context context) {
        Intent newIntent = new Intent(Constants.GET_CURRENT_LOCATION);
        LocalBroadcastManager.getInstance(context).sendBroadcast(newIntent);

        return super.execute(context);
    }

    @Override
    public boolean isParamRequired() {
        return false;
    }

    @Override
    public String toString() {
        return "LocationAction, param: " + param;
    }
}
