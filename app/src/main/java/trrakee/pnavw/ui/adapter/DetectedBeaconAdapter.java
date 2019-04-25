package trrakee.pnavw.ui.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.altbeacon.beacon.Beacon;

import java.util.Collection;

import trrakee.pnavw.R;
import trrakee.pnavw.databinding.ItemDetectedBeaconBinding;
import trrakee.pnavw.model.DetectedBeacon;
import trrakee.pnavw.ui.fragment.BeaconFragment;
import trrakee.pnavw.viewModel.DetectedBeaconViewModel;

/**
 * Created by Tushar Sharma on 03/26/2019.
 */
public class DetectedBeaconAdapter extends BeaconAdapter<DetectedBeaconAdapter.BindingHolder> {

    public DetectedBeaconAdapter(BeaconFragment fragment) {
        mFragment = fragment;
    }

    @Override
    public BindingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDetectedBeaconBinding beaconBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_detected_beacon,
                parent,
                false);
        return new BindingHolder(beaconBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull BindingHolder holder, final int position) {
        ItemDetectedBeaconBinding beaconBinding = holder.binding;
        holder.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onBeaconLongClickListener != null) {
                    onBeaconLongClickListener.onBeaconLongClick(position);
                }
                return false;
            }
        });
        beaconBinding.setViewModel(new DetectedBeaconViewModel(mFragment, (DetectedBeacon) getItem(position)));
    }


    public void insertBeacons(Collection<Beacon> beacons) {
        for (Beacon beacon : beacons) {
            DetectedBeacon dBeacon = new DetectedBeacon(beacon);
            dBeacon.setTimeLastSeen(System.currentTimeMillis());
            this.mBeacons.put(dBeacon.getId(), dBeacon);
        }
        notifyDataSetChanged();
    }

    static class BindingHolder extends RecyclerView.ViewHolder {
        private ItemDetectedBeaconBinding binding;

        BindingHolder(ItemDetectedBeaconBinding binding) {
            super(binding.contentView);
            this.binding = binding;
        }

        void setOnLongClickListener(View.OnLongClickListener listener) {
            binding.contentView.setOnLongClickListener(listener);
        }
    }
}