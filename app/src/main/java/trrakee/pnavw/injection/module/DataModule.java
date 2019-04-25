package trrakee.pnavw.injection.module;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import trrakee.pnavw.data.DbStoreService;
import trrakee.pnavw.data.StoreService;
import trrakee.pnavw.injection.UserScope;

/**
 * Created by Tushar Sharma on 03/26/2019.
 */
@Module
public class DataModule {
    private Context mContext;

    public DataModule(Context context) {
        mContext = context;
    }

    @Provides
    @UserScope
    StoreService provideStoreService() {
        return new DbStoreService(mContext);
    }
}
