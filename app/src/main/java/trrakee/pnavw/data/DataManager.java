package trrakee.pnavw.data;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import trrakee.pnavw.BeaconLocatorApp;
import trrakee.pnavw.injection.component.DaggerDataComponent;
import trrakee.pnavw.injection.module.DataModule;
import trrakee.pnavw.model.ActionBeacon;
import trrakee.pnavw.model.TrackedBeacon;

/**
 * Created by Tushar Sharma on 03/26/2019.
 */
public class DataManager {

    @Inject
    StoreService mStoreService;
    private List<ActionBeacon> mActionBeaconCache = new ArrayList<>();

    public DataManager(Context context) {
        injectDependencies(context);
    }

    private void injectDependencies(Context context) {
        DaggerDataComponent.builder()
                .applicationComponent(BeaconLocatorApp.from(context).getComponent())
                .dataModule(new DataModule(context))
                .build()
                .inject(this);
    }

    public boolean createBeacon(TrackedBeacon beacon) {
        return mStoreService.createBeacon(beacon);
    }

    public boolean updateBeacon(TrackedBeacon beacon) {
        return mStoreService.updateBeacon(beacon);
    }

    public List<TrackedBeacon> getAllBeacons() {
        return mStoreService.getBeacons();
    }

    public boolean createBeaconAction(ActionBeacon beacon) {
        return mStoreService.createBeaconAction(beacon);
    }

    public boolean updateBeaconAction(ActionBeacon beacon) {
        return mStoreService.updateBeaconAction(beacon);
    }

    public boolean deleteBeaconAction(int id) {
        return mStoreService.deleteBeaconAction(id);
    }

    public boolean deleteBeacon(String beaconId, boolean cascade) {
        return mStoreService.deleteBeacon(beaconId, cascade);
    }

    public List<ActionBeacon> getAllEnabledBeaconActions() {
        List<ActionBeacon> actions = mStoreService.getAllEnabledBeaconActions();
        mActionBeaconCache.clear();
        mActionBeaconCache.addAll(actions);
        return actions;
    }

    public boolean enableBeaconAction(int id, boolean enable) {
        return mStoreService.updateBeaconActionEnable(id, enable);
    }

    public List<ActionBeacon> getEnabledBeaconActionsByEvent(ActionBeacon.EventType eventType, String beaconId) {
        if (!mActionBeaconCache.isEmpty()) {
            List<ActionBeacon> actionBeacons = new ArrayList<>();
            for (ActionBeacon action : mActionBeaconCache) {
                if (action.getBeaconId().equals(beaconId) && action.getEventType() == eventType) {
                    actionBeacons.add(action);
                }
            }
            if (actionBeacons.size() > 0) {
                return actionBeacons;
            }
        }
        return mStoreService.getEnabledBeaconActionsByEvent(eventType.getValue(), beaconId);
    }

    public TrackedBeacon getBeacon(String id) {
        return mStoreService.getBeacon(id);
    }

    public void updateBeaconDistance(final String id, double distance) {
        mStoreService.updateBeaconDistance(id, distance);
    }

    public boolean isBeaconExists(String id) {
        return mStoreService.isBeaconExists(id);
    }
}