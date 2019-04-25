package trrakee.pnavw.ui.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Objects;

import trrakee.pnavw.BeaconLocatorApp;
import trrakee.pnavw.data.DataManager;
import trrakee.pnavw.model.ActionBeacon;
import trrakee.pnavw.util.Constants;

/**
 * Created by Tushar Sharma on 03/26/2019.
 */
public abstract class PageBeaconFragment extends PreferenceFragmentCompat {

    protected DataManager mDataManager;
    protected ActionBeacon mActionBeacon;
    protected int mPage;

    abstract public void onCreatePreferences(Bundle savedInstanceState, String rootKey);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mDataManager = BeaconLocatorApp.from(Objects.requireNonNull(getActivity())).getComponent().dataManager();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = super.onCreateView(inflater, container, savedInstanceState);

        readArguments();
        setData();

        return fragmentView;
    }

    protected abstract void setData();

    protected void readArguments() {
        if (getArguments() != null) {
            mPage = getArguments().getInt(Constants.ARG_PAGE);
            mActionBeacon = getArguments().getParcelable(Constants.ARG_ACTION_BEACON);
        }
    }

    protected boolean updateActionBeacon() {
        return mDataManager.updateBeaconAction(mActionBeacon);
    }


}
