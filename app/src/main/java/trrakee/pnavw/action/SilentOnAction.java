package trrakee.pnavw.action;

import android.content.Context;
import android.media.AudioManager;

import trrakee.pnavw.model.NotificationAction;
import trrakee.pnavw.util.PreferencesUtil;

/**
 * Created by Tushar Sharma on 03/26/2019.
 */
public class SilentOnAction extends NoneAction {

    private int mRingerMode;

    SilentOnAction(String param, NotificationAction notification) {
        super(param, notification);
    }

    @Override
    public String execute(Context context) {
        final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            mRingerMode = AudioManager.RINGER_MODE_NORMAL;
        } else if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {
            audioManager.setMode(AudioManager.RINGER_MODE_SILENT);
            mRingerMode = AudioManager.RINGER_MODE_VIBRATE;
        }
        PreferencesUtil.setSilentModeProfile(context, mRingerMode);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        return super.execute(context);
    }

    @Override
    public String toString() {
        return "SilentOnAction, param: " + param;
    }
}
