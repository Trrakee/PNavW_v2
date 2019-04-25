package trrakee.pnavw.model;

import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tushar Sharma on 03/26/2019.
 */
public class ActionRegion {

    public static Region parseRegion(ActionBeacon actionBeacon) {
        if (actionBeacon == null) {
            throw new IllegalArgumentException("ActionBeacon object is null");
        }
        String[] indents = actionBeacon.getBeaconId().split(";");
        if (indents.length < 3) {
            throw new IllegalArgumentException("ActionBeacon has invalid id");
        }
        List<Identifier> identifiers = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            identifiers.add(Identifier.parse(indents[i]));
        }
        return new Region(RegionName.buildRegionNameId(actionBeacon), identifiers);
    }

}
