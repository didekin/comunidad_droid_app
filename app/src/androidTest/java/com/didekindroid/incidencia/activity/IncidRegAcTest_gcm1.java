package com.didekindroid.incidencia.activity;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.core.deps.guava.base.Preconditions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.serviceone.domain.Comunidad;
import com.didekindroid.common.IdlingResourceForIntentServ;
import com.didekindroid.common.UiException;
import com.didekindroid.incidencia.gcm.GcmRegistrationIntentServ;
import com.didekindroid.usuario.activity.utils.CleanUserEnum;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static com.didekindroid.common.utils.AppIntentExtras.COMUNIDAD_ID;
import static com.didekindroid.common.utils.UIutils.checkPlayServices;
import static com.didekindroid.common.utils.UIutils.isGcmTokenSentServer;
import static com.didekindroid.common.utils.UIutils.updateIsGcmTokenSentServer;
import static com.didekindroid.common.utils.UIutils.updateIsRegistered;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.cleanOptions;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.signUpAndUpdateTk;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
import static org.hamcrest.CoreMatchers.is;
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

    @Rule
    public IntentsTestRule<IncidRegAc> intentRule = new IntentsTestRule<IncidRegAc>(IncidRegAc.class) {

        @Override
        protected void beforeActivityLaunched()
        {
            Context context = InstrumentationRegistry.getTargetContext();
            Preconditions.checkState(!isGcmTokenSentServer(context));
        }

        @Override
        protected Intent getActivityIntent()
        {
            try {
                signUpAndUpdateTk(COMU_ESCORIAL_PEPE);
            } catch (UiException e) {
                e.printStackTrace();
            }
            List<Comunidad> comunidadesUserOne = null;
            try {
                comunidadesUserOne = ServOne.getComusByUser();
            } catch (UiException e) {
            }
            Intent intent = new Intent();
            long comunidadIdIntent = comunidadesUserOne != null ? comunidadesUserOne.get(0).getC_Id() : 0;
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
        idlingResource = new IdlingResourceForIntentServ(mActivity, new GcmRegistrationIntentServ());
        Espresso.registerIdlingResources(idlingResource);
    }

    @After
    public void tearDown() throws Exception
    {
        Espresso.unregisterIdlingResources(idlingResource);
        updateIsGcmTokenSentServer(false, mActivity);
        cleanOptions(whatToClean);
    }

    //  ===========================================================================

    @Test
    public void testOnCreate() throws Exception
    {
        // Precondition for the test.
        assertThat(checkPlayServices(mActivity), is(true));

        assertThat(isGcmTokenSentServer(mActivity), is(true));
    }
}