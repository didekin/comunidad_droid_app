package com.didekindroid.incidencia.core.edit;

import android.content.Intent;
import android.os.Bundle;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.exception.UiException;
import com.didekindroid.incidencia.core.IncidenciaDataDbHelper;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.incidencia.IncidDaoRemote.incidenciaDao;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetIncidImportancia;
import static com.didekindroid.incidencia.testutils.IncidUiTestUtils.checkDataEditMinFr;
import static com.didekindroid.incidencia.testutils.IncidUiTestUtils.doImportanciaSpinner;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_FLAG;
import static com.didekindroid.incidencia.utils.IncidFragmentTags.incid_edit_ac_frgs_tag;
import static com.didekindroid.security.SecurityTestUtils.updateSecurityData;
import static com.didekindroid.testutil.ActivityTestUtils.addSubscription;
import static com.didekindroid.testutil.ActivityTestUtils.checkBack;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.testutil.ActivityTestUtils.clickNavigateUp;
import static com.didekindroid.testutil.ActivityTestUtils.isResourceIdDisplayed;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN_AND_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.dao.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.makeUsuarioComunidad;
import static com.didekinlib.model.usuariocomunidad.Rol.PROPIETARIO;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 19/01/16
 * Time: 16:57
 * <p>
 * Dos incidImportancias registradas en BD, para la misma incidencia.
 * Usuario inicial en sesión SIN permisos para modificar o borrar una incidencia.
 */
@RunWith(AndroidJUnit4.class)
public class IncidEditAcMinTest {

    final static AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    IncidEditAc activity;
    IncidImportancia incidImportanciaIntent;

    @Rule
    public IntentsTestRule<IncidEditAc> activityRule = new IntentsTestRule<IncidEditAc>(IncidEditAc.class) {
        @Override
        protected Intent getActivityIntent()
        {
            try {
                IncidImportancia incidImportancia_0 = insertGetIncidImportancia(COMU_REAL_PEPE);
                // Registro userComu en misma comunidad.
                UsuarioComunidad userComuJuan = makeUsuarioComunidad(incidImportancia_0.getIncidencia().getComunidad(), USER_JUAN,
                        "portal", "esc", "plantaX", "door12", PROPIETARIO.function);
                userComuDaoRemote.regUserAndUserComu(userComuJuan).execute();
                updateSecurityData(USER_JUAN.getUserName(), USER_JUAN.getPassword());
                incidImportanciaIntent = incidenciaDao.seeIncidImportancia(incidImportancia_0.getIncidencia().getIncidenciaId()).getIncidImportancia();
            } catch (IOException | UiException e) {
                fail();
            }
            Intent intent = new Intent();
            intent.putExtra(INCID_IMPORTANCIA_OBJECT.key, incidImportanciaIntent);
            intent.putExtra(INCID_RESOLUCION_FLAG.key, false);
            return intent;
        }
    };

    IncidenciaDataDbHelper dbHelper;

    @Before
    public void setUp() throws Exception
    {
        activity = activityRule.getActivity();
        dbHelper = new IncidenciaDataDbHelper(activity);
        // Usuario sin registro previo de incidImportancia.
        assertThat(incidImportanciaIntent.getImportancia(), is((short) 0));
    }

    @After
    public void tearDown() throws Exception
    {
        dbHelper.close();
        cleanOptions(CLEAN_JUAN_AND_PEPE);
    }

//  ======================================= INTEGRATION TESTS  =====================================

    @Test
    public void testOnCreate_1() throws Exception
    {
        checkDataEditMinFr(dbHelper, activity, incidImportanciaIntent);
    }

    @Test
    public void testPressBack()
    {
        // CASO: presionamos TextView para ver la importancia dada por otros miembros, y luego hacemos BACK.
        onView(withId(R.id.incid_importancia_otros_view)).check(matches(isDisplayed())).perform(click());
        // BACK.
        waitAtMost(1, SECONDS).until(isResourceIdDisplayed(R.id.incid_see_usercomu_importancia_frg));
        checkBack(onView(withId(R.id.incid_see_usercomu_importancia_frg)));
        // Datos a la vista.
        checkDataEditMinFr(dbHelper, activity, incidImportanciaIntent);
    }

    @Test
    public void testUpNavigate()
    {
        /* CASO: presionamos TextView para ver la importancia dada por otros miembros, y luego Up (Volver).*/
        onView(withId(R.id.incid_importancia_otros_view)).check(matches(isDisplayed())).perform(click());
        // Up Navigate.
        waitAtMost(1, SECONDS).until(isResourceIdDisplayed(R.id.incid_see_usercomu_importancia_frg));
        clickNavigateUp();
        // Datos a la vista.
        checkDataEditMinFr(dbHelper, activity, incidImportanciaIntent);
    }

    @Test
    public void testModifyIncidImportanciaPressUp()
    {
        // Modificamos importancia y UP.
        short newImportancia = 1;
        doImportanciaSpinner(activity, newImportancia);
        // Modify.
        onView(withId(R.id.incid_edit_fr_modif_button)).perform(scrollTo(), click());
        // Check.
        waitAtMost(2, SECONDS).until(isResourceIdDisplayed(R.id.incid_see_open_by_comu_ac));

        checkUp();
        IncidImportancia newIncidImportancia = new IncidImportancia.IncidImportanciaBuilder(incidImportanciaIntent.getIncidencia())
                .copyIncidImportancia(incidImportanciaIntent)
                .importancia(newImportancia)
                .build();
        checkDataEditMinFr(dbHelper, activity, newIncidImportancia);
    }

    @Test
    public void testModifyIncidImportanciaPressBack()
    {
        // Modify.
        onView(withId(R.id.incid_edit_fr_modif_button)).perform(scrollTo(), click());
        // Check.
        waitAtMost(2, SECONDS).until(isResourceIdDisplayed(R.id.incid_see_open_by_comu_ac));

        checkBack(onView(withId(R.id.incid_see_open_by_comu_ac)));
        checkDataEditMinFr(dbHelper, activity, incidImportanciaIntent);
    }

    //  ======================================== UNIT TESTS  =======================================

    @Test
    public void testOnCreate_2() throws Exception
    {
        IncidEditMinFr fragment = (IncidEditMinFr) activity.getSupportFragmentManager().findFragmentByTag(incid_edit_ac_frgs_tag);
        assertThat(fragment.viewerInjector, instanceOf(IncidEditAc.class));
        assertThat(fragment.viewer.getParentViewer(), CoreMatchers.<ViewerIf>is(activity.viewer));
    }

    @Test
    public void testOnSaveInstanceState()
    {
        activity.viewer = new ViewerIncidEditAc(activity) {
            @Override
            public void saveState(Bundle savedState)
            {
                assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
            }

            @Override
            public int clearSubscriptions()  // It is called from onStop() and gives problems.
            {
                return 0;
            }
        };
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                InstrumentationRegistry.getInstrumentation().callActivityOnSaveInstanceState(activity, new Bundle(0));
            }
        });
        waitAtMost(1, SECONDS).untilAtomic(flagMethodExec, is(AFTER_METHOD_EXEC_A));
    }

    @Test
    public void testOnStop()
    {
        AtomicInteger atomicInteger = new AtomicInteger(addSubscription(activity.viewer.getController()).size());
        InstrumentationRegistry.getInstrumentation().callActivityOnStop(activity);
        atomicInteger.compareAndSet(1, activity.viewer.getController().getSubscriptions().size());
        waitAtMost(2, SECONDS).untilAtomic(atomicInteger, is(0));
    }
}