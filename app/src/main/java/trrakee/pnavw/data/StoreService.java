package trrakee.pnavw.data;

import java.util.List;

import trrakee.pnavw.model.ActionBeacon;
import trrakee.pnavw.model.TrackedBeacon;

/**
 * Created by Tushar Sharma on 03/26/2019.
 */
public interface StoreService {

    boolean createBeacon(final TrackedBeacon beacon);

    boolean updateBeacon(final TrackedBeacon beacon);

    boolean deleteBeacon(final String id, boolean cascade);

    TrackedBeacon getBeacon(final String id);

    List<TrackedBeacon> getBeacons();

    void updateBeaconDistance(final String id, double distance);

    boolean updateBeaconAction(ActionBeacon beacon);

    boolean createBeaconAction(ActionBeacon beacon);

    List<ActionBeacon> getBeaconActions(final String beaconId);

    boolean deleteBeaconAction(final int id);

    void deleteBeaconActions(final String beaconId);

    List<ActionBeacon> getAllEnabledBeaconActions();

    boolean updateBeaconActionEnable(final int id, boolean enable);

    List<ActionBeacon> getEnabledBeaconActionsByEvent(final int eventType, final String beaconId);

    boolean isBeaconExists(String id);
}
