package com.didekindroid.incidencia.list;

import android.content.Intent;
import android.os.Build;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.app.TaskStackBuilder.create;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static com.didekindroid.comunidad.util.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_CLOSED_LIST_FLAG;
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_SEE_CLOSED_BY_COMU_AC;
import static com.didekindroid.lib_one.testutil.UiTestUtil.cleanTasks;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_DROID;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.testutil.ActivityTestUtil.checkAppBarMnNotExist;
import static com.didekindroid.testutil.ActivityTestUtil.checkUp;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.seeUserComuByUserFrRsId;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_REAL_DROID;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.signUpGetComu;
import static org.hamcrest.CoreMatchers.is;

/**
 * User: pedro@didekin
 * Date: 02/02/16
 * Time: 18:00
 */
@RunWith(AndroidJUnit4.class)
public class IncidSeeByComuAc_Open_Mn_Test {

    @Rule
    public IntentsTestRule<IncidSeeByComuAc> activityRule = new IntentsTestRule<IncidSeeByComuAc>(IncidSeeByComuAc.class, true, true) {

        @Override
        protected Intent getActivityIntent()
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                create(getTargetContext()).addParentStack(IncidSeeByComuAc.class).startActivities();
            }
            return new Intent()
                    .putExtra(COMUNIDAD_ID.key, c_id)
                    .putExtra(INCID_CLOSED_LIST_FLAG.key, false);
        }
    };

    private static long c_id;
    private IncidSeeByComuAc activity;

    @BeforeClass
    public static void setUpStatic() throws Exception
    {
        c_id = signUpGetComu(COMU_REAL_DROID).getC_Id();
    }

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
    }

    @AfterClass
    public static void cleanStatic()
    {
        cleanOptions(CLEAN_DROID);
    }

    // ============================================================
    //    ..... ACTION BAR ....
    // ============================================================

    @Test
    public void testOnCreate()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            checkUp(seeUserComuByUserFrRsId);
        }
    }

    @Test
    public void testIncidSeeCloseByComuMn_1()
    {
        // Precondition
        assertThat(activity.getTitle(), is(activity.getText(R.string.incid_see_by_user_ac_label)));
        assertThat(activity.getIntent().getBooleanExtra(INCID_CLOSED_LIST_FLAG.key, false), is(false));
        // Run
        INCID_SEE_CLOSED_BY_COMU_AC.checkItem(activity);
        // Checks.
        assertThat(activity.getTitle(), is(activity.getText(R.string.incid_closed_by_user_ac_label)));
        assertThat(activity.getIntent().getBooleanExtra(INCID_CLOSED_LIST_FLAG.key, false), is(true));
        checkAppBarMnNotExist(activity, R.id.incid_see_closed_by_comu_ac_mn);
    }

    @Test
    public void testIncidSeeCloseByComuMn_2()
    {
        INCID_SEE_CLOSED_BY_COMU_AC.checkItem(activity);
        // CheckUp.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            checkUp(seeUserComuByUserFrRsId);
        }
    }
}