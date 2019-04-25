package trrakee.pnavw.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.content.ContextCompat;

import trrakee.pnavw.R;

/**
 * Created by Tushar Sharma on 03/26/2019.
 */
public class NotificationBuilder {

    private Context mContext;
    private Builder mBuilder;
    private NotificationManager mNotificationManager;

    public NotificationBuilder(Context mContext) {
        this.mContext = mContext.getApplicationContext();
    }

    /**
     * Creation of notification on operations completed
     */
    public NotificationBuilder createNotification(int smallIcon, String title, boolean isVibrate, PendingIntent notifyIntent) {

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "blocator_channel_01";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    title, NotificationManager.IMPORTANCE_HIGH);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            if (isVibrate) {
                notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                notificationChannel.enableVibration(true);
            }
            notificationManager.createNotificationChannel(notificationChannel);
        }

        mBuilder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID);

        mBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(smallIcon)
                .setContentTitle(title)
                .setColor(ContextCompat.getColor(mContext, R.color.purdue));

        if (notifyIntent != null) {
            mBuilder.setContentIntent(notifyIntent);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // setLargeIcon(R.drawable.logo_notification_lollipop);
            //FIXME
            setLargeIcon();
        } else {
            setLargeIcon();
        }

        return this;
    }


    public Builder getBuilder() {
        return mBuilder;
    }


    private NotificationBuilder setLargeIcon(Bitmap largeIconBitmap) {
        mBuilder.setLargeIcon(largeIconBitmap);
        return this;
    }


    private NotificationBuilder setLargeIcon() {
        Bitmap largeIconBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher);
        return setLargeIcon(largeIconBitmap);
    }


    public NotificationBuilder setRingtone(String ringtone) {
        // Ringtone options
        if (ringtone != null) {
            mBuilder.setSound(Uri.parse(ringtone));
        }
        return this;
    }


    public NotificationBuilder setVibration() {
        return setVibration(null);
    }


    private NotificationBuilder setVibration(long[] pattern) {
        if (pattern == null || pattern.length == 0) {
            pattern = new long[]{500, 500};
        }
        mBuilder.setVibrate(pattern);
        return this;
    }


    public NotificationBuilder setLedActive() {
        mBuilder.setLights(Color.BLUE, 1000, 1000);
        return this;
    }


    public NotificationBuilder setIcon(int icon) {
        mBuilder.setSmallIcon(icon);
        return this;
    }


    public NotificationBuilder setMessage(String message) {
        mBuilder.setContentText(message);
        return this;
    }

    public NotificationBuilder setTicker(String ticker) {
        this.mBuilder.setTicker(ticker);
        return this;
    }

    public NotificationBuilder setIndeterminate() {
        mBuilder.setProgress(0, 0, true);
        return this;
    }


    public NotificationBuilder setOngoing() {
        mBuilder.setOngoing(true);
        return this;
    }


    public NotificationBuilder show() {
        show(0);
        return this;
    }


    public NotificationBuilder show(long id) {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        Notification mNotification = mBuilder.build();
        if (mNotification.contentIntent == null) {
            // Creates a dummy PendingIntent
            mBuilder.setContentIntent(PendingIntent.getActivity(mContext, 0, new Intent(),
                    PendingIntent.FLAG_UPDATE_CURRENT));
        }
        // Builds an anonymous Notification object from the builder, and passes it to the NotificationManager
        mNotificationManager.notify((int) id, mBuilder.build());
        return this;
    }


}
