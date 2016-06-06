package com.didekindroid.common.gcm;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.common.controller.RetrofitHandler;
import com.didekinservice.common.GcmEndPointImp;
import com.didekinservice.common.GcmException;
import com.didekinservice.common.GcmMulticastRequest;
import com.didekinservice.common.GcmRequest;
import com.didekinservice.common.GcmResponse;
import com.didekinservice.common.GcmSingleRequest;
import com.didekinservice.incidservice.gcm.GcmIncidRequestData;
import com.google.firebase.iid.FirebaseInstanceId;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.didekin.incidservice.gcm.GcmKeyValueIncidData.incidencia_type;
import static com.didekindroid.DidekindroidApp.getHttpTimeOut;
import static com.didekindroid.common.gcm.AppFirebaseMsgService.TypeMsgHandler.INCIDENCIA;
import static com.didekinservice.common.GcmEndPoint.FCM_HOST_PORT;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 31/05/16
 * Time: 12:21
 */
@RunWith(AndroidJUnit4.class)
public class GcmNotificationTest {

    static RetrofitHandler retrofitHandler;
    GcmEndPointImp endPointImp;
    static FirebaseInstanceId firebaseInstanceId;
    String gcmToken;
    NotificationManager mManager;
    Context context;

    static final String secondToken = "d8qFf3QHu3A:APA91bEaQsPiV1bKGbGnZ5Mw9LdEtubtMMQ3Mget8mQ-iQ78lUKg3_Ego0sosuuWrOx0pjm104aUy4FoaY3tQeTdzfbMChi_ivIrQyUk7zQGS0Gwudb4jUv36ZbdTod3Ff_5G_a7LqG3";

    @BeforeClass
    public static void setFirebaseInstance()
    {
        firebaseInstanceId = FirebaseInstanceId.getInstance();
        retrofitHandler = new RetrofitHandler(FCM_HOST_PORT, getHttpTimeOut());
    }

    @Before
    public void setUp() throws Exception
    {
        endPointImp = new GcmEndPointImp(retrofitHandler);
        context = InstrumentationRegistry.getTargetContext();
        mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        gcmToken = firebaseInstanceId.getToken();
    }

    //    =========================== TESTS =============================

    /**
     * Sinqle tokenId request.
     */
    @TargetApi(Build.VERSION_CODES.M)
    @Test
    public void testCheckNotification_1() throws IOException, InterruptedException
    {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            GcmSingleRequest request = new GcmSingleRequest.Builder(gcmToken,
                    new GcmRequest.Builder(new GcmIncidRequestData(incidencia_type, 999L)).build())
                    .build();
            GcmResponse gcmResponse = endPointImp.sendGcmSingleRequest(request).execute().body();
            assertThat(gcmResponse.getSuccess(), is(1));
            Thread.sleep(2000);
            assertThat(mManager.getActiveNotifications().length, is(1));
            StatusBarNotification barNotification = mManager.getActiveNotifications()[0];
            assertThat(barNotification.getId(), is(INCIDENCIA.getTitleRsc()));
            assertThat(barNotification.getPackageName(), is(request.restricted_package_name));
            assertThat(barNotification.getNotification().extras.getString(Notification.EXTRA_TEXT), is(context.getString(INCIDENCIA.getContentTextRsc())));
        }
    }

    /**
     * Multicast request with two tokenIds.
     */
    @TargetApi(Build.VERSION_CODES.M)
    @Test
    public void testCheckNotification_2() throws IOException, InterruptedException, GcmException
    {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            List<String> gcmTokens = new ArrayList<>(2);
            gcmTokens.add(firebaseInstanceId.getToken());
            gcmTokens.add(secondToken);
            GcmMulticastRequest request = new GcmMulticastRequest.Builder(gcmTokens,
                    new GcmRequest.Builder(new GcmIncidRequestData(incidencia_type, 999L)).build())
                    .build();
            GcmResponse gcmResponse = endPointImp.sendGcmMulticastRequestImp(request);
            assertThat(gcmResponse.getSuccess(), is(2));
            Thread.sleep(2000);
            assertThat(mManager.getActiveNotifications().length, is(1));
            StatusBarNotification barNotification = mManager.getActiveNotifications()[0];
            assertThat(barNotification.getId(), is(INCIDENCIA.getTitleRsc()));
        }
    }
}
