package com.didekindroid.common.activity;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.usuario.testutils.CleanUserEnum;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekin.common.exception.DidekinExceptionMsg.INCIDENCIA_COMMENT_WRONG_INIT;
import static com.didekin.common.exception.DidekinExceptionMsg.INCIDENCIA_NOT_REGISTERED;
import static com.didekin.common.exception.DidekinExceptionMsg.RESOLUCION_DUPLICATE;
import static com.didekin.common.exception.DidekinExceptionMsg.USER_DATA_NOT_MODIFIED;
import static com.didekindroid.common.TokenHandler.TKhandler;
import static com.didekindroid.common.testutils.ActivityTestUtils.checkToastInTest;
import static com.didekindroid.common.testutils.ActivityTestUtils.cleanOptions;
import static com.didekindroid.common.testutils.ActivityTestUtils.signUpAndUpdateTk;
import static com.didekindroid.common.utils.UIutils.isRegisteredUser;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_PLAZUELA5_JUAN;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 17/11/15
 * Time: 10:07
 */
@SuppressWarnings({"ThrowableInstanceNeverThrown", "ThrowableResultOfMethodCallIgnored"})
@RunWith(AndroidJUnit4.class)
public class UiExceptionTests_2 extends UiExceptionAbstractTest {

    //    IncidenciaUser incidJuanReal1;
    private MockActivity mActivity;

    @Rule
    public IntentsTestRule<MockActivity> intentRule = new IntentsTestRule<MockActivity>(MockActivity.class) {

        @Override
        protected Intent getActivityIntent()
        {
            return super.getActivityIntent();
        }

        @Override
        protected void beforeActivityLaunched()
        {
            try {
                signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
                /*UsuarioComunidad juanReal = ServOne.seeUserComusByUser().get(0);
                incidJuanReal1 = new IncidenciaUser.IncidenciaUserComuBuilder(doIncidencia("Incidencia Real One", juanReal.getComunidad().getC_Id(), (short) 43))
                        .userComu(juanReal)
                        .importancia((short) 3).build();
                IncidenciaServ.regIncidenciaUser(incidJuanReal1);
                Incidencia incidenciaDb = IncidenciaServ.seeIncidsOpenByComu(juanReal.getComunidad().getC_Id()).get(0).getResolucion();
                incidJuanReal1 = IncidenciaServ.getIncidenciaUserByIncid(incidenciaDb.getIncidenciaId());*/
            } catch (UiException e) {
                e.printStackTrace();
            }
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
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(CleanUserEnum.CLEAN_JUAN);
        Thread.sleep(4000);
    }

    //  ===========================================================================

    @Test
    public void testIncidReg()
    {
        // Preconditions.
        assertThat(isRegisteredUser(mActivity), is(true));
        assertThat(TKhandler.getTokensCache(), notNullValue());

        final UiException ue = getUiException(INCIDENCIA_NOT_REGISTERED);
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                ue.processMe(mActivity, new Intent());
            }
        });

        checkToastInTest(R.string.incidencia_not_registered, mActivity);
        onView(withId(R.id.incid_reg_ac_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void testIncidSeeByComu() throws Exception
    {
        // Preconditions.
        assertThat(isRegisteredUser(mActivity), is(true));
        assertThat(TKhandler.getTokensCache(), notNullValue());

        final UiException ue = getUiException(INCIDENCIA_COMMENT_WRONG_INIT);
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                ue.processMe(mActivity, new Intent());
            }
        });

        checkToastInTest(R.string.incidencia_wrong_init, mActivity);
        onView(withId(R.id.incid_see_by_comu_ac)).check(matches(isDisplayed()));
    }

    @Test
    public void testResolucionDup() throws Exception
    {
        final UiException ue = getUiException(RESOLUCION_DUPLICATE);

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                ue.processMe(mActivity, new Intent());
            }
        });
        // TODO: necesita intent. Añadirlo cuando lo haga en la activity.
       /* checkToastInTest(R.string.resolucion_duplicada, mActivity);
        onView(withId(R.id.incid_resolucion_edit_ac_layout)).check(matches(isDisplayed()));*/
    }

    @Test
    public void testUserDataAc() throws Exception
    {
        // Preconditions.
        assertThat(isRegisteredUser(mActivity), is(true));
        assertThat(TKhandler.getTokensCache(), notNullValue());

        final UiException ue = getUiException(USER_DATA_NOT_MODIFIED);
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                ue.processMe(mActivity, new Intent());
            }
        });

        checkToastInTest(R.string.user_data_not_modified_msg, mActivity);
        onView(withId(R.id.user_data_ac_layout)).check(matches(isDisplayed()));
    }

    //  ============================== HELPERS  ===================================
}