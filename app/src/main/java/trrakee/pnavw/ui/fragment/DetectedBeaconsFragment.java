package trrakee.pnavw.ui.fragment;

import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ProgressBar;

import org.altbeacon.beacon.Beacon;

import java.util.Collection;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import trrakee.pnavw.R;
import trrakee.pnavw.model.IManagedBeacon;
import trrakee.pnavw.model.TrackedBeacon;
import trrakee.pnavw.ui.adapter.BeaconAdapter;
import trrakee.pnavw.ui.adapter.DetectedBeaconAdapter;
import trrakee.pnavw.ui.view.ContextMenuRecyclerView;
import trrakee.pnavw.ui.view.DividerItemDecoration;
import trrakee.pnavw.util.Constants;
import trrakee.pnavw.util.PreferencesUtil;


/**
 * Created by Tushar Sharma on 03/26/2019.
 */
public class DetectedBeaconsFragment extends ScanFragment implements BeaconAdapter.OnBeaconLongClickListener {

    //40 sec timeout for scanning
    static final int SCAN_TIMEOUT = 40000;
    protected CountDownTimer mTimer;
    @BindView(R.id.recycler_detected_beacons)
    ContextMenuRecyclerView mListBeacons;
    @BindView(R.id.progress_indicator)
    ProgressBar mProgressBar;
    @BindView(R.id.empty_scan_view)
    ViewStub mEmpty;
    EmptyView mEmptyView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    private DetectedBeaconAdapter mBeaconsAdapter;

    public static DetectedBeaconsFragment newInstance() {
        return new DetectedBeaconsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBeaconsAdapter = new DetectedBeaconAdapter(this);
        mBeaconsAdapter.setOnBeaconLongClickListener(this);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_scan_beacons, container, false);
        unbinder = ButterKnife.bind(this, fragmentView);

        setupToolbar();
        setupRecyclerView();
        setupTimer();

        return fragmentView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTimer.cancel();
        unbinder.unbind();
    }

    private void setupToolbar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ((AppCompatActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(mToolbar);
        }
        ActionBar actionBar = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            actionBar = ((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar();
        }
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(R.string.title_fragment_detected_beacons);
        }
    }

    private void setupRecyclerView() {
        View viewFromEmpty = mEmpty.inflate();
        mEmptyView = new EmptyView(viewFromEmpty);
        mEmptyView.text.setText(getString(R.string.text_please_start_scan));

        mListBeacons.setLayoutManager(new LinearLayoutManager(getActivity()));
        mListBeacons.setHasFixedSize(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mListBeacons.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getActivity())));
        }
        mListBeacons.setAdapter(mBeaconsAdapter);

        registerForContextMenu(mListBeacons);

        mProgressBar.setVisibility(View.GONE);

    }

    private void setupTimer() {
        mTimer = new CountDownTimer(SCAN_TIMEOUT, PreferencesUtil.getManualScanTimeout(getApplicationContext())) {
            public void onFinish() {
                stopScanTimeout();
            }

            public void onTick(long tick) {
            }
        };
    }

    private void stopScanTimeout() {
        stopScan();
        mEmptyView.text.setText(getString(R.string.text_scan_not_found));
    }

    private void emptyListSetup() {
        if (mEmpty != null) {
            if (mBeaconsAdapter != null && mBeaconsAdapter.getItemCount() == 0) {
                mEmpty.setVisibility(View.VISIBLE);
                mEmptyView.text.setText(getString(R.string.text_please_start_scan));
            } else {
                mEmpty.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void startScan() {
        mProgressBar.setVisibility(View.VISIBLE);
        mEmpty.setVisibility(View.GONE);
        mBeaconsAdapter.removeAll();
        mTimer.start();
        super.startScan();
    }

    @Override
    public void stopScan() {
        mProgressBar.setVisibility(View.GONE);
        super.stopScan();
        emptyListSetup();
    }

    @Override
    public void onCanScan() {
        emptyListSetup();
    }

    @Override
    public void updateBeaconList(final Collection<Beacon> beacons, final org.altbeacon.beacon.Region region) {
        if (getActivity() != null) {
            //update list, even nothing, we want update last seen time on detected beacons
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    if (getActivity() == null)
                        return;

                    mBeaconsAdapter.notifyDataSetChanged();
                    Log.d(Constants.TAG, "called on region " + region.toString());
                }
            });
        }
    }

    @Override
    public void updateBeaconList(final Collection<Beacon> beacons) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    if (getActivity() == null)
                        return;

                    mBeaconsAdapter.insertBeacons(beacons);
                    mBeaconsAdapter.sort(PreferencesUtil.getScanBeaconSort(getApplicationContext()));
                    mTimer.cancel();
                }
            });
        }
    }


    @Override
    public void onBeaconLongClick(int position) {
        mListBeacons.openContextMenu(position);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo
            menuInfo) {

        super.onCreateContextMenu(menu, view, menuInfo);

        MenuInflater inflater = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            inflater = Objects.requireNonNull(getActivity()).getMenuInflater();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(inflater).inflate(R.menu.menu_detected_list, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ContextMenuRecyclerView.RecyclerContextMenuInfo info = (ContextMenuRecyclerView
                .RecyclerContextMenuInfo) item
                .getMenuInfo();

        switch (item.getItemId()) {
            case R.id.action_manage_add:
                selectBeacon(new TrackedBeacon((IManagedBeacon) mBeaconsAdapter.getItem(info.position)));
                return true;
            case R.id.action_filter_add:
                //TODO
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}