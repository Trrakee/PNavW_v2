package trrakee.pnavw.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import trrakee.pnavw.BuildConfig;
import trrakee.pnavw.R;
import trrakee.pnavw.model.TrackedBeacon;
import trrakee.pnavw.ui.activity.MainNavigationActivity;


/**
 * Created by Tushar Sharma on 03/26/2019.
 */
public class BeaconFragment extends Fragment {

    protected Unbinder unbinder;
    private OnTrackedBeaconSelectedListener mBeaconSelectedListener;

    protected boolean isDebug() {
        return BuildConfig.DEBUG;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() instanceof MainNavigationActivity) {
            ((MainNavigationActivity) getActivity()).swappingFloatingIcon();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mBeaconSelectedListener = (OnTrackedBeaconSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnTrackedBeaconSelectedListener");
        }
    }

    public void selectBeacon(TrackedBeacon trackedBeacon) {
        if (mBeaconSelectedListener != null) {
            mBeaconSelectedListener.onBeaconSelected(trackedBeacon);
        }
    }


    public interface OnTrackedBeaconSelectedListener {
        void onBeaconSelected(TrackedBeacon beacon);
    }

    class EmptyView {

        @BindView(R.id.empty_text)
        TextView text;

        EmptyView(View view) {
            ButterKnife.bind(this, view);
        }
    }

}