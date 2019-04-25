package trrakee.pnavw.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import trrakee.pnavw.R;
import trrakee.pnavw.model.ActionBeacon;
import trrakee.pnavw.ui.fragment.BeaconActionPageFragment;
import trrakee.pnavw.ui.fragment.BeaconDetailPageFragment;
import trrakee.pnavw.ui.fragment.BeaconEventPageFragment;
import trrakee.pnavw.ui.fragment.BeaconNotificationPageFragment;
import trrakee.pnavw.ui.fragment.PageBeaconFragment;
import trrakee.pnavw.util.Constants;

/**
 * Created by Tushar Sharma on 25/12/15.
 */
public class DetailFragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {
    private int tabTitleResources[] = new int[]{R.string.tab_title_beacon_info, R.string.tab_title_beacon_event, R.string.tab_title_beacon_action,
            R.string.tab_title_beacon_notification};
    private Context mContext;
    private ActionBeacon mActionBeacon;

    public DetailFragmentPagerAdapter(FragmentManager fm, ActionBeacon beacon, Context context) {
        super(fm);
        this.mContext = context;
        this.mActionBeacon = beacon;
    }

    @Override
    public int getCount() {
        return tabTitleResources.length;
    }

    @Override
    public PageBeaconFragment getItem(int position) {
        Bundle args = new Bundle();
        args.putInt(Constants.ARG_PAGE, position + 1);

        PageBeaconFragment frg = BeaconDetailPageFragment.newInstance(position + 1);
        switch (position) {
            case 0:
                frg = BeaconDetailPageFragment.newInstance(position + 1);
                break;
            case 1:
                frg = BeaconEventPageFragment.newInstance(position + 1);
                break;
            case 2:
                frg = BeaconActionPageFragment.newInstance(position + 1);
                break;
            case 3:
                frg = BeaconNotificationPageFragment.newInstance(position + 1);
                break;
        }

        if (mActionBeacon != null) {
            args.putParcelable(Constants.ARG_ACTION_BEACON, mActionBeacon);
            frg.setArguments(args);
        }
        return frg;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return this.mContext.getString(tabTitleResources[position]);
    }
}
