package com.didekindroid.incidencia.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.comunidad.Comunidad;
import com.didekindroid.exception.UiException;
import com.didekindroid.testutil.IdlingResourceForIntentServ;
import com.didekindroid.usuario.testutil.UsuarioDataTestUtils;
import com.didekindroid.R;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Objects;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.testutil.ActivityTestUtils.clickNavigateUp;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDao;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.comunidad.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.incidencia.firebase.IncidFirebaseDownMsgHandler.INCIDENCIA_OPEN;
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_REG_AC;
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_SEE_CLOSED_BY_COMU_AC;
import static com.didekindroid.usuariocomunidad.UserComuService.AppUserComuServ;
import static com.didekindroid.usuariocomunidad.testutil.UserComuMenuTestUtil.SEE_USERCOMU_BY_COMU_AC;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.regSeveralUserComuSameUser;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
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
    private UsuarioDataTestUtils.CleanUserEnum whatToClean = CLEAN_JUAN;
    IdlingResourceForIntentServ idlingResource;
    Comunidad comunidadInIntent;
    NotificationManager mNotifyManager;
    private int messageId = INCIDENCIA_OPEN.getBarNotificationId();
    // Layouts to check in navigate-up.
    int activityLayoutId = R.id.incid_see_open_by_comu_ac;
    int secondLayoutId = R.id.incid_see_generic_layout;

    @Rule
    public IntentsTestRule<IncidSeeOpenByComuAc> activityRule = new IntentsTestRule<IncidSeeOpenByComuAc>(IncidSeeOpenByComuAc.class) {

        @Override
        protected void beforeActivityLaunched()
        {
            Context context = InstrumentationRegistry.getTargetContext();
            TKhandler.updateIsGcmTokenSentServer(false);
            try {
                Objects.equals(usuarioDao.getGcmToken() == null, true);
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
                comunidadInIntent = AppUserComuServ.seeUserComusByUser().get(0).getComunidad();
                Intent intent = new Intent();
                intent.putExtra(COMUNIDAD_ID.key, comunidadInIntent.getC_Id());
                return intent;
            } catch (IOException e) {
                e.printStackTrace();
                fail();
            } catch (UiException e) {
                e.printStackTrace();
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
//        idlingResource = new IdlingResourceForIntentServ(mActivity, new RegGcmIntentService());
        Espresso.registerIdlingResources(idlingResource);
    }

    @After
    public void tearDown() throws Exception
    {
        Espresso.unregisterIdlingResources(idlingResource);
        TKhandler.updateIsGcmTokenSentServer(false);
        mNotifyManager.cancel(messageId);
        cleanOptions(whatToClean);
    }

    @Test
    public void testOnCreate_1() throws Exception
    {
        assertThat(TKhandler.isRegisteredUser(), is(true));
        assertThat(mActivity, notNullValue());
        onView(withId(R.id.appbar)).check(matches(isDisplayed()));

        onView(withId(activityLayoutId)).check(matches(isDisplayed()));
        onView(withId(secondLayoutId)).check(matches(isDisplayed()));
        // No hay incidencias registradas.
        onView(withId(android.R.id.list)).check(matches(not(isDisplayed())));
        onView(withId(android.R.id.empty)).check(matches(isDisplayed()));

        assertThat(mActivity.mComunidadSelected, is(comunidadInIntent));
        onView(allOf(
                withId(R.id.app_spinner_1_dropdown_item),
                withParent(withId(R.id.incid_reg_comunidad_spinner))
        )).check(matches(withText(is(comunidadInIntent.getNombreComunidad()))
        )).check(matches(isDisplayed()));

        clickNavigateUp();
    }

//  ================================ MENU ====================================

    @Test
    public void testIncidSeeClosedByComuMn() throws InterruptedException
    {
        INCID_SEE_CLOSED_BY_COMU_AC.checkMenuItem_WTk(mActivity);
        checkUp(activityLayoutId, secondLayoutId);
    }

    @Test
    public void testIncidRegMn() throws InterruptedException
    {
        INCID_REG_AC.checkMenuItem_WTk(mActivity);
        checkUp(activityLayoutId, secondLayoutId);
    }

    @Test
    public void testSeeUserComuByComuMn() throws InterruptedException
    {
        SEE_USERCOMU_BY_COMU_AC.checkMenuItem_WTk(mActivity);
        intended(hasExtra(COMUNIDAD_ID.key, comunidadInIntent.getC_Id()));
        checkUp(activityLayoutId, secondLayoutId);
    }
}