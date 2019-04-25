package trrakee.pnavw.model;

import java.util.List;

/**
 * Created by Tushar Sharma on 03/26/2019.
 */
public interface ITriggerable {
    List<ActionBeacon> getActions();

    void addAction(ActionBeacon action);

    void addActions(List<ActionBeacon> actions);
}
