package trrakee.pnavw.action;

import android.content.Context;

/**
 * Created by Tushar Sharma on 03/26/2019.
 */
abstract class Action implements IAction {
    @Override
    abstract public String execute(Context context);
}