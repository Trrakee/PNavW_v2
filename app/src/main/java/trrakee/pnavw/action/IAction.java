package trrakee.pnavw.action;

import android.content.Context;

/**
 * Created by Tushar Sharma on 03/26/2019.
 */
public interface IAction {
    boolean isParamRequired();
    String execute(Context context);
}
