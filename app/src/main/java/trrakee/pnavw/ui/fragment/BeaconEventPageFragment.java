package trrakee.pnavw.ui.fragment;

import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.Preference;

import trrakee.pnavw.R;
import trrakee.pnavw.model.ActionBeacon;

/**
 * Created by Tushar Sharma on 03/26/2019.
 */
public class BeaconEventPageFragment extends PageBeaconFragment {


    public static BeaconEventPageFragment newInstance(int page) {
        return new BeaconEventPageFragment();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        this.addPreferencesFromResource(R.xml.preferences_beacon_event);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {

        String key = preference.getKey();
        switch (key) {
            case "be_event_enter_region":
                mActionBeacon.setEventType(ActionBeacon.EventType.EVENT_ENTERS_REGION);
                break;
            case "be_event_leaves_region":
                mActionBeacon.setEventType(ActionBeacon.EventType.EVENT_LEAVES_REGION);
                break;
            default:
                mActionBeacon.setEventType(ActionBeacon.EventType.EVENT_NEAR_YOU);
                break;
        }

        setData();

        return super.onPreferenceTreeClick(preference);
    }

    @Override
    protected void setData() {
        switch (mActionBeacon.getEventType()) {
            case EVENT_ENTERS_REGION:
                ((CheckBoxPreference) findPreference("be_event_enter_region")).setChecked(true);
                ((CheckBoxPreference) findPreference("be_event_leaves_region")).setChecked(false);
                ((CheckBoxPreference) findPreference("be_event_near_you")).setChecked(false);
                break;
            case EVENT_LEAVES_REGION:
                ((CheckBoxPreference) findPreference("be_event_leaves_region")).setChecked(true);
                ((CheckBoxPreference) findPreference("be_event_enter_region")).setChecked(false);
                ((CheckBoxPreference) findPreference("be_event_near_you")).setChecked(false);
                break;
            default:
                ((CheckBoxPreference) findPreference("be_event_near_you")).setChecked(true);
                ((CheckBoxPreference) findPreference("be_event_leaves_region")).setChecked(false);
                ((CheckBoxPreference) findPreference("be_event_enter_region")).setChecked(false);
        }
    }

}
