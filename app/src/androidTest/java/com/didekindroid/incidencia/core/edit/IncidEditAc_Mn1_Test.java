package com.didekindroid.incidencia.core.edit;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekindroid.incidencia.core.IncidenciaDataDbHelper;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetIncidImportancia;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetResolucionNoAdvances;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkDataEditMaxPowerFr;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkScreenEditMaxPowerFrNotErase;
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_COMMENTS_SEE_AC;
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_COMMENT_REG_AC;
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_RESOLUCION_REG_EDIT_AC;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCIDENCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_BUNDLE;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.testutil.ActivityTestUtils.checkAppBarMenu;
import static com.didekindroid.testutil.ActivityTestUtils.checkAppBarMenuOnView;
import static com.didekindroid.testutil.ActivityTestUtils.checkAppBarMnNotExist;
import static com.didekindroid.testutil.ActivityTestUtils.checkBack;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.testutil.ActivityTestUtils.isResourceIdDisplayed;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_TRAV_PLAZUELA_PEPE;
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
public class IncidEditAc_Mn1_Test {

    IncidEditAc activity;
    IncidAndResolBundle resolBundle;
    Resolucion resolucion;
    @Rule
    public IntentsTestRule<IncidEditAc> activityRule = new IntentsTestRule<IncidEditAc>(IncidEditAc.class) {
        @Override
        protected Intent getActivityIntent()
        {
            try {
                /* Perfil adm, inicidador de la incidencia.*/
                final IncidImportancia incidImportancia = insertGetIncidImportancia(COMU_ESCORIAL_PEPE);
                resolucion = insertGetResolucionNoAdvances(incidImportancia);
                resolBundle = new IncidAndResolBundle(incidImportancia, true);
            } catch (IOException | UiException e) {
                fail();
            }
            Intent intent = new Intent();
            intent.putExtra(INCID_RESOLUCION_BUNDLE.key, resolBundle);
            return intent;
        }
    };
    IncidenciaDataDbHelper dbHelper;

    @Before
    public void setUp() throws Exception
    {
        activity = activityRule.getActivity();
        dbHelper = new IncidenciaDataDbHelper(activity);
        checkDataEditMaxPowerFr(dbHelper, activity, activity.resolBundle.getIncidImportancia());
        checkScreenEditMaxPowerFrNotErase(activity.resolBundle);
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
        INCID_COMMENT_REG_AC.checkMenuItem(activity);
        intended(hasExtra(INCIDENCIA_OBJECT.key, activity.resolBundle.getIncidImportancia().getIncidencia()));
        checkUp();
        checkDataEditMaxPowerFr(dbHelper, activity, activity.resolBundle.getIncidImportancia());
    }

    @Test
    public void testIncidCommentsSee_Mn() throws Exception
    {
        INCID_COMMENTS_SEE_AC.checkMenuItem(activity);
        intended(hasExtra(INCIDENCIA_OBJECT.key, activity.resolBundle.getIncidImportancia().getIncidencia()));
        checkUp();
        checkDataEditMaxPowerFr(dbHelper, activity, activity.resolBundle.getIncidImportancia());
    }

    @Test
    public void testIncidResolucionReg_Mn_1() throws Exception
    {
        // Preconditions: usuario ADM, with resolucion.
        assertThat(activity.resolBundle.hasResolucion(), is(true));
        assertThat(activity.resolBundle.getIncidImportancia().getUserComu().hasAdministradorAuthority(), is(true));

        INCID_RESOLUCION_REG_EDIT_AC.checkMenuItem(activity);
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(R.id.incid_resolucion_edit_fr_layout));

        // Extra con oldIncidImportancia.
        intended(hasExtra(INCID_IMPORTANCIA_OBJECT.key, activity.resolBundle.getIncidImportancia()));
        // Hay resolución en BD --> extra con resolución.
        intended(hasExtra(INCID_RESOLUCION_OBJECT.key, resolucion));

        checkBack(onView(withId(R.id.incid_resolucion_edit_fr_layout)));
        checkScreenEditMaxPowerFrNotErase(activity.resolBundle);
    }

    @Test
    public void testIncidResolucionReg_Mn_2() throws Exception  // TODO: fail.
    {
        // Preconditions: usuario ADM, without resolucion.
        IncidAndResolBundle newResolBundle = new IncidAndResolBundle(activity.resolBundle.getIncidImportancia(), false);
        assertThat(newResolBundle.hasResolucion(), is(false));
        assertThat(newResolBundle.getIncidImportancia().getUserComu().hasAdministradorAuthority(), is(true));
        IncidEditAc newAc = doIntentStartActivity(newResolBundle);
        // Check.
        checkAppBarMenuOnView(newAc, R.string.incid_resolucion_ac_mn, R.id.incid_resolucion_reg_frg_layout);
    }

    @Test
    public void testIncidResolucionReg_Mn_3() throws Exception
    {
        cleanOptions(CLEAN_PEPE);
        // Preconditions: usuario NO ADM, without resolucion.
        IncidAndResolBundle newResolBundle = new IncidAndResolBundle(insertGetIncidImportancia(COMU_TRAV_PLAZUELA_PEPE), false);
        assertThat(newResolBundle.hasResolucion(), is(false));
        assertThat(newResolBundle.getIncidImportancia().getUserComu().hasAdministradorAuthority(), is(false));
        IncidEditAc newAc = doIntentStartActivity(newResolBundle);
        // Check.
        checkAppBarMnNotExist(newAc, R.string.incid_resolucion_ac_mn);
    }

    @Test
    public void testIncidResolucionReg_Mn_4() throws Exception    // TODO: fail.
    {
        cleanOptions(CLEAN_PEPE);
        // Preconditions: usuario NO ADM, with resolucion.
        IncidAndResolBundle newResolBundle = new IncidAndResolBundle(insertGetIncidImportancia(COMU_TRAV_PLAZUELA_PEPE), true);
        assertThat(activity.viewer.getController().isRegisteredUser(), is(true));
        assertThat(newResolBundle.hasResolucion(), is(true));
        assertThat(newResolBundle.getIncidImportancia().getUserComu().hasAdministradorAuthority(), is(false));
        IncidEditAc newAc = doIntentStartActivity(newResolBundle);
        // Check.
        checkAppBarMenu(newAc, R.string.incid_resolucion_ac_mn, R.id.incid_resolucion_see_fr_layout);
    }

    //    ============================  HELPER  ===================================

    private IncidEditAc doIntentStartActivity(IncidAndResolBundle newResolBundle)
    {
        Intent intent = new Intent(getTargetContext(), IncidEditAc.class);
        intent.putExtra(INCID_RESOLUCION_BUNDLE.key, newResolBundle);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        activity.finish();
        // Run
        return (IncidEditAc) getInstrumentation().startActivitySync(intent);
    }
}