package trrakee.pnavw.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.widget.Toast;

import java.util.Objects;

import trrakee.pnavw.BeaconLocatorApp;
import trrakee.pnavw.R;
import trrakee.pnavw.action.ActionExecutor;
import trrakee.pnavw.action.IAction;
import trrakee.pnavw.model.ActionBeacon;
import trrakee.pnavw.util.Constants;

/**
 * Created by Tushar Sharma on 03/26/2019.
 */
public class BeaconActionPageFragment extends PageBeaconFragment {

    private ActionExecutor mActionExecutor;

    public static BeaconActionPageFragment newInstance(int page) {
        return new BeaconActionPageFragment();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        this.addPreferencesFromResource(R.xml.preferences_beacon_action);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mActionExecutor = BeaconLocatorApp.from(Objects.requireNonNull(getActivity())).getComponent().actionExecutor();
        }
    }

    @Override
    protected void setData() {

        setActionParam1(mActionBeacon.getActionParam());

        EditTextPreference param_name = (EditTextPreference) findPreference("ba_action_param1");
        param_name.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue instanceof String) {
                    setActionParam1((String) newValue);
                }
                return true;
            }
        });

        setActionType(Integer.toString(mActionBeacon.getActionType().getValue()));

        final ListPreference action_list = (ListPreference) findPreference("ba_action_list");
        action_list.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue instanceof String) {
                    setActionType((String) newValue);
                }
                return true;
            }
        });

        Preference testButton = getPreferenceManager().findPreference("ba_action_test");
        testButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {
                IAction action = ActionExecutor.actionBuilder(mActionBeacon.getActionType(),
                        mActionBeacon.getActionParam(), mActionBeacon.getNotification());
                String resMessage = mActionExecutor.storeAndExecute(action);
                if (resMessage != null) {
                    Toast.makeText(getActivity(), resMessage, Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });

    }

    private ActionBeacon.ActionType getActionType() {
        final ListPreference action_list = (ListPreference) findPreference("ba_action_list");
        return ActionBeacon.ActionType.fromInt(Integer.parseInt(action_list.getValue()));
    }

    private void setActionType(String newValue) {
        final ListPreference action_list = (ListPreference) findPreference("ba_action_list");
        if (newValue != null && !newValue.isEmpty()) {
            int index = action_list.findIndexOfValue(newValue);
            action_list.setSummary(index >= 0
                    ? action_list.getEntries()[index]
                    : null);
            mActionBeacon.setActionType(ActionBeacon.ActionType.fromInt(index >= 0 ? index : 0));
        } else {
            action_list.setSummary(action_list.getEntries()[0]);
            mActionBeacon.setActionType(ActionBeacon.ActionType.fromInt(0));
        }
    }

    private String getActionParam1() {
        EditTextPreference param_name = (EditTextPreference) findPreference("ba_action_param1");
        return param_name.getSummary().toString();
    }

    private void setActionParam1(String newValue) {
        EditTextPreference param_name = (EditTextPreference) findPreference("ba_action_param1");
        if (newValue != null && !newValue.isEmpty()) {
            param_name.setSummary(newValue);
            mActionBeacon.setActionParam(newValue);
        } else {
            param_name.setSummary("");
            mActionBeacon.setActionParam("");
        }
    }


    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQ_TASKER_ACTION_NAME_REQUEST) {
            if (data != null) {
                final String id = data.getStringExtra("id");
                final String taskName = data.getDataString();
            }
        }
    }
}
