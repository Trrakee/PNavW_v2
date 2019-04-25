package trrakee.pnavw.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import org.altbeacon.beacon.Region;

import java.util.List;

import trrakee.pnavw.BeaconLocatorApp;
import trrakee.pnavw.action.ActionExecutor;
import trrakee.pnavw.action.IAction;
import trrakee.pnavw.data.DataManager;
import trrakee.pnavw.model.ActionBeacon;
import trrakee.pnavw.model.RegionName;
import trrakee.pnavw.util.Constants;


/**
 * Created by Tushar Sharma on 03/26/2019.
 */
public class BeaconRegionReceiver extends BroadcastReceiver {

    ActionExecutor mActionExecutor;
    DataManager mDataManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        //TODO
        if (intent.hasExtra("REGION")) {
            Region region = intent.getParcelableExtra("REGION");
            if (region != null) {
                RegionName regionName = RegionName.parseString(region.getUniqueId());

                mDataManager = BeaconLocatorApp.from(context).getComponent().dataManager();
                List<ActionBeacon> actions = mDataManager.getEnabledBeaconActionsByEvent(regionName.getEventType(), regionName.getBeaconId());
                if (actions.size() > 0) {

                    mActionExecutor = BeaconLocatorApp.from(context).getComponent().actionExecutor();
                    for (ActionBeacon actionBeacon : actions) {
                        // load action from db
                        IAction action = ActionExecutor.actionBuilder(actionBeacon.getActionType(), actionBeacon.getActionParam(), actionBeacon.getNotification());
                        if (action != null) {
                            String resMessage = mActionExecutor.storeAndExecute(action);
                            if (resMessage != null) {
                                Toast.makeText(context, resMessage, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Log.w(Constants.TAG, "Action not found for " + actionBeacon);
                        }
                    }
                }
            }
        }
    }
}