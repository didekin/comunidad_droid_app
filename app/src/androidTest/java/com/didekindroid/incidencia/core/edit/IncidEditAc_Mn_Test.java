package com.didekindroid.incidencia.core.edit;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekindroid.incidencia.core.IncidenciaDataDbHelper;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.incidencia.IncidDaoRemote.incidenciaDao;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetIncidImportancia;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetResolucionNoAdvances;
import static com.didekindroid.incidencia.testutils.IncidUiTestUtils.checkDataEditMaxPowerFr;
import static com.didekindroid.incidencia.testutils.IncidUiTestUtils.checkScreenEditMaxPowerFr;
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_COMMENTS_SEE_AC;
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_COMMENT_REG_AC;
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_RESOLUCION_REG_EDIT_AC;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCIDENCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_FLAG;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.testutil.ActivityTestUtils.checkBack;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.testutil.ActivityTestUtils.isResourceIdDisplayed;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 19/01/16
 * Time: 16:57
 */
@RunWith(AndroidJUnit4.class)
public class IncidEditAc_Mn_Test {

    IncidEditAc activity;
    IncidImportancia incidImportancia;
    Resolucion resolucion;
    boolean flagResolucion;
    @Rule
    public IntentsTestRule<IncidEditAc> activityRule = new IntentsTestRule<IncidEditAc>(IncidEditAc.class) {
        @Override
        protected Intent getActivityIntent()
        {
            try {
                // Perfil adm, inicidador de la incidencia.
                incidImportancia = insertGetIncidImportancia(COMU_ESCORIAL_PEPE);
                resolucion = insertGetResolucionNoAdvances(incidImportancia);
                flagResolucion = incidenciaDao.seeIncidImportancia(incidImportancia.getIncidencia().getIncidenciaId()).hasResolucion();
            } catch (IOException | UiException e) {
                fail();
            }
            Intent intent = new Intent();
            intent.putExtra(INCID_IMPORTANCIA_OBJECT.key, incidImportancia);
            intent.putExtra(INCID_RESOLUCION_FLAG.key, flagResolucion);
            return intent;
        }
    };
    IncidenciaDataDbHelper dbHelper;

    @Before
    public void setUp() throws Exception
    {
        activity = activityRule.getActivity();
        dbHelper = new IncidenciaDataDbHelper(activity);
        assertThat(flagResolucion, is(true));
        assertThat(incidImportancia.getUserComu().hasAdministradorAuthority(), is(true));
        checkDataEditMaxPowerFr(dbHelper, activity, incidImportancia); // Es usuario adm.
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
        INCID_COMMENT_REG_AC.checkMenuItem_WTk(activity);
        intended(hasExtra(INCIDENCIA_OBJECT.key, incidImportancia.getIncidencia()));
        checkUp();
        checkDataEditMaxPowerFr(dbHelper, activity, incidImportancia);
    }

    @Test
    public void testIncidCommentsSee_Mn() throws Exception
    {
        INCID_COMMENTS_SEE_AC.checkMenuItem_WTk(activity);
        intended(hasExtra(INCIDENCIA_OBJECT.key, incidImportancia.getIncidencia()));
        checkUp();
        checkDataEditMaxPowerFr(dbHelper, activity, incidImportancia);
    }

    @Test
    public void testIncidResolucionReg_Mn() throws Exception
    {
        INCID_RESOLUCION_REG_EDIT_AC.checkMenuItem_WTk(activity);
        waitAtMost(2, SECONDS).until(isResourceIdDisplayed(R.id.incid_resolucion_edit_fr_layout));

        // Extra con oldIncidImportancia.
        intended(hasExtra(INCID_IMPORTANCIA_OBJECT.key, incidImportancia));
        // Hay resolución en BD --> extra con resolución.
        intended(hasExtra(INCID_RESOLUCION_OBJECT.key, resolucion));

        checkBack(onView(withId(R.id.incid_resolucion_edit_fr_layout)));
        checkScreenEditMaxPowerFr(incidImportancia, flagResolucion);
    }
}