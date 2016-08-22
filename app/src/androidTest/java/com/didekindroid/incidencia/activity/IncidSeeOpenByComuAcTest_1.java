package com.didekindroid.incidencia.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.usuario.dominio.Comunidad;
import com.didekindroid.R;
import com.didekindroid.common.activity.IdlingResourceForIntentServ;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.common.gcm.GcmRegistrationIntentService;
import com.didekindroid.usuario.testutils.CleanUserEnum;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.core.deps.guava.base.Preconditions.checkState;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.common.activity.BundleKey.COMUNIDAD_ID;
import static com.didekindroid.common.gcm.AppFirebaseMsgService.TypeMsgHandler.INCIDENCIA_OPEN;
import static com.didekindroid.common.testutils.ActivityTestUtils.checkNavigateUp;
import static com.didekindroid.common.testutils.ActivityTestUtils.cleanOptions;
import static com.didekindroid.common.testutils.ActivityTestUtils.regSeveralUserComuSameUser;
import static com.didekindroid.common.utils.UIutils.isRegisteredUser;
import static com.didekindroid.common.utils.UIutils.updateIsGcmTokenSentServer;
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_REG_AC;
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_SEE_CLOSED_BY_COMU_AC;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutils.UserMenuTestUtils.SEE_USERCOMU_BY_COMU_AC;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_REAL_JUAN;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 23/11/15
 * Time: 18:45
 */
/* Tests de presentación, navegación y notificaciones GCM. */
@RunWith(AndroidJUnit4.class)
public class IncidSeeOpenByComuAcTest_1 {

    private IncidSeeOpenByComuAc mActivity;
    private CleanUserEnum whatToClean = CLEAN_JUAN;
    IdlingResourceForIntentServ idlingResource;
    Comunidad comunidadInIntent;
    NotificationManager mNotifyManager;
    private int messageId = INCIDENCIA_OPEN.getTitleRsc();

    @Rule
    public IntentsTestRule<IncidSeeOpenByComuAc> activityRule = new IntentsTestRule<IncidSeeOpenByComuAc>(IncidSeeOpenByComuAc.class) {

        @Override
        protected void beforeActivityLaunched()
        {
            try {
                Context context = InstrumentationRegistry.getTargetContext();
                updateIsGcmTokenSentServer(false, context);
                checkState(ServOne.getGcmToken() == null);

            } catch (UiException e) {
                e.printStackTrace();
            }
        }

        /**
         * Preconditions:
         * 1. A comunidadId is passed as an intent extra.
         */
        @Override
        protected Intent getActivityIntent()
        {
            try {
                regSeveralUserComuSameUser(COMU_REAL_JUAN, COMU_PLAZUELA5_JUAN);
                comunidadInIntent = ServOne.seeUserComusByUser().get(0).getComunidad();
                Intent intent = new Intent();
                intent.putExtra(COMUNIDAD_ID.key, comunidadInIntent.getC_Id());
                return intent;
            } catch (UiException | IOException e) {
                e.printStackTrace();
                fail();
            }
            return null;
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
        mActivity = activityRule.getActivity();
        mNotifyManager = (NotificationManager) mActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        idlingResource = new IdlingResourceForIntentServ(mActivity, new GcmRegistrationIntentService());
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

    @Test
    public void testOnCreate_1() throws Exception
    {
        assertThat(isRegisteredUser(mActivity), is(true));
        assertThat(mActivity, notNullValue());
        onView(withId(R.id.appbar)).check(matches(isDisplayed()));

        onView(withId(R.id.incid_see_open_by_comu_ac)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_see_generic_layout)).check(matches(isDisplayed()));
        // No hay incidencias registradas.
        onView(withId(android.R.id.list)).check(matches(not(isDisplayed())));
        onView(withId(android.R.id.empty)).check(matches(isDisplayed()));

        checkNavigateUp();
    }

    @Test
    public void testOnCreate_2() throws InterruptedException
    {
        Thread.sleep(2000);

        assertThat(mActivity.mComunidadSelected, is(comunidadInIntent));
        onView(allOf(
                withId(R.id.app_spinner_1_dropdown_item),
                withParent(withId(R.id.incid_reg_comunidad_spinner))
        )).check(matches(withText(is(comunidadInIntent.getNombreComunidad()))
        )).check(matches(isDisplayed()));
    }

//  ================================ MENU ====================================

    @Test
    public void testIncidSeeClosedByComuMn() throws InterruptedException
    {
        INCID_SEE_CLOSED_BY_COMU_AC.checkMenuItem_WTk(mActivity);
    }

    @Test
    public void testIncidRegMn() throws InterruptedException
    {
        INCID_REG_AC.checkMenuItem_WTk(mActivity);
    }

    @Test
    public void testSeeUserComuByComuMn() throws InterruptedException
    {
        SEE_USERCOMU_BY_COMU_AC.checkMenuItem_WTk(mActivity);
        intended(hasExtra(COMUNIDAD_ID.key, comunidadInIntent.getC_Id()));
    }
}