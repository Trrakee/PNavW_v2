package trrakee.pnavw.ui.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.design.widget.SwipeDismissBehavior;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

import trrakee.pnavw.R;
import trrakee.pnavw.databinding.ItemTrackedBeaconBinding;
import trrakee.pnavw.model.ActionBeacon;
import trrakee.pnavw.model.IManagedBeacon;
import trrakee.pnavw.model.TrackedBeacon;
import trrakee.pnavw.ui.fragment.BeaconFragment;
import trrakee.pnavw.ui.fragment.TrackedBeaconsFragment;
import trrakee.pnavw.ui.view.WrapLinearLayoutManager;
import trrakee.pnavw.util.Constants;
import trrakee.pnavw.viewModel.TrackedBeaconViewModel;

/**
 * Created by Tushar Sharma on 03/26/2019.
 */
public class TrackedBeaconAdapter extends BeaconAdapter<TrackedBeaconAdapter.BindingHolder> {

    private Map<String, ActionBeaconAdapter> mActionAdapters = new HashMap<>();

    public TrackedBeaconAdapter(BeaconFragment fragment) {
        mFragment = fragment;
    }

    @Override
    public BindingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTrackedBeaconBinding beaconBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_tracked_beacon,
                parent,
                false);
        setupSwipe(beaconBinding);
        return new BindingHolder(beaconBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull BindingHolder holder, final int position) {
        ItemTrackedBeaconBinding beaconBinding = holder.binding;

        ActionBeaconAdapter adapter = new ActionBeaconAdapter((TrackedBeaconsFragment) mFragment);
        beaconBinding.recyclerActions.setLayoutManager(new WrapLinearLayoutManager(mFragment.getActivity()));
        beaconBinding.recyclerActions.setAdapter(adapter);

        TrackedBeacon beacon = (TrackedBeacon) getItem(position);
        adapter.setItems(beacon.getActions());

        mActionAdapters.put(beacon.getId(), adapter);

        holder.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onBeaconLongClickListener != null) {
                    onBeaconLongClickListener.onBeaconLongClick(position);
                }
                return false;
            }
        });

        beaconBinding.setViewModel(new TrackedBeaconViewModel(mFragment, beacon));
    }


    private void setupSwipe(ItemTrackedBeaconBinding beaconBinding) {

        final SwipeDismissBehavior<CardView> swipe = new SwipeDismissBehavior();
        swipe.setSwipeDirection(SwipeDismissBehavior.SWIPE_DIRECTION_ANY);
        swipe.setListener(new SwipeDismissBehavior.OnDismissListener() {
            @Override
            public void onDismiss(View view) {
                Log.d(Constants.TAG, "Swipe +");
            }

            @Override
            public void onDragStateChanged(int state) {
            }
        });

    }

    public int getActionCount(String beaconId) {
        ActionBeaconAdapter adapter = mActionAdapters.get(beaconId);
        if (adapter != null) {
            return adapter.getItemCount();
        }
        return 0;
    }

    public void removeBeaconAction(String beaconId, int id) {
        ActionBeaconAdapter adapter = mActionAdapters.get(beaconId);
        if (adapter != null) {
            adapter.removeItemById(id);
        }
    }

    public void addBeaconAction(ActionBeacon newAction) {
        ActionBeaconAdapter adapter = mActionAdapters.get(newAction.getBeaconId());
        if (adapter != null) {
            adapter.addItem(newAction);
        }
    }

    public void updateBeaconAction(ActionBeacon action) {
        ActionBeaconAdapter adapter = mActionAdapters.get(action.getBeaconId());
        if (adapter != null) {
            adapter.addItem(action);
        }
    }

    public IManagedBeacon getBeacon(int position) {
        return (IManagedBeacon) getItem(position);
    }

    public void enableAction(String beaconId, int id, boolean enable) {
        ActionBeaconAdapter adapter = mActionAdapters.get(beaconId);
        if (adapter != null) {
            ActionBeacon action = adapter.getItemById(id);
            if (action != null) {
                action.setIsEnabled(enable);
                adapter.addItem(action);
            }
        }
    }


    static class BindingHolder extends RecyclerView.ViewHolder {
        private ItemTrackedBeaconBinding binding;

        BindingHolder(ItemTrackedBeaconBinding binding) {
            super(binding.cardView);
            this.binding = binding;
        }

        void setOnLongClickListener(View.OnLongClickListener listener) {
            binding.cardView.setOnLongClickListener(listener);
        }
    }
}