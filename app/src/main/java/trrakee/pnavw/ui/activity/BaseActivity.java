package trrakee.pnavw.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import trrakee.pnavw.BeaconLocatorApp;
import trrakee.pnavw.R;
import trrakee.pnavw.util.Constants;
import trrakee.pnavw.util.PreferencesUtil;


/**
 * Created by Tushar Sharma on 03/26/2019.
 */
@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {
    public static int glCount = 0;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                FragmentManager fm = getFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                } else {
                    finish();
                }
                return true;
            case R.id.action_settings:
                launchSettingsActivity();
                return true;
            case R.id.action_view_on_github:
                launchGitHubPage();
                return true;
            case R.id.action_help:
                launchHelpPage();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void launchGitHubPage() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://github.com/Trrakee"));
        startActivity(browserIntent);
    }

    private void launchHelpPage() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://github.com/Trrakee"));
        startActivity(browserIntent);
    }

    protected void launchSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, Constants.REQ_GLOBAL_SETTING);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQ_GLOBAL_SETTING) {
            //TODO settings
        }
        super.onActivityResult(requestCode, resultCode, data);

    }


    protected Fragment checkFragmentInstance(int id, Object instanceClass) {

        Fragment fragment = getFragmentInstance(id);
        if (fragment != null && instanceClass.equals(fragment.getClass())) {
            return fragment;
        }
        return null;
    }

    protected Fragment getFragmentInstance(int id) {
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager != null) {
            Fragment fragment = fragmentManager.findFragmentById(id);
            if (fragment != null) {
                return fragment;
            }
        }
        return null;
    }


    @Override
    protected void onStart() {
        super.onStart();
        glCount++;
        if (glCount == 1) {
            BeaconLocatorApp.from(this).enableBackgroundScan(false);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        glCount--;
        if (glCount <= 0) {
            BeaconLocatorApp.from(this).enableBackgroundScan(PreferencesUtil.isBackgroundScan(this));
        }
    }
}