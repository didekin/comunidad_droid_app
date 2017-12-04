package com.didekindroid.incidencia.core.edit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekindroid.incidencia.core.IncidenciaDataDbHelper;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetIncidImportancia;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetResolucionNoAdvances;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkDataEditMaxPowerFr;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkDataEditMinFr;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkScreenEditMaxPowerFrErase;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkScreenEditMaxPowerFrNotErase;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkScreenEditMinFr;
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_COMMENTS_SEE_AC;
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_COMMENT_REG_AC;
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_RESOLUCION_REG_EDIT_AC;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_BUNDLE;
import static com.didekindroid.security.SecurityTestUtils.updateSecurityData;
import static com.didekindroid.testutil.ActivityTestUtils.checkAppBarMnNotExist;
import static com.didekindroid.testutil.ActivityTestUtils.checkBack;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.testutil.ActivityTestUtils.isResourceIdDisplayed;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_TK_HANDLER;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_TRAV_PLAZUELA_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.makeUserComuWithComunidadId;
import static com.didekindroid.usuariocomunidad.testutil.UserComuMockDaoRemote.userComuMockDao;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 19/01/16
 * Time: 16:57
 */
@RunWith(AndroidJUnit4.class)
public class IncidEditAc_Mn1_Test {

    IncidEditAc activity;
    IncidenciaDataDbHelper dbHelper;

    @Before
    public void setUp() throws Exception
    {
    }

    @After
    public void tearDown() throws Exception
    {
        dbHelper.close();
        cleanOptions(CLEAN_PEPE);
    }

//    ============================  TESTS  ===================================

    @Test
    public void testIncidCommentReg_Mn() throws Exception
    {
        activity = doIntentStartActivity(initDbData(COMU_ESCORIAL_PEPE, true));
        dbHelper = new IncidenciaDataDbHelper(activity);
        checkDataEditMaxPowerFr(dbHelper, activity, activity.resolBundle.getIncidImportancia());
        checkScreenEditMaxPowerFrNotErase(activity.resolBundle);

        INCID_COMMENT_REG_AC.checkMenuItem(activity);
        checkUp();
        checkDataEditMaxPowerFr(dbHelper, activity, activity.resolBundle.getIncidImportancia());
    }

    @Test
    public void testIncidCommentsSee_Mn() throws Exception
    {
        activity = doIntentStartActivity(initDbData(COMU_ESCORIAL_PEPE, true));
        dbHelper = new IncidenciaDataDbHelper(activity);
        checkDataEditMaxPowerFr(dbHelper, activity, activity.resolBundle.getIncidImportancia());
        checkScreenEditMaxPowerFrNotErase(activity.resolBundle);

        INCID_COMMENTS_SEE_AC.checkMenuItem(activity);
        checkUp();
        checkDataEditMaxPowerFr(dbHelper, activity, activity.resolBundle.getIncidImportancia());
    }

    @Test
    public void testIncidResolucionReg_Mn_1() throws Exception
    {
        activity = doIntentStartActivity(initDbData(COMU_ESCORIAL_PEPE, true));
        dbHelper = new IncidenciaDataDbHelper(activity);
        checkDataEditMaxPowerFr(dbHelper, activity, activity.resolBundle.getIncidImportancia());
        checkScreenEditMaxPowerFrNotErase(activity.resolBundle);

        // Preconditions: usuario ADM, with resolucion.
        assertThat(activity.resolBundle.hasResolucion(), is(true));
        assertThat(activity.resolBundle.getIncidImportancia().getUserComu().hasAdministradorAuthority(), is(true));

        INCID_RESOLUCION_REG_EDIT_AC.checkMenuItem(activity);
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(R.id.incid_resolucion_edit_fr_layout));

        checkBack(onView(withId(R.id.incid_resolucion_edit_fr_layout)));
        checkScreenEditMaxPowerFrNotErase(activity.resolBundle);
    }

    @Test
    public void testIncidResolucionReg_Mn_2() throws Exception
    {
        activity = doIntentStartActivity(initDbData(COMU_ESCORIAL_PEPE, false));
        dbHelper = new IncidenciaDataDbHelper(activity);
        checkDataEditMaxPowerFr(dbHelper, activity, activity.resolBundle.getIncidImportancia());
        checkScreenEditMaxPowerFrErase(activity.resolBundle);  // No hay resolución. La incidencia se puede borrar.

        // Preconditions: usuario ADM, without resolucion.
        assertThat(activity.resolBundle.hasResolucion(), is(false));
        assertThat(activity.resolBundle.getIncidImportancia().getUserComu().hasAdministradorAuthority(), is(true));

        INCID_RESOLUCION_REG_EDIT_AC.checkMenuItem(activity);
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(R.id.incid_resolucion_reg_frg_layout));

        checkBack(onView(withId(R.id.incid_resolucion_reg_frg_layout)));
        checkScreenEditMaxPowerFrErase(activity.resolBundle);
    }

    @Test
    public void testIncidResolucionReg_Mn_3() throws Exception
    {
        activity = doIntentStartActivity(initDbData(COMU_TRAV_PLAZUELA_PEPE, false));
        dbHelper = new IncidenciaDataDbHelper(activity);
        checkDataEditMaxPowerFr(dbHelper, activity, activity.resolBundle.getIncidImportancia());
        checkScreenEditMaxPowerFrErase(activity.resolBundle);  // No hay resolución. La incidencia se puede borrar.

        // Preconditions: usuario NO ADM, without resolucion.
        assertThat(activity.resolBundle.hasResolucion(), is(false));
        assertThat(activity.resolBundle.getIncidImportancia().getUserComu().hasAdministradorAuthority(), is(false));
        // Check.
        checkAppBarMnNotExist(activity, R.string.incid_resolucion_ac_mn);
    }

    @Test
    public void testIncidResolucionReg_Mn_4() throws Exception
    {
        // Damos de alta resolución con usuario ADM.
        final IncidImportancia incidImportancia = insertGetIncidImportancia(COMU_ESCORIAL_PEPE);
        insertGetResolucionNoAdvances(incidImportancia);
        cleanOptions(CLEAN_TK_HANDLER);
        // Damos de alta usuario no ADM en misma comunidad.
        UsuarioComunidad juanEscorial = makeUserComuWithComunidadId(COMU_ESCORIAL_JUAN, incidImportancia.getIncidencia().getComunidadId());
        userComuMockDao.regUserAndUserComu(juanEscorial).execute().body();
        updateSecurityData(juanEscorial.getUsuario().getUserName(), juanEscorial.getUsuario().getPassword());
        // Construimos resolBundle con nuevo usuario para la misma incidencia.
        IncidImportancia noAdmIncidImportancia = new IncidImportancia.IncidImportanciaBuilder(incidImportancia.getIncidencia())
                .usuarioComunidad(juanEscorial)
                .importancia((short) 2)
                .build();
        // Activity con bundle.
        activity = doIntentStartActivity(new IncidAndResolBundle(noAdmIncidImportancia, true));
        dbHelper = new IncidenciaDataDbHelper(activity);
        checkDataEditMinFr(dbHelper, activity, activity.resolBundle.getIncidImportancia());
        checkScreenEditMinFr();

        // Preconditions: usuario NO ADM, with resolucion.
        assertThat(activity.resolBundle.hasResolucion(), is(true));
        assertThat(activity.resolBundle.getIncidImportancia().getUserComu().hasAdministradorAuthority(), is(false));
        // Run
        INCID_RESOLUCION_REG_EDIT_AC.checkMenuItem(activity);
        // Check
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(R.id.incid_resolucion_see_fr_layout));

        checkBack(onView(withId(R.id.incid_resolucion_see_fr_layout)));
        checkScreenEditMinFr();

        cleanOptions(CLEAN_JUAN);
    }

    //    ============================  HELPER  ===================================

    @NonNull
    IncidAndResolBundle initDbData(UsuarioComunidad usuarioComunidad, boolean hasResolucion) throws IOException, UiException
    {
        /* Perfil adm, inicidador de la incidencia.*/
        final IncidImportancia incidImportancia = insertGetIncidImportancia(usuarioComunidad);
        if (hasResolucion) {
            insertGetResolucionNoAdvances(incidImportancia);
            return new IncidAndResolBundle(incidImportancia, true);
        } else {
            return new IncidAndResolBundle(incidImportancia, false);
        }
    }

    private IncidEditAc doIntentStartActivity(IncidAndResolBundle newResolBundle)
    {
        Intent intent = new Intent(getTargetContext(), IncidEditAc.class);
        intent.putExtra(INCID_RESOLUCION_BUNDLE.key, newResolBundle);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        // Run
        return (IncidEditAc) getInstrumentation().startActivitySync(intent);
    }
}