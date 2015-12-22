package com.didekindroid.incidencia.gcm;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat.Builder;

import com.didekindroid.R;
import com.didekindroid.incidencia.activity.IncidSeeByUserAc;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeoutException;

import static com.didekindroid.incidencia.gcm.GcmNotificationListenerServ.GCM_NOTIFICATION_action;
import static com.didekindroid.incidencia.gcm.GcmNotificationListenerServ.notification_extra;
import static com.didekindroid.incidencia.gcm.GcmNotificationListenerServ.notification_id_extra;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 01/12/15
 * Time: 13:50
 */
@RunWith(AndroidJUnit4.class)
public class GcmBroadCastNotificationAcTest {

    Context context = InstrumentationRegistry.getTargetContext();
    GcmBroadCastNotificationAc mActivity;

    @Rule
    public ActivityTestRule<GcmBroadCastNotificationAc> mActivityRule = new ActivityTestRule<>(GcmBroadCastNotificationAc.class, true, false);

    @Test
    public void testNotification_1() throws TimeoutException, InterruptedException
    {
        mActivity = mActivityRule.launchActivity(new Intent());
        sendNotification();
        Thread.sleep(3000);
        assertThat(mActivity.notificationId, is(1));
        assertThat(mActivity.title,is(context.getString(R.string.incid_gcm_nueva_incidencia_title)));
        assertThat(mActivity.text,is(context.getString(R.string.incid_gcm_nueva_incidencia_body)));
        assertThat(mActivity.subText, is(context.getString(R.string.incid_gcm_nueva_incidencia_subtext)));
    }


    //    ======================== HELPER METHODS ==========================

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void sendNotification()
    {
        Builder mBuilder = (Builder) new Builder(context)
                .setSmallIcon(R.drawable.ic_info_outline_white_36dp)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
                .setCategory(Notification.CATEGORY_SOCIAL)
                .setAutoCancel(true)
                .setContentTitle(context.getString(R.string.incid_gcm_nueva_incidencia_title))
                .setContentText(context.getString(R.string.incid_gcm_nueva_incidencia_body))
                .setSubText(context.getString(R.string.incid_gcm_nueva_incidencia_subtext));

        Intent resultIntent = new Intent(context, IncidSeeByUserAc.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(IncidSeeByUserAc.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);
        Notification notification = mBuilder.build();

        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(/* notification ID*/1, notification);

        sendBroadcastMsg(notification, 1);
    }

    private void sendBroadcastMsg(Notification notification, int notificationId)
    {
        Intent intent = new Intent(GCM_NOTIFICATION_action);
        intent.putExtra(notification_id_extra, notificationId);
        intent.putExtra(notification_extra, notification);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
