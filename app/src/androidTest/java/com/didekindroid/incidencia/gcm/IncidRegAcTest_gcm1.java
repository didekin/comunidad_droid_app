package com.didekindroid.incidencia.gcm;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.usuario.dominio.UsuarioComunidad;
import com.didekindroid.common.IdlingResourceForIntentServ;
import com.didekindroid.common.UiException;
import com.didekindroid.incidencia.activity.IncidRegAc;
import com.didekindroid.usuario.activity.utils.CleanUserEnum;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.core.deps.guava.base.Preconditions.checkState;
import static com.didekindroid.common.utils.AppKeysForBundle.COMUNIDAD_ID;
import static com.didekindroid.common.utils.UIutils.checkPlayServices;
import static com.didekindroid.common.utils.UIutils.isGcmTokenSentServer;
import static com.didekindroid.common.utils.UIutils.updateIsGcmTokenSentServer;
import static com.didekindroid.incidencia.gcm.AppGcmListenerServ.TypeMsgHandler.INCIDENCIA;
import static com.didekindroid.common.utils.ActivityTestUtils.cleanOptions;
import static com.didekindroid.common.utils.ActivityTestUtils.signUpAndUpdateTk;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_ESCORIAL_PEPE;
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
public class IncidRegAcTest_gcm1 {

    private IncidRegAc mActivity;
    private CleanUserEnum whatToClean = CleanUserEnum.CLEAN_PEPE;
    IdlingResourceForIntentServ idlingResource;
    private UsuarioComunidad pepeUserComu;
    NotificationManager mNotifyManager;
    private int messageId = INCIDENCIA.getTitleRsc();

    @Rule
    public IntentsTestRule<IncidRegAc> intentRule = new IntentsTestRule<IncidRegAc>(IncidRegAc.class) {

        @Override
        protected void beforeActivityLaunched()
        {
            Context context = InstrumentationRegistry.getTargetContext();
            updateIsGcmTokenSentServer(false, context);
            checkState(!isGcmTokenSentServer(context));
            try {
                checkState(ServOne.getGcmToken() == null);
            } catch (UiException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Intent getActivityIntent()
        {
            try {
                signUpAndUpdateTk(COMU_ESCORIAL_PEPE);
            } catch (UiException e) {
                e.printStackTrace();
            }
            try {
                pepeUserComu = ServOne.seeUserComusByUser().get(0);
            } catch (UiException e) {
            }
            Intent intent = new Intent();
            long comunidadIdIntent = pepeUserComu.getComunidad().getC_Id();
            intent.putExtra(COMUNIDAD_ID.extra, comunidadIdIntent);
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
        idlingResource = new IdlingResourceForIntentServ(mActivity, new GcmRegistrationIntentServ());
        Espresso.registerIdlingResources(idlingResource);
    }

    @After
    public void tearDown() throws Exception
    {
        Espresso.unregisterIdlingResources(idlingResource);
        updateIsGcmTokenSentServer(false, mActivity);
        mNotifyManager.cancel(messageId);
        cleanOptions(whatToClean);
    }

    //  ===========================================================================

    /**
     * Test para GcmRegistrationIntentServ methods.
     */
    @Test
    public void testOnCreate() throws Exception
    {
        // Preconditions for the test.
        assertThat(checkPlayServices(mActivity), is(true));

        assertThat(isGcmTokenSentServer(mActivity), is(true));
        assertThat(ServOne.getGcmToken(), notNullValue());
    }
}