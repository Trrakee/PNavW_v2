package trrakee.pnavw.action;

import android.content.Context;
import android.media.AudioManager;

import trrakee.pnavw.model.NotificationAction;
import trrakee.pnavw.util.PreferencesUtil;

/**
 * Created by Tushar Sharma on 03/26/2019.
 */
public class SilentOffAction extends NoneAction {


    SilentOffAction(String param, NotificationAction notification) {
        super(param, notification);
    }

    @Override
    public String execute(Context context) {
        final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int ringerMode = audioManager.getRingerMode();
        int old_mode = PreferencesUtil.getSilentModeProfile(context);
        if (ringerMode != AudioManager.RINGER_MODE_VIBRATE) {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        }
        return super.execute(context);
    }

    @Override
    public String toString() {
        return "SilentOffAction, param: " + param;
    }
}
