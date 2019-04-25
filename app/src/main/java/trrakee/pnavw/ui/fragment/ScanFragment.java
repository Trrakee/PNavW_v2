package trrakee.pnavw.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;
import java.util.Objects;

import trrakee.pnavw.BeaconLocatorApp;
import trrakee.pnavw.ui.activity.MainNavigationActivity;
import trrakee.pnavw.util.Constants;
import trrakee.pnavw.util.PreferencesUtil;


/**
 * Created by Tushar Sharma on 8/11/15.
 */
public abstract class ScanFragment extends BeaconFragment implements BeaconConsumer, RangeNotifier {

    final static String STATE_SCANNING = "STATE_SCANNING";

    protected Region mRegion;
    protected boolean isReadyForScan;
    protected boolean isScanning;
    protected BeaconManager mBeaconManager;
    protected boolean needContinueScan;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mBeaconManager = BeaconLocatorApp.from(Objects.requireNonNull(getActivity())).getComponent().beaconManager();
        }
        mRegion = new Region(PreferencesUtil.getDefaultRegionName(getApplicationContext()), null, null, null);
        mBeaconManager.bind(this);
        mBeaconManager.addRangeNotifier(this);

        if (savedInstanceState != null) {
            needContinueScan = savedInstanceState.getBoolean(STATE_SCANNING);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mBeaconManager != null && mBeaconManager.isBound(this)) {
            mBeaconManager.unbind(this);
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() instanceof MainNavigationActivity) {
            ((MainNavigationActivity) getActivity()).swappingFloatingScanIcon(isScanning);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getActivity() instanceof MainNavigationActivity) {
            ((MainNavigationActivity) getActivity()).swappingFabUp();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        stopScan();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mBeaconManager.isBound(this)) mBeaconManager.setBackgroundMode(false);
    }

    @Override
    public void onPause() {
        //if (mBeaconManager.isBound(this)) mBeaconManager.setBackgroundMode(PreferencesUtil.isBackgroundScan(getActivity()));
        super.onPause();
    }

    public void scanStartStopAction() {
        if (isScanning) {
            stopScan();
        } else {
            startScan();
        }
    }

    public void startScan() {
        try {
            if (isCanScan() & mBeaconManager.isBound(this)) {
                mBeaconManager.startRangingBeaconsInRegion(mRegion);
                isScanning = true;
                if (getActivity() instanceof MainNavigationActivity) {
                    ((MainNavigationActivity) getActivity()).swappingFloatingScanIcon(isScanning);
                }
            }
        } catch (RemoteException e) {
            Log.d(Constants.TAG, "Start scan beacon problem", e);
        }
    }

    public void stopScan() {
        try {
            if (mBeaconManager.isBound(this)) {
                mBeaconManager.stopRangingBeaconsInRegion(mRegion);
            }
            isScanning = false;
            if (getActivity() instanceof MainNavigationActivity) {
                ((MainNavigationActivity) getActivity()).swappingFloatingScanIcon(isScanning);
            }
        } catch (RemoteException e) {
            Log.d(Constants.TAG, "Stop scan beacon problem", e);
        }
    }

    public abstract void onCanScan();

    public abstract void updateBeaconList(final Collection<Beacon> beacons);

    public abstract void updateBeaconList(final Collection<Beacon> beacons, final Region region);

    protected boolean isCanScan() {
        return isReadyForScan;
    }

    @Override
    public void onBeaconServiceConnect() {
        isReadyForScan = true;
        isScanning = false;
        onCanScan();

        if (needContinueScan) {
            scanStartStopAction();
        }
    }

    @Override
    public void didRangeBeaconsInRegion(final Collection<Beacon> beacons, final Region region) {
        if (beacons != null) {
            if (beacons.size() > 0 && region != null && region.equals(mRegion)) {
                updateBeaconList(beacons);
            } else {
                updateBeaconList(beacons, region);
            }
        }
    }

    @Override
    public Context getApplicationContext() {
        return getActivity().getApplication();
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {
        Log.d(Constants.TAG, "scan fragment unbound from beacon service");
        if (mBeaconManager.isBound(this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Objects.requireNonNull(getActivity()).unbindService(serviceConnection);
            }
        }
        isReadyForScan = false;
        isScanning = false;
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        Log.d(Constants.TAG, "scan fragment bound to beacon service");
        return getActivity().bindService(intent, serviceConnection, i);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_SCANNING, isScanning);
    }
}