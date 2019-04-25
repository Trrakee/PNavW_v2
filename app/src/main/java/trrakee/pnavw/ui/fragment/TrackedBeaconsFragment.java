package trrakee.pnavw.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ProgressBar;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import trrakee.pnavw.BeaconLocatorApp;
import trrakee.pnavw.R;
import trrakee.pnavw.data.DataManager;
import trrakee.pnavw.model.ActionBeacon;
import trrakee.pnavw.model.TrackedBeacon;
import trrakee.pnavw.ui.activity.MainNavigationActivity;
import trrakee.pnavw.ui.adapter.BeaconAdapter;
import trrakee.pnavw.ui.adapter.TrackedBeaconAdapter;
import trrakee.pnavw.ui.view.ContextMenuRecyclerView;
import trrakee.pnavw.util.Constants;

/**
 * Created by Tushar Sharma on 03/26/2019.
 */
public class TrackedBeaconsFragment extends BeaconFragment implements SwipeRefreshLayout.OnRefreshListener, BeaconAdapter.OnBeaconLongClickListener {

    @BindView(R.id.recycler_beacons)
    ContextMenuRecyclerView mListBeacons;
    @BindView(R.id.progress_indicator)
    ProgressBar mProgressBar;
    @BindView(R.id.empty_view)
    ViewStub mEmpty;
    EmptyView mEmptyView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    private TrackedBeaconAdapter mBeaconsAdapter;
    private DataManager mDataManager;

    public static TrackedBeaconsFragment newInstance() {
        return new TrackedBeaconsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mDataManager = BeaconLocatorApp.from(getActivity()).getComponent().dataManager();
        mBeaconsAdapter = new TrackedBeaconAdapter(this);
        mBeaconsAdapter.setOnBeaconLongClickListener(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_tracked_beacons, container, false);
        unbinder = ButterKnife.bind(this, fragmentView);

        setupToolbar();
        setupRecyclerView();

        return fragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getActivity() instanceof MainNavigationActivity) {
            ((MainNavigationActivity) getActivity()).hideFab();
        }
        loadBeacons();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
            actionBar.setTitle(R.string.title_fragment_tracked_beacons);
        }
    }

    private void setupRecyclerView() {
        View viewFromEmpty = mEmpty.inflate();
        mEmptyView = new EmptyView(viewFromEmpty);
        mEmptyView.text.setText(getString(R.string.text_empty_list_tracked_beacons));

        mListBeacons.setLayoutManager(new LinearLayoutManager(getActivity()));
        mListBeacons.setHasFixedSize(true);
        mListBeacons.setAdapter(mBeaconsAdapter);

        registerForContextMenu(mListBeacons);

        mProgressBar.setVisibility(View.GONE);

    }

    private void setupSwipe() {

        ItemTouchHelper swipeToDismissTouchHelper = new ItemTouchHelper(new UndoSwipableCallback(
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT));
        swipeToDismissTouchHelper.attachToRecyclerView(mListBeacons);
    }

    private void emptyListUpdate() {
        if (mBeaconsAdapter.getItemCount() == 0) {
            mEmpty.setVisibility(View.VISIBLE);
            mEmptyView.text.setText(getString(R.string.text_empty_list_tracked_beacons));
        } else {
            mEmpty.setVisibility(View.GONE);
        }
    }

    private void loadBeacons() {
        showLoadingViews();

        getExtras();

        mBeaconsAdapter.insertBeacons(mDataManager.getAllBeacons());

        emptyListUpdate();
        hideLoadingViews();
    }

    private void getExtras() {
        if (getArguments() != null && !getArguments().isEmpty()) {

            TrackedBeacon beacon = getArguments().getParcelable(Constants.ARG_BEACON);
            if (beacon != null) {
                if (!mBeaconsAdapter.isItemExists(beacon.getId())) {
                    if (!mDataManager.isBeaconExists(beacon.getId())) {
                        if (!mDataManager.createBeacon(beacon)) {
                            //TODO error
                        }
                    }
                } else {
                    //TODO make selection of updated
                    if (!mDataManager.updateBeacon(beacon)) {
                        //TODO error
                    }
                }
            }
        }
    }

    public void removeBeacon(String beaconId) {
        if (mDataManager.deleteBeacon(beaconId, true)) {
            mBeaconsAdapter.removeBeaconById(beaconId);
        }  //TODO error

        emptyListUpdate();
    }

    public void removeBeaconAction(String beaconId, int id) {
        if (mDataManager.deleteBeaconAction(id)) {
            mBeaconsAdapter.removeBeaconAction(beaconId, id);
        }  //TODO error

    }

    public void newBeaconAction(String beaconId) {
        String defName = getString(R.string.pref_bd_default_name);
        int actionCount = mBeaconsAdapter.getActionCount(beaconId);

        if (actionCount > 0) {
            defName += " (" + actionCount + ")";
        }

        ActionBeacon newAction = new ActionBeacon(beaconId, defName);
        if (mDataManager.createBeaconAction(newAction)) {
            mBeaconsAdapter.addBeaconAction(newAction);
        }  //FIXME error

    }

    public void enableBeaconAction(String beaconId, int id, boolean enable) {
        if (mDataManager.enableBeaconAction(id, enable)) {
            mBeaconsAdapter.enableAction(beaconId, id, enable);
        }  //TODO error

    }

    @Override
    public void onRefresh() {
        loadBeacons();
    }

    private void hideLoadingViews() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    private void showLoadingViews() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBeaconLongClick(int position) {
        mListBeacons.openContextMenu(position);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQ_UPDATED_ACTION_BEACON) {
            if (data != null && data.hasExtra(Constants.ARG_ACTION_BEACON)) {
                ActionBeacon actionBeacon = data.getParcelableExtra(Constants.ARG_ACTION_BEACON);
                if (actionBeacon != null) {
                    //TODO check if isDirty, now we store always even no changes
                    if (mDataManager.updateBeaconAction(actionBeacon)) {
                        mBeaconsAdapter.updateBeaconAction(actionBeacon);
                    }  //TODO error

                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo
            menuInfo) {

        super.onCreateContextMenu(menu, view, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_tracked_list, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        ContextMenuRecyclerView.RecyclerContextMenuInfo info = (ContextMenuRecyclerView
                .RecyclerContextMenuInfo) item
                .getMenuInfo();

        switch (item.getItemId()) {
            case R.id.action_add:
                newBeaconAction(mBeaconsAdapter.getBeacon(info.position).getId());
                return true;
            case R.id.action_delete:
                removeBeacon(mBeaconsAdapter.getBeacon(info.position).getId());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class UndoSwipableCallback extends ItemTouchHelper.SimpleCallback {

        UndoSwipableCallback(int dragDirs, int swipeDirs) {
            super(dragDirs, swipeDirs);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            mBeaconsAdapter.removeBeacon(viewHolder.getAdapterPosition());
        }

    }

}