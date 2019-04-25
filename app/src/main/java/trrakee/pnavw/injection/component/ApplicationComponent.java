package trrakee.pnavw.injection.component;


import android.app.Application;

import org.altbeacon.beacon.BeaconManager;

import javax.inject.Singleton;

import dagger.Component;
import trrakee.pnavw.action.ActionExecutor;
import trrakee.pnavw.data.DataManager;
import trrakee.pnavw.injection.module.ApplicationModule;


/**
 * Created by Tushar Sharma on 03/26/2019.
 */
@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    Application application();

    DataManager dataManager();

    BeaconManager beaconManager();

    ActionExecutor actionExecutor();

}