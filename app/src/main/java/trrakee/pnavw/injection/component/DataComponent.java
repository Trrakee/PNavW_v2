package trrakee.pnavw.injection.component;

import dagger.Component;
import trrakee.pnavw.data.DataManager;
import trrakee.pnavw.injection.UserScope;
import trrakee.pnavw.injection.module.DataModule;

@UserScope
@Component(dependencies = ApplicationComponent.class, modules = {DataModule.class})
public interface DataComponent {
    void inject(DataManager dataManager);
}