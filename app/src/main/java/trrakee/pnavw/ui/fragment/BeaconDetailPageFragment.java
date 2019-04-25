package trrakee.pnavw.ui.fragment;

import android.os.Bundle;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.SwitchPreferenceCompat;

import org.altbeacon.beacon.Region;

import trrakee.pnavw.R;
import trrakee.pnavw.model.ActionRegion;


/**
 * Created by Tushar Sharma on 03/26/2019.
 */
public class BeaconDetailPageFragment extends PageBeaconFragment {


    public static BeaconDetailPageFragment newInstance(int page) {
        return new BeaconDetailPageFragment();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        this.addPreferencesFromResource(R.xml.preferences_beacon_detail);
    }

    @Override
    protected void setData() {

        SwitchPreferenceCompat switch_manage = (SwitchPreferenceCompat) findPreference("bd_switch_active");
        switch_manage.setChecked(mActionBeacon.isEnabled());

        switch_manage.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue instanceof Boolean) {
                    mActionBeacon.setIsEnabled((Boolean) newValue);
                }
                return true;
            }
        });

        setNameText(mActionBeacon.getName());

        EditTextPreference edit_name = (EditTextPreference) findPreference("bd_name_info");
        edit_name.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue instanceof String) {
                    setNameText((String) newValue);
                }
                return true;
            }
        });

        Region aRegion = ActionRegion.parseRegion(mActionBeacon);

        findPreference("bd_uuid_info").setSummary(aRegion.getId1().toString());
        findPreference("bd_major_info").setSummary(aRegion.getId2().toString());
        findPreference("bd_minor_info").setSummary(aRegion.getId3().toString());
        findPreference("bd_bluetooth_address_info").setSummary(aRegion.getBluetoothAddress());

    }

    private void setNameText(String newValue) {
        EditTextPreference name = (EditTextPreference) findPreference("bd_name_info");
        if (newValue != null && !newValue.isEmpty()) {
            name.setSummary(newValue);
            mActionBeacon.setName(newValue);
        } else {
            name.setSummary(getString(R.string.pref_bd_default_name));
            mActionBeacon.setName(getString(R.string.pref_bd_default_name));
        }
    }

}
