package trrakee.pnavw.ui.fragment;


import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.SwitchPreferenceCompat;

import trrakee.pnavw.R;
import trrakee.pnavw.util.Constants;

/**
 * Created by Tushar Sharma on 03/26/2019.
 */
public class BeaconNotificationPageFragment extends PageBeaconFragment {

    private String mNoneRintoneValue = "";

    public static BeaconNotificationPageFragment newInstance(int page) {
        return new BeaconNotificationPageFragment();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        this.addPreferencesFromResource(R.xml.preferences_beacon_notification);
        //default text
        mNoneRintoneValue = getResources().getString(R.string.pref_bn_none_notification_action_ringtone);
    }

    @Override
    protected void setData() {

        SwitchPreferenceCompat switch_enable = (SwitchPreferenceCompat) findPreference("bn_notification_action");
        switch_enable.setChecked(mActionBeacon.getNotification().isEnabled());

        switch_enable.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue instanceof Boolean) {
                    mActionBeacon.getNotification().setIsEnabled((Boolean) newValue);
                }
                return true;
            }
        });

        setRingtoneValue(mActionBeacon.getNotification().getRingtone());

        setMessageText(mActionBeacon.getNotification().getMessage());

        EditTextPreference msg_name = (EditTextPreference) findPreference("bn_notification_action_message");
        msg_name.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue instanceof String) {
                    setMessageText((String) newValue);
                }
                return true;
            }
        });

        SwitchPreferenceCompat vibrate_enable = (SwitchPreferenceCompat) findPreference("bn_notification_action_vibrate");
        vibrate_enable.setChecked(mActionBeacon.getNotification().isVibrate());

        vibrate_enable.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue instanceof Boolean) {
                    mActionBeacon.getNotification().setIsVibrate((Boolean) newValue);
                }
                return true;
            }
        });
    }

    private void setMessageText(String newValue) {
        EditTextPreference msg_name = (EditTextPreference) findPreference("bn_notification_action_message");
        if (newValue != null && !newValue.isEmpty()) {
            msg_name.setSummary(newValue);
            mActionBeacon.getNotification().setMessage(newValue);
        } else {
            msg_name.setSummary(getString(R.string.pref_bn_default_notification_action_message));
            mActionBeacon.getNotification().setMessage(getString(R.string.pref_bn_default_notification_action_message));
        }
    }

    private String getRingtoneValue() {
        return findPreference("bn_notification_action_ringtone").getSummary().toString();
    }

    private void setRingtoneValue(String value) {
        if (value != null && !value.isEmpty()) {
            findPreference("bn_notification_action_ringtone").setSummary(value);
        } else {
            //none
            findPreference("bn_notification_action_ringtone").setSummary(mNoneRintoneValue);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference.getKey().equals("bn_notification_action_ringtone")) {
            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, Settings.System.DEFAULT_NOTIFICATION_URI);

            String existingValue = getRingtoneValue();
            if (existingValue.length() == 0 || existingValue.equalsIgnoreCase(mNoneRintoneValue)) {
                // Select "Silent"
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
            } else {
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(existingValue));
            }

            startActivityForResult(intent, Constants.REQ_CODE_ALERT_RINGTONE);
            return true;
        } else {
            return super.onPreferenceTreeClick(preference);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQ_CODE_ALERT_RINGTONE && data != null) {
            Uri ringtone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (ringtone != null) {
                setRingtoneValue(ringtone.toString());
                mActionBeacon.getNotification().setRingtone(ringtone.toString());

            } else {
                // "Silent" was selected
                setRingtoneValue(null);
                mActionBeacon.getNotification().setRingtone(mNoneRintoneValue);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
