package com.didekindroid.incidencia.core.reg;

import android.content.Intent;
import android.os.Build;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.incidencia.list.IncidSeeByComuAc;
import com.didekindroid.lib_one.incidencia.spinner.AmbitoIncidValueObj;
import com.didekindroid.usuariocomunidad.data.UserComuDataAc;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.app.TaskStackBuilder.create;
import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.comunidad.testutil.ComuTestData.COMU_LA_FUENTE;
import static com.didekindroid.comunidad.util.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_CLOSED_LIST_FLAG;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.doAmbitoAndDescripcion;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.doComunidadSpinner;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.doImportanciaSpinner;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidRegAcLayout;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidRegFrLayout;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidSeeByComuAcLayout;
import static com.didekindroid.lib_one.testutil.UiTestUtil.cleanTasks;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.testutil.ActivityTestUtil.checkBack;
import static com.didekindroid.testutil.ActivityTestUtil.checkSubscriptionsOnStop;
import static com.didekindroid.testutil.ActivityTestUtil.checkUp;
import static com.didekindroid.testutil.ActivityTestUtil.isToastInView;
import static com.didekindroid.testutil.ActivityTestUtil.isViewDisplayed;
import static com.didekindroid.testutil.ActivityTestUtil.isViewDisplayedAndPerform;
import static com.didekindroid.usuariocomunidad.UserComuBundleKey.USERCOMU_LIST_OBJECT;
import static com.didekindroid.usuariocomunidad.repository.UserComuDao.userComuDao;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.seeUserComuByUserFrRsId;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_LA_FUENTE_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_REAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.regSeveralUserComuSameUser;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 17/11/15
 * Time: 10:07
 */
@SuppressWarnings("ConstantConditions")
@RunWith(AndroidJUnit4.class)
public class IncidRegAcTest {

    private static List<UsuarioComunidad> usuarioComunidades;
    private AmbitoIncidValueObj ambitoObj = new AmbitoIncidValueObj((short) 10, "Calefacción comunitaria");
    private IncidRegAc activity;

    @Rule
    public IntentsTestRule<IncidRegAc> intentRule = new IntentsTestRule<IncidRegAc>(IncidRegAc.class) {
        @Override
        protected Intent getActivityIntent()
        {
            if (Build.VERSION.SDK_INT >= LOLLIPOP) {
                Intent intent0 = new Intent(getTargetContext(), UserComuDataAc.class)
                        .putExtra(USERCOMU_LIST_OBJECT.key,
                                new UsuarioComunidad.UserComuBuilder(
                                        usuarioComunidades.get(0).getComunidad(),
                                        usuarioComunidades.get(0).getUsuario()
                                ).userComuRest(usuarioComunidades.get(0)).build()
                        );
                Intent intent1 = new Intent(getTargetContext(), IncidSeeByComuAc.class).putExtra(INCID_CLOSED_LIST_FLAG.key, false);
                create(getTargetContext()).addNextIntent(intent0).addNextIntentWithParentStack(intent1).startActivities();
            }
            return new Intent().putExtra(COMUNIDAD_ID.key, usuarioComunidades.get(0).getComunidad().getC_Id());
        }
    };

    @BeforeClass
    public static void setUpStatic() throws Exception
    {
        regSeveralUserComuSameUser(COMU_ESCORIAL_PEPE, COMU_REAL_PEPE, COMU_LA_FUENTE_PEPE);
        usuarioComunidades = userComuDao.seeUserComusByUser().blockingGet();
    }

    @Before
    public void setUp() throws Exception
    {
        activity = intentRule.getActivity();
    }

    @After
    public void tearDown() throws Exception
    {
        if (Build.VERSION.SDK_INT >= LOLLIPOP) {
            cleanTasks(activity);
        }
    }

    @AfterClass
    public static void cleanStatic()
    {
        cleanOptions(CLEAN_PEPE);
    }

    /*  ================================ INTEGRATION ===================================*/

    @Test
    public void testRegisterIncidencia_1() throws InterruptedException
    {
        /* Caso NOT OK: descripción de incidencia no válida.*/
        doImportanciaSpinner(activity, 4);
        SECONDS.sleep(1);
        doAmbitoAndDescripcion(ambitoObj, "descripcion = not valid");
        onView(withId(R.id.incid_reg_ac_button)).perform(scrollTo(), click());

        waitAtMost(6, SECONDS).until(isToastInView(R.string.error_validation_msg, activity, R.string.incid_reg_descripcion));
        //  testOnStop
        checkSubscriptionsOnStop(activity, activity.viewer.getController());
    }

    @Test
    public void testRegisterIncidencia_2() throws InterruptedException
    {
        // Caso OK: incidencia CON datos de importancia.
        doImportanciaSpinner(activity, 4);
        SECONDS.sleep(1);
        doAmbitoAndDescripcion(ambitoObj, "descripcion es valida");

        onView(withId(R.id.incid_reg_ac_button)).perform(scrollTo(), click());
        waitAtMost(6, SECONDS).until(isViewDisplayedAndPerform(withId(incidSeeByComuAcLayout)));
        checkUp(seeUserComuByUserFrRsId);
    }

    @Test
    public void testRegisterIncidencia_3() throws InterruptedException
    {
        // Caso OK: incidencia SIN datos de importancia.
        doAmbitoAndDescripcion(ambitoObj, "descripcion is valid");
        SECONDS.sleep(1);
        onView(withId(R.id.incid_reg_ac_button)).perform(scrollTo(), click());

        waitAtMost(6, SECONDS).until(isViewDisplayed(withId(incidSeeByComuAcLayout)));
        checkBack(onView(withId(incidSeeByComuAcLayout)), incidRegAcLayout, incidRegFrLayout);
    }

    @Test
    public void testRegisterIncidencia_4() throws InterruptedException
    {
        // Probamos cambio de comunidad en spinner: Calle La Fuente.
        doComunidadSpinner(COMU_LA_FUENTE);
        SECONDS.sleep(1);
        // Registro de incidencia con importancia.
        doImportanciaSpinner(activity, 4);
        SECONDS.sleep(1);
        doAmbitoAndDescripcion(ambitoObj, "Incidencia La Fuente");
        onView(withId(R.id.incid_reg_ac_button)).perform(scrollTo(), click());

        waitAtMost(4, SECONDS).until(isViewDisplayedAndPerform(withId(incidSeeByComuAcLayout)));
        checkUp(seeUserComuByUserFrRsId);
    }

    //    =======================   UNIT TESTS ========================

    @Test
    public void testOnCreate()
    {
        assertThat(activity.getInjectedParentViewer(), notNullValue());
        assertThat(activity.incidRegFr.getArguments().getLong(COMUNIDAD_ID.key), is(usuarioComunidades.get(0).getComunidad().getC_Id()));
        assertThat(activity.incidRegFr.viewerInjector, instanceOf(IncidRegAc.class));
        assertThat(activity.incidRegFr.viewer.getParentViewer(), is(activity.viewer));

        checkUp(incidSeeByComuAcLayout);
    }
}