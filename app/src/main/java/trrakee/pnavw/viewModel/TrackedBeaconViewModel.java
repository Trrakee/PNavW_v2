package trrakee.pnavw.viewModel;

import android.support.annotation.NonNull;
import android.view.View;

import trrakee.pnavw.model.IManagedBeacon;
import trrakee.pnavw.ui.fragment.BeaconFragment;
import trrakee.pnavw.ui.fragment.TrackedBeaconsFragment;

/**
 * Created by Tushar Sharma on 03/26/2019.
 */
public class TrackedBeaconViewModel extends BeaconViewModel {

    public TrackedBeaconViewModel(@NonNull BeaconFragment fragment, @NonNull IManagedBeacon managedBeacon) {
        super(fragment, managedBeacon);
    }

    public View.OnClickListener onClickBeaconDelete() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TrackedBeaconsFragment) mFragment).removeBeacon(mManagedBeacon.getId());
            }
        };
    }

    public View.OnClickListener onClickBeaconAdd() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TrackedBeaconsFragment) mFragment).newBeaconAction(mManagedBeacon.getId());
            }
        };
    }


}
