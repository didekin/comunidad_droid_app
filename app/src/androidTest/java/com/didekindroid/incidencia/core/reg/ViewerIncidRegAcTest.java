package com.didekindroid.incidencia.core.reg;

import android.support.annotation.NonNull;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.incidencia.dominio.AmbitoIncidencia;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Incidencia;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.isComuSpinnerWithText;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidSeeByComuAcLayout;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.lib_one.util.UiUtil.getErrorMsgBuilder;
import static com.didekindroid.testutil.ActivityTestUtil.checkSubscriptionsOnStop;
import static com.didekindroid.testutil.ActivityTestUtil.isToastInView;
import static com.didekindroid.testutil.ActivityTestUtil.isViewDisplayedAndPerform;
import static com.didekindroid.usuariocomunidad.repository.UserComuDao.userComuDao;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.makeListTwoUserComu;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.regTwoUserComuSameUser;
import static com.didekinlib.model.common.dominio.ValidDataPatterns.LINE_BREAK;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 31/03/17
 * Time: 15:24
 */
@RunWith(AndroidJUnit4.class)
public class ViewerIncidRegAcTest {

    private static Comunidad comuReal;
    private static Comunidad comuPlazuela5;
    private ViewerIncidRegAc viewer;
    private IncidRegAc activity;

    @Rule
    public ActivityTestRule<IncidRegAc> activityRule = new ActivityTestRule<>(IncidRegAc.class);

    @BeforeClass
    public static void setUpStatic() throws Exception
    {
        regTwoUserComuSameUser(makeListTwoUserComu());
        List<Comunidad> comunidades = userComuDao.getComusByUser().blockingGet();
        comuReal = comunidades.get(0);
        comuPlazuela5 = comunidades.get(1);
    }

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        waitAtMost(4, SECONDS).until(() -> activity.viewer != null);
        viewer = activity.viewer;
    }

    @After
    public void clearUp()
    {
        viewer.clearSubscriptions();
    }

    @AfterClass
    public static void cleanStatic()
    {
        cleanOptions(CLEAN_JUAN);
    }

    //  ================================ TESTS ===================================

    @Test
    public void testRegisterIncidencia_1()
    {
        // Check change of activity.
        viewer.registerIncidencia(doIncidImportancia(), getErrorMsgBuilder(activity));
        waitAtMost(6, SECONDS).until(isViewDisplayedAndPerform(withId(incidSeeByComuAcLayout)));
    }

    @Test
    public void testRegisterIncidencia_2()
    {
        assertThat(viewer.getController(), notNullValue());
        assertThat(viewer.getController().isRegisteredUser(), is(true));
        // The flag should be turned to true.
        onView(withId(R.id.incid_reg_frg)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_reg_ac_button)).check(matches(isDisplayed()));

        final StringBuilder errors = getErrorMsgBuilder(activity);
        errors.append(activity.getResources().getString(R.string.incid_reg_importancia)).append(LINE_BREAK.getRegexp());
        activity.runOnUiThread(() -> assertThat(viewer.registerIncidencia(null, errors), is(false)));
        // Check errors.
        waitAtMost(4, SECONDS).until(isToastInView(R.string.incid_reg_importancia, activity));

        // testClearSubscriptions
        checkSubscriptionsOnStop(activity, viewer.getController());
    }

    @Test
    public void testOnSuccessRegisterIncidImportancia_1()
    {
        viewer.onSuccessRegisterIncidImportancia(comuPlazuela5);
        // Check change of activity.
        waitAtMost(6, SECONDS).until(isViewDisplayedAndPerform(withId(incidSeeByComuAcLayout)));
        // Check comuSpinner initialization.
        waitAtMost(4, SECONDS).until(isComuSpinnerWithText(comuPlazuela5.getNombreComunidad()));
    }

    @Test
    public void testOnSuccessRegisterIncidImportancia_2()
    {
        // Check spinner initialization with the other comunidad.
        viewer.onSuccessRegisterIncidImportancia(comuReal);
        waitAtMost(4, SECONDS).until(isComuSpinnerWithText(comuReal.getNombreComunidad()));
    }

    // ...................................... HELPERS ..........................................

    @NonNull
    private IncidImportancia doIncidImportancia()
    {
        return new IncidImportancia.IncidImportanciaBuilder(
                new Incidencia.IncidenciaBuilder()
                        .comunidad(new Comunidad.ComunidadBuilder().c_id(comuReal.getC_Id()).build())
                        .descripcion("Descripción válida")
                        .ambitoIncid(new AmbitoIncidencia((short) 4))
                        .build()
        ).build();
    }
}