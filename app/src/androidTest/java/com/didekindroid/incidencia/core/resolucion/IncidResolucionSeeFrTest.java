package com.didekindroid.incidencia.core.resolucion;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.incidencia.list.IncidSeeByComuAc;
import com.didekinlib.model.incidencia.dominio.Avance;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Resolucion;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.TaskStackBuilder.create;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.incidencia.IncidenciaDao.incidenciaDao;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetIncidImportancia;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetResolucionNoAdvances;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkDataResolucionSeeFr;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkScreenResolucionSeeFr;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidSeeByComuAcLayout;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_CLOSED_LIST_FLAG;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.security.SecurityTestUtils.updateSecurityData;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.testutil.ActivityTestUtils.cleanTasks;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN_AND_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.makeUserComuWithComunidadId;
import static com.didekindroid.usuariocomunidad.testutil.UserComuMockDaoRemote.userComuMockDao;
import static com.didekindroid.lib_one.util.UIutils.formatTimeStampToString;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 14/03/16
 * Time: 17:49
 * <p>
 * Preconditions:
 * 1. A user WITHOUT powers to edit a resolucion is received.
 * 2. A resolucion in BD and intent.
 */
@RunWith(AndroidJUnit4.class)
public class IncidResolucionSeeFrTest {

    IncidImportancia incidImportancia;
    Resolucion resolucion;
    UsuarioComunidad userJuan;
    Activity activity;

    static Resolucion doResolucionAvances(IncidImportancia incidImportancia) throws InterruptedException, UiException
    {
        // Registramos resolución.
        Thread.sleep(1000);
        Resolucion resolucion = insertGetResolucionNoAdvances(incidImportancia);
        // Modificamos con avances.
        Avance avance = new Avance.AvanceBuilder().avanceDesc("avance1_desc").build();
        List<Avance> avances = new ArrayList<>(1);
        avances.add(avance);
        resolucion = new Resolucion.ResolucionBuilder(incidImportancia.getIncidencia())
                .copyResolucion(resolucion)
                .avances(avances)
                .build();
        assertThat(incidenciaDao.modifyResolucion(resolucion), is(2));
        resolucion = incidenciaDao.seeResolucion(resolucion.getIncidencia().getIncidenciaId());
        return resolucion;
    }

    @Before
    public void setUp()
    {
        try {
            incidImportancia = insertGetIncidImportancia(COMU_ESCORIAL_PEPE);
            assertThat(incidImportancia.getUserComu().hasAdministradorAuthority(), is(true));
        } catch (IOException | UiException e) {
            fail();
        }

        if (Build.VERSION.SDK_INT >= LOLLIPOP) {
            Intent intent1 = new Intent(getTargetContext(), IncidSeeByComuAc.class).putExtra(INCID_CLOSED_LIST_FLAG.key, false);
            create(getTargetContext()).addNextIntentWithParentStack(intent1).startActivities();
        }
    }

    @After
    public void tearDown() throws Exception
    {
        if (Build.VERSION.SDK_INT >= LOLLIPOP) {
            cleanTasks(activity);
        }
        cleanOptions(CLEAN_JUAN_AND_PEPE);
    }

    /*    ============================  TESTS  ===================================*/

    @Test
    public void testOnCreate_1() throws Exception
    {
        // Precondition: resolucion with avances; usuario NO adm.
        Intent intent = doPreconditionsWithAvances();
        updateSecurityData(USER_JUAN.getUserName(), USER_JUAN.getPassword());
        activity = getInstrumentation().startActivitySync(intent);
        // Check.
        checkScreenResolucionSeeFr(resolucion);
        checkDataResolucionSeeFr(resolucion);
        // Avances.
        Avance avance = resolucion.getAvances().get(0);
        onData(is(avance)).inAdapterView(withId(android.R.id.list)).check(matches(isDisplayed()));
        onView(allOf(
                withText("avance1_desc"),
                withId(R.id.incid_avance_desc_view)
        )).check(matches(isDisplayed()));
        onView(allOf(
                withText(formatTimeStampToString(avance.getFechaAlta())),
                withId(R.id.incid_avance_fecha_view),
                hasSibling(allOf(
                        withId(R.id.incid_avance_aliasUser_view),
                        withText(USER_PEPE.getAlias()) // usuario en sesión que modifica resolución.
                )))).check(matches(isDisplayed()));

        if (Build.VERSION.SDK_INT >= LOLLIPOP) {
            checkUp(incidSeeByComuAcLayout);
        }
    }

    @Test
    public void testOnCreate_2() throws Exception
    {
        // Precondition: Resolucion without avances; usuario NO adm.
        Intent intent = doPreconditions();
        updateSecurityData(USER_JUAN.getUserName(), USER_JUAN.getPassword());
        activity = getInstrumentation().startActivitySync(intent);
        // Check.
        checkScreenResolucionSeeFr(resolucion);
        checkDataResolucionSeeFr(resolucion);
        // Avances.
        onView(allOf(
                withId(android.R.id.empty),
                withText(R.string.incid_resolucion_no_avances_message)
        )).check(matches(isDisplayed()));

        if (Build.VERSION.SDK_INT >= LOLLIPOP) {
            checkUp(incidSeeByComuAcLayout);
        }
    }

    /*    ============================  Helpers  ===================================*/

    Intent doPreconditionsWithAvances() throws InterruptedException
    {
        try {
            doUser();
            resolucion = doResolucionAvances(incidImportancia);

        } catch (UiException e) {
            fail();
        }
        return doIntent(resolucion);
    }

    Intent doPreconditions() throws UiException, InterruptedException
    {
        doUser();
        // Registramos resolución.
        Thread.sleep(1000);
        return doIntent(insertGetResolucionNoAdvances(incidImportancia));
    }

    private void doUser()
    {
        try {
            // Usuario del test.
            userJuan = makeUserComuWithComunidadId(COMU_ESCORIAL_JUAN, incidImportancia.getIncidencia().getComunidadId());
            assertThat(userComuMockDao.regUserAndUserComu(userJuan).execute().body(), is(true));
        } catch (IOException e) {
            fail();
        }
    }

    @NonNull
    private Intent doIntent(Resolucion resolucionIn)
    {
        resolucion = resolucionIn;
        Intent intent = new Intent(getTargetContext(), IncidResolucionEditAc.class).setFlags(FLAG_ACTIVITY_NEW_TASK);
        IncidImportancia noAdmIncidImport = new IncidImportancia.IncidImportanciaBuilder(incidImportancia.getIncidencia())
                .copyIncidImportancia(incidImportancia)
                .usuarioComunidad(userJuan)
                .build();
        assertThat(noAdmIncidImport.getUserComu().hasAdministradorAuthority(), is(false));
        intent.putExtra(INCID_IMPORTANCIA_OBJECT.key, noAdmIncidImport);
        intent.putExtra(INCID_RESOLUCION_OBJECT.key, resolucion);
        return intent;
    }
}
