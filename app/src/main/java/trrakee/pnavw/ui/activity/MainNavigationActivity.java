package trrakee.pnavw.ui.activity;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;

import org.altbeacon.beacon.BeaconManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import trrakee.pnavw.BeaconLocatorApp;
import trrakee.pnavw.R;
import trrakee.pnavw.model.TrackedBeacon;
import trrakee.pnavw.receiver.BeaconAlertReceiver;
import trrakee.pnavw.receiver.BeaconRegionReceiver;
import trrakee.pnavw.receiver.LocationReceiver;
import trrakee.pnavw.ui.fragment.BeaconFragment;
import trrakee.pnavw.ui.fragment.DetectedBeaconsFragment;
import trrakee.pnavw.ui.fragment.ScanFragment;
import trrakee.pnavw.ui.fragment.ScanRadarFragment;
import trrakee.pnavw.ui.fragment.TrackedBeaconsFragment;
import trrakee.pnavw.util.Constants;
import trrakee.pnavw.util.DialogBuilder;

public class MainNavigationActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, BeaconFragment.OnTrackedBeaconSelectedListener {

    public static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    BeaconManager mBeaconManager;

    BeaconRegionReceiver mRegionReceiver;
    BeaconAlertReceiver mAlertReceiver;
    LocationReceiver mLocationReceiver;

    TrackedBeacon mSelectedBeacon;
    private Unbinder unbinder;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, MainNavigationActivity.class);
    }

    @OnClick(R.id.fab)
    void navAction() {

        Fragment currentFragment = getFragmentInstance(R.id.content_frame);
        String tag = currentFragment.getTag();
        if (tag != null) {
            switch (tag) {
                case Constants.TAG_FRAGMENT_SCAN_LIST:
                case Constants.TAG_FRAGMENT_SCAN_RADAR:
                    ((ScanFragment) currentFragment).scanStartStopAction();
                    break;
                case Constants.TAG_FRAGMENT_TRACKED_BEACON_LIST:
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_navigation);
        unbinder = ButterKnife.bind(this);
        setupToolbar();
        navigationView.setNavigationItemSelectedListener(this);
        mBeaconManager = BeaconLocatorApp.from(this).getComponent().beaconManager();
        checkPermissions();
        verifyBluetooth();
        readExtras();
        registerReceivers();
        if (null == savedInstanceState) {
            if (mSelectedBeacon != null) {
                launchManagedListView();
            } else {
                launchScanBeaconView();
            }
        }
    }

    void registerReceivers() {
        mRegionReceiver = new BeaconRegionReceiver();
        mLocationReceiver = new LocationReceiver();
        mAlertReceiver = new BeaconAlertReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mRegionReceiver, new IntentFilter(Constants.NOTIFY_BEACON_ENTERS_REGION));
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mRegionReceiver, new IntentFilter(Constants.NOTIFY_BEACON_LEAVES_REGION));
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mRegionReceiver, new IntentFilter(Constants.NOTIFY_BEACON_NEAR_YOU_REGION));
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mLocationReceiver, new IntentFilter(Constants.GET_CURRENT_LOCATION));
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mAlertReceiver, new IntentFilter(Constants.ALARM_NOTIFICATION_SHOW));
    }

    void unregisterReceivers() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegionReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocationReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mAlertReceiver);
    }

    @Override
    protected void onDestroy() {
        unregisterReceivers();
        unbinder.unbind();
        super.onDestroy();
    }

    protected void readExtras() {
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            mSelectedBeacon = intent.getExtras().getParcelable(Constants.ARG_BEACON);
        }
    }

    private void setupToolbar() {

        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.nav_drawer_open,
                R.string.nav_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(Constants.TAG, "coarse location permission granted");
                } else {
                    final Dialog permFailedDialog = DialogBuilder.createSimpleOkErrorDialog(
                            this,
                            getString(R.string.dialog_error_functionality_limited),
                            getString(R.string.error_message_location_access_not_granted)
                    );
                    permFailedDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            //finish();
                        }
                    });
                    permFailedDialog.show();
                }
            }
        }
    }

    @TargetApi(23)
    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final Dialog permDialog = DialogBuilder.createSimpleOkErrorDialog(
                        this,
                        getString(R.string.dialog_error_need_location_access),
                        getString(R.string.error_message_location_access_need_tobe_granted)
                );
                permDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @TargetApi(23)
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                permDialog.show();
            }
        }
    }

    @TargetApi(18)
    private void verifyBluetooth() {
        try {
            if (!mBeaconManager.checkAvailability()) {

                final Dialog bleDialog = DialogBuilder.createSimpleOkErrorDialog(
                        this,
                        getString(R.string.dialog_error_ble_not_enabled),
                        getString(R.string.error_message_please_enable_bluetooth)
                );
                bleDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                        System.exit(0);
                    }
                });
                bleDialog.show();

            }
        } catch (RuntimeException e) {

            final Dialog bleDialog = DialogBuilder.createSimpleOkErrorDialog(
                    this,
                    getString(R.string.dialog_error_ble_not_supported),
                    getString(R.string.error_message_bluetooth_le_not_supported)
            );
            bleDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    finish();
                    System.exit(0);
                }
            });
            bleDialog.show();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation ui item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_scan_radar:
                launchRadarScanView();
                break;
            case R.id.nav_scan_around:
                launchScanBeaconView();
                break;
            case R.id.nav_settings:
                launchSettingsActivity();
                break;
            case R.id.nav_managed_list:
                launchManagedListView();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void createOrResumeFragment(String fragmentTag) {
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        boolean fragmentPopped = false;
        Fragment fragment = fragmentManager.findFragmentByTag(fragmentTag);
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentPopped = fragmentManager.popBackStackImmediate(fragmentTag, 0);
        }
        if (!fragmentPopped && fragment == null) {
            //Create an new instance if it is null and add it to stack
            switch (fragmentTag) {
                case Constants.TAG_FRAGMENT_SCAN_LIST:
                    fragment = DetectedBeaconsFragment.newInstance();
                    break;
                case Constants.TAG_FRAGMENT_SCAN_RADAR:
                    fragment = ScanRadarFragment.newInstance();
                    break;
                case Constants.TAG_FRAGMENT_TRACKED_BEACON_LIST:
                    fragment = TrackedBeaconsFragment.newInstance();
                    if (mSelectedBeacon != null) {
                        Bundle bundles = new Bundle();
                        bundles.putParcelable(Constants.ARG_BEACON, mSelectedBeacon);
                        fragment.setArguments(bundles);
                    }
                    break;
            }
            ft.addToBackStack(fragmentTag);
        }
        assert fragment != null;
        ft.replace(R.id.content_frame, fragment, fragmentTag);

        ft.commit();

        fragmentManager.executePendingTransactions();

    }

    private void addScanBeaconFragment() {
        createOrResumeFragment(Constants.TAG_FRAGMENT_SCAN_LIST);
    }

    private void addRadarScanFragment() {
        createOrResumeFragment(Constants.TAG_FRAGMENT_SCAN_RADAR);
    }

    private void addTrackedListFragment() {
        createOrResumeFragment(Constants.TAG_FRAGMENT_TRACKED_BEACON_LIST);
    }

    public void hideFab() {
        fab.clearAnimation();
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.hide_fab);
        fab.startAnimation(animation);

        CoordinatorLayout.LayoutParams params =
                (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        FloatingActionButton.Behavior behavior =
                (FloatingActionButton.Behavior) params.getBehavior();

        if (behavior != null) {
            behavior.setAutoHideEnabled(false);
        }

        fab.hide();
    }

    public void swappingFabUp() {
        fab.clearAnimation();
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.pop_up);
        fab.startAnimation(animation);
        fab.show();
        CoordinatorLayout.LayoutParams params =
                (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        FloatingActionButton.Behavior behavior =
                (FloatingActionButton.Behavior) params.getBehavior();

        if (behavior != null) {
            behavior.setAutoHideEnabled(true);
        }
    }


    public void swappingFloatingScanIcon(boolean isScanning) {
        if (isScanning) {
            Animation mAnimation = new AlphaAnimation(1, 0);
            mAnimation.setDuration(200);
            mAnimation.setInterpolator(new LinearInterpolator());
            mAnimation.setRepeatCount(Animation.INFINITE);
            mAnimation.setRepeatMode(Animation.REVERSE);
            fab.startAnimation(mAnimation);
        } else {
            fab.clearAnimation();
        }
    }

    public void swappingFloatingIcon() {
        Fragment currentFragment = getFragmentInstance(R.id.content_frame);
        String tag = currentFragment.getTag();
        assert tag != null;
        switch (tag) {
            case Constants.TAG_FRAGMENT_SCAN_LIST:
            case Constants.TAG_FRAGMENT_SCAN_RADAR:
                swappingFabUp();
                break;
            default:
                hideFab();
        }
    }

    private void launchScanBeaconView() {
        addScanBeaconFragment();
    }

    private void launchRadarScanView() {
        addRadarScanFragment();
    }

    private void launchManagedListView() {
        addTrackedListFragment();
    }

    @Override
    public void onBeaconSelected(TrackedBeacon beacon) {
        mSelectedBeacon = beacon;
        launchManagedListView();
    }
}
