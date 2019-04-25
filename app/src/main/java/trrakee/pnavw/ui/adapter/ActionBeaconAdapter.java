package trrakee.pnavw.ui.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import trrakee.pnavw.R;
import trrakee.pnavw.databinding.ItemActionBeaconBinding;
import trrakee.pnavw.model.ActionBeacon;
import trrakee.pnavw.ui.fragment.TrackedBeaconsFragment;
import trrakee.pnavw.viewModel.ActionBeaconViewModel;

/**
 * Created by Tushar Sharma on 03/26/2019.
 */
public class ActionBeaconAdapter extends RecyclerView.Adapter<ActionBeaconAdapter.BindingHolder> {
    private List<ActionBeacon> mItemsList;
    private TrackedBeaconsFragment mFragment;

    ActionBeaconAdapter(TrackedBeaconsFragment fragment) {
        mFragment = fragment;
        mItemsList = new ArrayList<>();
    }

    @Override
    public BindingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemActionBeaconBinding beaconBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_action_beacon,
                parent,
                false);
        return new BindingHolder(beaconBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull BindingHolder holder, int position) {
        ItemActionBeaconBinding itemBinding = holder.binding;
        itemBinding.setViewModel(new ActionBeaconViewModel(mFragment, mItemsList.get(position)));
    }

    @Override
    public int getItemCount() {
        return mItemsList.size();
    }

    void setItems(List<ActionBeacon> itemsList) {
        this.mItemsList = itemsList;
        notifyDataSetChanged();
    }

    void addItem(ActionBeacon itemsList) {
        if (!this.mItemsList.contains(itemsList)) {
            this.mItemsList.add(itemsList);
            notifyItemInserted(this.mItemsList.size() - 1);
        } else {
            this.mItemsList.set(mItemsList.indexOf(itemsList), itemsList);
            notifyItemChanged(this.mItemsList.indexOf(itemsList));
        }
    }

    void removeItemById(int id) {
        for (ActionBeacon action : mItemsList) {
            if (action.getId() == id) {
                mItemsList.remove(action);
                notifyDataSetChanged();
                break;
            }
        }
    }

    ActionBeacon getItemById(int id) {
        for (ActionBeacon action : mItemsList) {
            if (action.getId() == id) {
                return action;
            }
        }
        return null;
    }

    static class BindingHolder extends RecyclerView.ViewHolder {
        private ItemActionBeaconBinding binding;

        BindingHolder(ItemActionBeaconBinding binding) {
            super(binding.contentView);
            this.binding = binding;
        }
    }

}