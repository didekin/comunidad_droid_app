package com.didekindroid.incidencia;

import android.app.NotificationManager;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.retrofit.RetrofitHandler;
import com.didekindroid.testutil.MockActivity;
import com.didekinservice.common.gcm.GcmMulticastRequest;
import com.didekinservice.common.gcm.GcmRequest;
import com.didekinservice.incidservice.gcm.GcmIncidRequestData;
import com.didekinservice.retrofit.GcmEndPointImp;
import com.google.firebase.iid.FirebaseInstanceId;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.incidencia.testutils.GcmConstantForTests.PACKAGE_TEST;
import static com.didekinservice.common.GcmServConstant.FCM_HOST_PORT;

/**
 * User: pedro@didekin
 * Date: 31/05/16
 * Time: 12:21
 */
@RunWith(AndroidJUnit4.class)
public abstract class GcmIncidNotificationTest {

    protected abstract IntentsTestRule<MockActivity> doIntentsTestRule();

    @Rule
    public IntentsTestRule<MockActivity> intentRule = doIntentsTestRule();

    RetrofitHandler retrofitHandler;
    FirebaseInstanceId firebaseInstanceId;
    GcmEndPointImp endPointImp;
    String gcmToken;
    NotificationManager mManager;
    Context context;
    MockActivity mActivity;
    long comunidadIdIntent;

    @Before
    public void setUp() throws Exception
    {
        firebaseInstanceId = FirebaseInstanceId.getInstance();
        retrofitHandler = new RetrofitHandler(FCM_HOST_PORT, 60); // in seconds.
        mActivity = intentRule.getActivity();
        TKhandler.updateIsGcmTokenSentServer(false);

        endPointImp = new GcmEndPointImp(retrofitHandler);
        context = InstrumentationRegistry.getTargetContext();
        mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Double check.
//        mManager.cancelAll();

        gcmToken = firebaseInstanceId.getToken();
    }

    @After
    public void tearDown() throws Exception
    {
        TKhandler.updateIsGcmTokenSentServer(false);
        mManager.cancelAll();
        cleanOptions(CLEAN_PEPE);
    }


//    ===============================  HELPER METHODS ===================================

    GcmMulticastRequest getGcmMultiRequest(String typeMsg)
    {
        List<String> gcmTokens = new ArrayList<>(2);
        gcmTokens.add(firebaseInstanceId.getToken());
        gcmTokens.add(firebaseInstanceId.getToken());
        return new GcmMulticastRequest.Builder(gcmTokens,
                new GcmRequest.Builder(new GcmIncidRequestData(typeMsg, comunidadIdIntent), PACKAGE_TEST).build())
                .build();
    }
}
