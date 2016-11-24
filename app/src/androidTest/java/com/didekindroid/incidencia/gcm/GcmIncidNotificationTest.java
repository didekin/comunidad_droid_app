package com.didekindroid.incidencia.gcm;

import android.app.NotificationManager;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.common.controller.RetrofitHandler;
import com.didekinaar.mock.MockActivity;
import com.didekinservice.common.gcm.GcmEndPointImp;
import com.didekinservice.common.gcm.GcmMulticastRequest;
import com.didekinservice.common.gcm.GcmRequest;
import com.didekinservice.incidservice.gcm.GcmIncidRequestData;
import com.google.firebase.iid.FirebaseInstanceId;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static com.didekinaar.utils.UIutils.updateIsGcmTokenSentServer;
import static com.didekinaar.testutil.AarActivityTestUtils.cleanOptions;
import static com.didekinaar.testutil.CleanUserEnum.CLEAN_PEPE;
import static com.didekinservice.common.gcm.GcmEndPoint.FCM_HOST_PORT;

/**
 * User: pedro@didekin
 * Date: 31/05/16
 * Time: 12:21
 */
@RunWith(AndroidJUnit4.class)
public abstract class GcmIncidNotificationTest {

    RetrofitHandler retrofitHandler;
    FirebaseInstanceId firebaseInstanceId;

    GcmEndPointImp endPointImp;
    String gcmToken;
    NotificationManager mManager;
    Context context;
    MockActivity mActivity;
    long comunidadIdIntent;

    final String secondToken = "d8qFf3QHu3A:APA91bEaQsPiV1bKGbGnZ5Mw9LdEtubtMMQ3Mget8mQ-iQ78lUKg3_Ego0sosuuWrOx0pjm104aUy4FoaY3tQeTdzfbMChi_ivIrQyUk7zQGS0Gwudb4jUv36ZbdTod3Ff_5G_a7LqG3";

    @Rule
    public IntentsTestRule<MockActivity> intentRule = doIntentsTestRule();

    @Before
    public void setUp() throws Exception
    {
        Thread.sleep(2000);

        firebaseInstanceId = FirebaseInstanceId.getInstance();
        retrofitHandler = new RetrofitHandler(FCM_HOST_PORT, 60); // in seconds.
        mActivity = intentRule.getActivity();
        updateIsGcmTokenSentServer(false, mActivity);

        endPointImp = new GcmEndPointImp(retrofitHandler);
        context = InstrumentationRegistry.getTargetContext();
        mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Double check.
        mManager.cancelAll();
        gcmToken = firebaseInstanceId.getToken();
    }

    @After
    public void tearDown() throws Exception
    {
        updateIsGcmTokenSentServer(false, mActivity);
        mManager.cancelAll();
        cleanOptions(CLEAN_PEPE);
    }

/*    ================================== TESTS ===============================*/

//    ===============================  HELPER METHODS ===================================

    GcmMulticastRequest getGcmMultiRequest(String typeMsg)
    {
        List<String> gcmTokens = new ArrayList<>(2);
        gcmTokens.add(firebaseInstanceId.getToken());
        gcmTokens.add(secondToken);
        return new GcmMulticastRequest.Builder(gcmTokens,
                new GcmRequest.Builder(new GcmIncidRequestData(typeMsg, comunidadIdIntent)).build())
                .build();
    }

    protected  abstract IntentsTestRule<MockActivity> doIntentsTestRule();
}
