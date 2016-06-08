package com.didekindroid.incidencia.gcm;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.common.controller.RetrofitHandler;
import com.didekin.incidservice.dominio.IncidenciaUser;
import com.didekin.usuario.dominio.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.common.activity.MockActivity;
import com.didekindroid.common.activity.UiException;
import com.didekinservice.common.GcmEndPointImp;
import com.didekinservice.common.GcmException;
import com.didekinservice.common.GcmMulticastRequest;
import com.didekinservice.common.GcmRequest;
import com.didekinservice.common.GcmResponse;
import com.didekinservice.common.GcmSingleRequest;
import com.didekinservice.incidservice.gcm.GcmIncidRequestData;
import com.google.firebase.iid.FirebaseInstanceId;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekin.incidservice.gcm.GcmKeyValueIncidData.incidencia_type;
import static com.didekindroid.DidekindroidApp.getHttpTimeOut;
import static com.didekindroid.common.activity.BundleKey.COMUNIDAD_ID;
import static com.didekindroid.common.gcm.AppFirebaseMsgService.TypeMsgHandler.INCIDENCIA;
import static com.didekindroid.common.testutils.ActivityTestUtils.checkNavigateUp;
import static com.didekindroid.common.testutils.ActivityTestUtils.cleanOptions;
import static com.didekindroid.common.testutils.ActivityTestUtils.signUpAndUpdateTk;
import static com.didekindroid.common.utils.UIutils.updateIsGcmTokenSentServer;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.insertGetIncidenciaUser;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_REAL_PEPE;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
import static com.didekinservice.common.GcmEndPoint.FCM_HOST_PORT;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 31/05/16
 * Time: 12:21
 */
@RunWith(AndroidJUnit4.class)
public class GcmIncidNotificationTest_1 {

    static RetrofitHandler retrofitHandler;
    GcmEndPointImp endPointImp;
    static FirebaseInstanceId firebaseInstanceId;
    String gcmToken;
    NotificationManager mManager;
    Context context;

    static final String secondToken = "d8qFf3QHu3A:APA91bEaQsPiV1bKGbGnZ5Mw9LdEtubtMMQ3Mget8mQ-iQ78lUKg3_Ego0sosuuWrOx0pjm104aUy4FoaY3tQeTdzfbMChi_ivIrQyUk7zQGS0Gwudb4jUv36ZbdTod3Ff_5G_a7LqG3";

    private MockActivity mActivity;
    long comunidadIdIntent;
    IncidenciaUser incidenciaUser;

    @Rule
    public IntentsTestRule<MockActivity> intentRule = new IntentsTestRule<MockActivity>(MockActivity.class) {

        @Override
        protected Intent getActivityIntent()
        {
            try {
                signUpAndUpdateTk(COMU_REAL_PEPE);
                UsuarioComunidad pepeUserComu = ServOne.seeUserComusByUser().get(0);
                comunidadIdIntent = pepeUserComu.getComunidad().getC_Id();
                // Insertamos incidencia.
                incidenciaUser = insertGetIncidenciaUser(pepeUserComu, 1);
            } catch (UiException | IOException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent();
            intent.putExtra(COMUNIDAD_ID.key, comunidadIdIntent);
            return intent;
        }

        @Override
        protected void beforeActivityLaunched()
        {
            super.beforeActivityLaunched();
        }
    };

    @BeforeClass
    public static void setFirebaseInstance()
    {
        firebaseInstanceId = FirebaseInstanceId.getInstance();
        retrofitHandler = new RetrofitHandler(FCM_HOST_PORT, getHttpTimeOut());
    }

    @Before
    public void setUp() throws Exception
    {
        mActivity = intentRule.getActivity();
        updateIsGcmTokenSentServer(false, mActivity);

        endPointImp = new GcmEndPointImp(retrofitHandler);
        context = InstrumentationRegistry.getTargetContext();
        mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        gcmToken = firebaseInstanceId.getToken();

    }

    @After
    public void tearDown() throws Exception
    {
        updateIsGcmTokenSentServer(false, mActivity);
        mManager.cancelAll();
        cleanOptions(CLEAN_PEPE);
    }

    //    =========================== TESTS =============================

    /**
     * Sinqle tokenId request.
     */
    @TargetApi(Build.VERSION_CODES.M)
    @Test
    public void testCheckNotification_1() throws IOException, InterruptedException
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            GcmSingleRequest request = new GcmSingleRequest.Builder(gcmToken,
                    new GcmRequest.Builder(new GcmIncidRequestData(incidencia_type, comunidadIdIntent)).build())
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            GcmResponse gcmResponse = endPointImp.sendGcmMulticastRequestImp(getGcmMultiRequest());
            assertThat(gcmResponse.getSuccess(), is(2));
            Thread.sleep(2000);
            assertThat(mManager.getActiveNotifications().length, is(1));
            StatusBarNotification barNotification = mManager.getActiveNotifications()[0];
            assertThat(barNotification.getId(), is(INCIDENCIA.getTitleRsc()));

            // We check the pending intent.
            PendingIntent pendingIntent = barNotification.getNotification().contentIntent;
            assertThat(pendingIntent.getCreatorPackage(), is(GcmRequest.PACKAGE_DIDEKINDROID));
        }
    }

    /**
     * Multicast request with two tokenIds.
     * We check the backStack with UP.
     */
    @TargetApi(Build.VERSION_CODES.M)
    @Test
    public void testCheckNotification_3() throws IOException, InterruptedException, GcmException
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            endPointImp.sendGcmMulticastRequestImp(getGcmMultiRequest());
            Thread.sleep(2000);
            Notification notification = mManager.getActiveNotifications()[0].getNotification();
            final PendingIntent pendingIntent = notification.contentIntent;

            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run()
                {
                    try {
                        pendingIntent.send();
                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    }
                }
            });

            onView(withId(R.id.incid_see_open_by_comu_ac)).check(matches(isDisplayed()));
            // Verificamos que se muestran los datos de las incidencias abiertas.
            onView(allOf(
                    withId(R.id.incid_see_importancia_block),
                    hasSibling(allOf(
                            withText(incidenciaUser.getIncidencia().getDescripcion()),
                            withId(R.id.incid_descripcion_view)
                    )),
                    hasSibling(allOf(
                            withId(R.id.incid_see_apertura_block),

                            hasDescendant(allOf(
                                    withId(R.id.incid_see_iniciador_view),
                                    withText(incidenciaUser.getUsuario().getAlias())
                            ))
                    ))
            )).check(matches(isDisplayed()));
            checkNavigateUp();
            // Verificamos que se muestran los datos de búsqueda de comunidad.
            onView(withId(R.id.comu_search_ac_layout)).check(matches(isDisplayed()));
        }
    }

    /**
     * Multicast request with two tokenIds.
     * We check the backStack with BACK.
     */
    @TargetApi(Build.VERSION_CODES.M)
    @Test
    public void testCheckNotification_4() throws IOException, InterruptedException, GcmException
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            endPointImp.sendGcmMulticastRequestImp(getGcmMultiRequest());
            Thread.sleep(2000);
            final PendingIntent pendingIntent = mManager.getActiveNotifications()[0].getNotification().contentIntent;

            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run()
                {
                    try {
                        pendingIntent.send();
                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    }
                }
            });

            onView(withId(R.id.incid_see_open_by_comu_ac)).check(matches(isDisplayed())).perform(pressBack());
            // Verificamos que se muestran los datos de búsqueda de comunidad.
            onView(withId(R.id.comu_search_ac_layout)).check(matches(isDisplayed()));
        }
    }

//    ===============================  HELPER METHODS ===================================

    private GcmMulticastRequest getGcmMultiRequest()
    {
        List<String> gcmTokens = new ArrayList<>(2);
        gcmTokens.add(firebaseInstanceId.getToken());
        gcmTokens.add(secondToken);
        return new GcmMulticastRequest.Builder(gcmTokens,
                new GcmRequest.Builder(new GcmIncidRequestData(incidencia_type, comunidadIdIntent)).build())
                .build();
    }
}
