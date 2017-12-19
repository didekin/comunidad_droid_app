package com.didekindroid.incidencia.list;

import android.content.Intent;
import android.os.Build;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekinlib.model.comunidad.Comunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static android.app.TaskStackBuilder.create;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.comunidad.utils.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidSeeByComuAcLayout;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidSeeGenericFrLayout;
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_REG_AC;
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_SEE_OPEN_BY_COMU_AC;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_CLOSED_LIST_FLAG;
import static com.didekindroid.testutil.ActivityTestUtils.checkAppBarMnNotExist;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.testutil.ActivityTestUtils.cleanTasks;
import static com.didekindroid.testutil.ActivityTestUtils.isStatementTrue;
import static com.didekindroid.testutil.ActivityTestUtils.isViewDisplayed;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_DROID;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_DROID;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpWithTkGetComu;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.seeUserComuByUserFrRsId;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 02/02/16
 * Time: 18:00
 */
@RunWith(AndroidJUnit4.class)
public class IncidSeeByComuAc_Close_Mn_Test {

    Comunidad comunidadInIntent;

    @Rule
    public IntentsTestRule<IncidSeeByComuAc> activityRule = new IntentsTestRule<IncidSeeByComuAc>(IncidSeeByComuAc.class, true, true) {

        @Override
        protected Intent getActivityIntent()
        {
            try {
                comunidadInIntent = signUpWithTkGetComu(COMU_REAL_DROID);
            } catch (IOException | UiException e) {
                fail();
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                create(getTargetContext()).addParentStack(IncidSeeByComuAc.class).startActivities();
            }

            Intent intent = new Intent();
            // Precondition: closed incidencias.
            intent.putExtra(COMUNIDAD_ID.key, comunidadInIntent.getC_Id()).putExtra(INCID_CLOSED_LIST_FLAG.key, true);
            return intent;
        }
    };

    private IncidSeeByComuAc activity;

    @Before
    public void setUp() throws Exception
    {
        activity = activityRule.getActivity();
    }

    @After
    public void tearDown() throws Exception
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cleanTasks(activityRule.getActivity());
        }
        cleanOptions(CLEAN_DROID);
    }

    // ============================================================
    //    ..... Empty list ....
    // ============================================================

    @Test
    public void testOnCreateEmptyList() throws Exception
    {
        IncidSeeByComuFr fr = (IncidSeeByComuFr) activity.getSupportFragmentManager().findFragmentByTag(IncidSeeByComuFr.class.getName());
        waitAtMost(6, SECONDS).until(isStatementTrue(
                fr != null
                        && fr.viewer != null
                        && fr.viewer.emptyListView != null)
        );
        TimeUnit.SECONDS.sleep(2);
        waitAtMost(2, SECONDS).until(isViewDisplayed(withText(R.string.no_incidencia_to_show)));
        // CheckUp.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            checkUp(seeUserComuByUserFrRsId);
        }
    }

    // ============================================================
    //    ..... ACTION BAR ....
    // ============================================================

    @Test
    public void testIncidSeeOpenByComuMn_1() throws InterruptedException
    {
        // Precondition
        assertThat(activity.getTitle(), is(activity.getText(R.string.incid_closed_by_user_ac_label)));
        assertThat(activity.getIntent().getBooleanExtra(INCID_CLOSED_LIST_FLAG.key, false), is(true));
        // Run
        INCID_SEE_OPEN_BY_COMU_AC.checkMenuItem(activity);
        // Checks.
        assertThat(activity.getTitle(), is(activity.getText(R.string.incid_see_by_user_ac_label)));
        assertThat(activity.getIntent().getBooleanExtra(INCID_CLOSED_LIST_FLAG.key, false), is(false));
        checkAppBarMnNotExist(activity, R.id.incid_see_open_by_comu_ac_mn);
    }

    @Test
    public void testIncidSeeOpenByComuMn_2() throws InterruptedException
    {
        INCID_SEE_OPEN_BY_COMU_AC.checkMenuItem(activity);
        // CheckUp.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            checkUp(seeUserComuByUserFrRsId);
        }
    }

    @Test
    public void testIncidRegMn() throws InterruptedException
    {
        INCID_REG_AC.checkMenuItem(activity);
        intended(hasExtra(COMUNIDAD_ID.key, comunidadInIntent.getC_Id()));
        checkUp(incidSeeByComuAcLayout, incidSeeGenericFrLayout);
    }
}