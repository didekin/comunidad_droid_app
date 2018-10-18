package com.didekindroid.incidencia.core.edit;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.testutil.ActivityTestUtil;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_RESOLUCION_BUNDLE;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkScreenEditMaxPowerFrErase;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.isComuSpinnerWithText;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidSeeByComuAcLayout;
import static com.didekindroid.incidencia.testutils.IncidTestData.insertGetIncidImportancia;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.testutil.ActivityTestUtil.isResourceIdDisplayed;
import static com.didekindroid.usuariocomunidad.repository.UserComuDao.userComuDao;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.makeListTwoUserComu;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.regTwoUserComuSameUser;
import static io.reactivex.Single.just;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 07/04/17
 * Time: 10:08
 */
@SuppressWarnings("WeakerAccess")
@RunWith(AndroidJUnit4.class)
public class ViewerIncidEditMaxFr_erase_Test {

    IncidEditAc activity;
    UsuarioComunidad comuRealJuan;
    UsuarioComunidad comuPlazuelaJuan;
    IncidEditMaxFr fragment;


    @Rule
    public IntentsTestRule<IncidEditAc> activityRule = new IntentsTestRule<IncidEditAc>(IncidEditAc.class) {
        @Override
        protected Intent getActivityIntent()
        {
            try {
                regTwoUserComuSameUser(makeListTwoUserComu());
            } catch (Exception e) {
                fail();
            }
            List<UsuarioComunidad> userComus = userComuDao.seeUserComusByUser().blockingGet();
            comuRealJuan = userComus.get(0);
            comuPlazuelaJuan = userComus.get(1);
            // Perfil pro, iniciador de la incidencia. Incidencia sin resolución abierta.

            return new Intent().putExtra(INCID_RESOLUCION_BUNDLE.key, new IncidAndResolBundle(insertGetIncidImportancia(comuRealJuan, (short) 3), false));
        }
    };

    @Before
    public void setUp() throws Exception
    {
        activity = activityRule.getActivity();
        fragment = (IncidEditMaxFr) activity.getSupportFragmentManager().findFragmentByTag(IncidEditMaxFr.class.getName());
        waitAtMost(4, SECONDS).until(() -> fragment != null && fragment.viewer != null);
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(CLEAN_JUAN);
    }

    //    ============================  TESTS  ===================================

    @Test
    public void testOnClickButtonErase()
    {
        // Preconditions.
        checkScreenEditMaxPowerFrErase(activity.resolBundle);

        activity.runOnUiThread(() -> fragment.viewer.onClickButtonErase());
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(incidSeeByComuAcLayout));
    }

    @Test
    public void testOnSuccessEraseIncidencia()
    {
        ActivityTestUtil.checkComuInSpinner(comuRealJuan);
        // Exec with the other comunidad as parameter.
        fragment.viewer.onSuccessEraseIncidencia(comuPlazuelaJuan.getComunidad());
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(incidSeeByComuAcLayout));
        // Check comuSpinner initialization with the comunidad in the method parameter.
        waitAtMost(4, SECONDS).until(isComuSpinnerWithText(comuPlazuelaJuan.getComunidad().getNombreComunidad()));
    }

    @Test
    public void test_EraseIncidenciaObserver()
    {
        just(1).subscribeWith(fragment.viewer.new EraseIncidenciaObserver(comuRealJuan.getComunidad()));
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(incidSeeByComuAcLayout));
    }

}