package trrakee.pnavw.injection.module;

import android.app.Application;
import android.content.Context;

import org.altbeacon.beacon.BeaconManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import trrakee.pnavw.action.ActionExecutor;
import trrakee.pnavw.data.DataManager;


@Module
public class ApplicationModule {
    protected final Application application;

    public ApplicationModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return this.application;
    }


    @Provides
    @Singleton
    Context provideApplicationContext() {
        return this.application;
    }

    @Provides
    @Singleton
    DataManager provideDataManager() {
        return new DataManager(this.application);
    }

    @Provides
    @Singleton
    ActionExecutor provideActionExecutor() {
        return new ActionExecutor(this.application);
    }

    @Provides
    @Singleton
    BeaconManager provideBeaconManager() {
        //manager.setDebug(true);
        return BeaconManager.getInstanceForApplication(application);
    }
}