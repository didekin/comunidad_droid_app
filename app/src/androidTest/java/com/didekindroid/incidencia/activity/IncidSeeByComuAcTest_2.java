package com.didekindroid.incidencia.activity;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.incidservice.domain.IncidenciaUser;
import com.didekin.serviceone.domain.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.common.UiException;
import com.didekindroid.usuario.activity.utils.CleanUserEnum;

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
import static com.didekindroid.common.utils.UIutils.isRegisteredUser;
import static com.didekindroid.incidencia.dominio.IncidenciaDomainTestUtils.doIncidencia;
import static com.didekindroid.usuario.activity.utils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.cleanOptions;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.signUpAndUpdateTk;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_REAL;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_REAL_JUAN;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 23/11/15
 * Time: 18:45
 */
@RunWith(AndroidJUnit4.class)
public class IncidSeeByComuAcTest_2 {

    private IncidSeeByComuAc mActivity;
    private IncidSeeByComuListFr mFragment;
    private CleanUserEnum whatToClean = CLEAN_PEPE;

    @Rule
    public IntentsTestRule<IncidSeeByComuAc> activityRule = new IntentsTestRule<IncidSeeByComuAc>(IncidSeeByComuAc.class) {

        @Override
        protected void beforeActivityLaunched()
        {
            try {
                signUpAndUpdateTk(COMU_REAL_JUAN);
                UsuarioComunidad juanUserComu = ServOne.seeUserComusByUser().get(0);
                IncidenciaUser incidPepeUser1 =  new IncidenciaUser.IncidenciaUserBuilder(doIncidencia("Incidencia One",juanUserComu.getComunidad().getC_Id(), (short) 43))
                        .usuario(juanUserComu.getUsuario())
                        .importancia((short) 3).build();
                IncidenciaUser incidPepeUser2 =  new IncidenciaUser.IncidenciaUserBuilder(doIncidencia("Incidencia Two",juanUserComu.getComunidad().getC_Id(), (short) 11))
                        .usuario(juanUserComu.getUsuario())
                        .importancia((short) 2).build();
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
        mFragment = (IncidSeeByComuListFr) mActivity.getFragmentManager().findFragmentById(R.id.incid_see_by_comu_frg);
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(whatToClean);
    }

    @Test
    public void testOnCreate() throws Exception
    {
        assertThat(isRegisteredUser(mActivity), is(true));
        onView(withId(R.id.appbar)).check(matches(isDisplayed()));

        IncidSeeByComuAdapter adapter = mFragment.mAdapter;
        assertThat(adapter.getCount(), is(0));
    }
}