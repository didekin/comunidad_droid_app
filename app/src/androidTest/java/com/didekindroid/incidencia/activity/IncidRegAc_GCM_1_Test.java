package com.didekindroid.incidencia.activity;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.incidservice.dominio.IncidImportancia;
import com.didekin.usuario.dominio.Usuario;
import com.didekin.usuario.dominio.UsuarioComunidad;
import com.didekindroid.common.activity.IdlingResourceForIntentServ;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.common.gcm.GcmRegistrationIntentService;
import com.didekindroid.usuario.testutils.CleanUserEnum;
import com.google.firebase.iid.FirebaseInstanceId;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.core.deps.guava.base.Preconditions.checkState;
import static com.didekindroid.common.activity.BundleKey.COMUNIDAD_ID;
import static com.didekindroid.common.testutils.ActivityTestUtils.cleanOptions;
import static com.didekindroid.common.testutils.ActivityTestUtils.signUpAndUpdateTk;
import static com.didekindroid.common.utils.UIutils.checkPlayServices;
import static com.didekindroid.common.utils.UIutils.isGcmTokenSentServer;
import static com.didekindroid.common.utils.UIutils.isRegisteredUser;
import static com.didekindroid.common.utils.UIutils.updateIsGcmTokenSentServer;
import static com.didekindroid.common.gcm.AppFirebaseMsgService.TypeMsgHandler.INCIDENCIA;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.doIncidencia;
import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 27/11/15
 * Time: 16:38
 */
@RunWith(AndroidJUnit4.class)
public class IncidRegAc_GCM_1_Test {

    private IncidRegAc mActivity;
    private CleanUserEnum whatToClean = CleanUserEnum.CLEAN_PEPE;
    IdlingResourceForIntentServ idlingResource;
    Usuario pepe;
    UsuarioComunidad pepeUserComu;
    NotificationManager mNotifyManager;
    private int messageId = INCIDENCIA.getTitleRsc();

    @Rule
    public IntentsTestRule<IncidRegAc> intentRule = new IntentsTestRule<IncidRegAc>(IncidRegAc.class) {

        @Override
        protected void beforeActivityLaunched()
        {
            Context context = InstrumentationRegistry.getTargetContext();
            updateIsGcmTokenSentServer(false, context);
            try {
                // Pepe hasn't got a gcmToken.
                checkState(ServOne.getGcmToken() == null);
            } catch (UiException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Intent getActivityIntent()
        {
            try {
                pepe = signUpAndUpdateTk(COMU_ESCORIAL_PEPE);
                pepeUserComu = ServOne.seeUserComusByUser().get(0);
            } catch (UiException | IOException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent();
            long comunidadIdIntent = pepeUserComu.getComunidad().getC_Id();
            intent.putExtra(COMUNIDAD_ID.key, comunidadIdIntent);
            return intent;
        }
    };

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(4000);
    }

    @Before
    public void setUp() throws Exception
    {
        mActivity = intentRule.getActivity();
        mNotifyManager = (NotificationManager) mActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        idlingResource = new IdlingResourceForIntentServ(mActivity, new GcmRegistrationIntentService());
        Espresso.registerIdlingResources(idlingResource);
    }

    @After
    public void tearDown() throws Exception
    {
        updateIsGcmTokenSentServer(false, mActivity);
        mNotifyManager.cancel(messageId);
        Espresso.unregisterIdlingResources(idlingResource);
        cleanOptions(whatToClean);
    }

    //  ===========================================================================

    /**
     * Test para GcmRegistrationIntentService methods.
     */
    @Test
    public void testRegistrationGcmToken() throws Exception
    {
        // Preconditions for the test.
        assertThat(checkPlayServices(mActivity), is(true));
        assertThat(isRegisteredUser(mActivity), is(true));

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        assertThat(refreshedToken, notNullValue());
        assertThat(isGcmTokenSentServer(mActivity), is(true));
        assertThat(ServOne.getGcmToken(), is(refreshedToken));
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Test
    public void testReceiveNotification() throws Exception
    {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            // Preconditions for the test: TOKEN en BD.
            assertThat(ServOne.getGcmToken(), is(FirebaseInstanceId.getInstance().getToken()));

            IncidImportancia incidPepe =
                    new IncidImportancia.IncidImportanciaBuilder(doIncidencia(pepe.getUserName(), "Incidencia One", pepeUserComu.getComunidad().getC_Id(), (short) 43))
                    .usuarioComunidad(pepeUserComu)
                    .importancia((short) 3)
                    .build();
            assertThat(IncidenciaServ.regIncidImportancia(incidPepe), is(2));

            NotificationManager mManager = (NotificationManager) mActivity.getSystemService(Context.NOTIFICATION_SERVICE);
            // Verifico recepción de notificación.
            Thread.sleep(2000);
            assertThat(mManager.getActiveNotifications().length, is(1));
            StatusBarNotification barNotification = mManager.getActiveNotifications()[0];
            assertThat(barNotification.getId(), is(INCIDENCIA.getTitleRsc()));
        }
    }
}