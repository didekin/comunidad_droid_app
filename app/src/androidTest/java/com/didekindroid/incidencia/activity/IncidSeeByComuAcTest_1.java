package com.didekindroid.incidencia.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.common.IdlingResourceForIntentServ;
import com.didekindroid.common.UiException;
import com.didekindroid.incidencia.gcm.GcmRegistrationIntentServ;
import com.didekindroid.usuario.activity.utils.CleanUserEnum;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.core.deps.guava.base.Preconditions.checkState;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.common.utils.UIutils.checkPlayServices;
import static com.didekindroid.common.utils.UIutils.isGcmTokenSentServer;
import static com.didekindroid.common.utils.UIutils.isRegisteredUser;
import static com.didekindroid.common.utils.UIutils.updateIsGcmTokenSentServer;
import static com.didekindroid.incidencia.activity.utils.IncidenciaMenuTestUtils.INCID_SEE_CLOSED_BY_USER_AC;
import static com.didekindroid.incidencia.activity.utils.IncidenciaMenuTestUtils.INCID_REG_AC;
import static com.didekindroid.incidencia.gcm.AppGcmListenerServ.TypeMsgHandler.INCIDENCIA;
import static com.didekindroid.usuario.activity.utils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.common.utils.ActivityTestUtils.cleanOptions;
import static com.didekindroid.common.utils.ActivityTestUtils.signUpAndUpdateTk;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 23/11/15
 * Time: 18:45
 */
@RunWith(AndroidJUnit4.class)
public class IncidSeeByComuAcTest_1 {

    private IncidSeeByComuAc mActivity;
    private CleanUserEnum whatToClean = CLEAN_PEPE;
    IdlingResourceForIntentServ idlingResource;
    NotificationManager mNotifyManager;
    private int messageId = INCIDENCIA.getTitleRsc();

    @Rule
    public IntentsTestRule<IncidSeeByComuAc> activityRule = new IntentsTestRule<IncidSeeByComuAc>(IncidSeeByComuAc.class) {

        @Override
        protected void beforeActivityLaunched()
        {
            try {
                signUpAndUpdateTk(COMU_ESCORIAL_PEPE);
                Context context = InstrumentationRegistry.getTargetContext();
                updateIsGcmTokenSentServer(false, context);
                checkState(ServOne.getGcmToken() == null);

            } catch (UiException e) {
                e.printStackTrace();
            }
        }
    };

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(5000);
    }


    @Before
    public void setUp() throws Exception
    {
        mActivity = activityRule.getActivity();
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

    @Test
    public void testOnCreate() throws Exception
    {
        assertThat(isRegisteredUser(mActivity), is(true));
        assertThat(mActivity, notNullValue());
        onView(withId(R.id.appbar)).check(matches(isDisplayed()));

        onView(withId(R.id.incid_see_by_comu_ac)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_see_by_comu_frg)).check(matches(isDisplayed()));
        // No hay incidencias registradas. La vista forma parte de la jerarquía de vistas de la página.
        onView(withId(android.R.id.list)).check(matches(not(isDisplayed())));
        onView(withId(android.R.id.empty)).check(matches(isDisplayed()));
    }

    @Test
    public void testOnCreateGcm() throws UiException
    {
        // Preconditions for the test.
        assertThat(checkPlayServices(mActivity), is(true));

        assertThat(isGcmTokenSentServer(mActivity), is(true));
        assertThat(ServOne.getGcmToken(), notNullValue());
    }

    @Test
    public void testOnNavigateUp()
    {
        onView(withContentDescription("Navigate up")).check(matches(isDisplayed()));
        onView(CoreMatchers.allOf(
                        withContentDescription("Navigate up"),
                        isClickable())
        ).check(matches(isDisplayed()));
    }

    @Test
    public void testIncidSeeClosedByComuMn() throws InterruptedException
    {
        INCID_SEE_CLOSED_BY_USER_AC.checkMenuItem_WTk(mActivity);
    }

    @Test
    public void testIncidRegMn() throws InterruptedException
    {
        INCID_REG_AC.checkMenuItem_WTk(mActivity);
    }
}