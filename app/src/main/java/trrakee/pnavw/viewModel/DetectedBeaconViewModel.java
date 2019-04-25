package trrakee.pnavw.viewModel;

import android.support.annotation.NonNull;

import trrakee.pnavw.model.IManagedBeacon;
import trrakee.pnavw.model.TrackedBeacon;
import trrakee.pnavw.ui.fragment.BeaconFragment;

/**
 * Created by Tushar Sharma on 03/26/2019.
 */
public class DetectedBeaconViewModel extends BeaconViewModel {

    public DetectedBeaconViewModel(@NonNull BeaconFragment fragment, @NonNull IManagedBeacon managedBeacon) {
        super(fragment, managedBeacon);
    }

    protected void clickBeacon() {
        mFragment.selectBeacon(new TrackedBeacon(mManagedBeacon));
    }
}
