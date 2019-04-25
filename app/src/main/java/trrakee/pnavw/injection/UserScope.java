package trrakee.pnavw.injection;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by Tushar Sharma on 03/26/2019.
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface UserScope {
}