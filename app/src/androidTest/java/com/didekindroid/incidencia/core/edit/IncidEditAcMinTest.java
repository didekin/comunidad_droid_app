package com.didekindroid.incidencia.core.edit;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.exception.UiException;
import com.didekindroid.incidencia.core.IncidenciaDataDbHelper;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.incidencia.IncidDaoRemote.incidenciaDao;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetIncidImportancia;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkDataEditMinFr;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.doImportanciaSpinner;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_BUNDLE;
import static com.didekindroid.incidencia.utils.IncidFragmentTags.incid_edit_ac_frgs_tag;
import static com.didekindroid.security.SecurityTestUtils.updateSecurityData;
import static com.didekindroid.testutil.ActivityTestUtils.checkBack;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.testutil.ActivityTestUtils.isResourceIdDisplayed;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN_AND_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.repository.UserComuDaoRemote.userComuDaoRemote;
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
public class IncidEditAcMinTest extends IncidEditAcTest {

    IncidImportancia incidImportanciaIntent;

    @Rule
    public IntentsTestRule<IncidEditAc> activityRule = new IntentsTestRule<IncidEditAc>(IncidEditAc.class) {
        @Override
        protected Intent getActivityIntent()
        {
            IncidAndResolBundle resolBundle = null;
            try {
                IncidImportancia incidImportancia_0 = insertGetIncidImportancia(COMU_REAL_PEPE);
                // Registro userComu en misma comunidad.
                UsuarioComunidad userComuJuan = makeUsuarioComunidad(incidImportancia_0.getIncidencia().getComunidad(), USER_JUAN,
                        "portal", "esc", "plantaX", "door12", PROPIETARIO.function);
                userComuDaoRemote.regUserAndUserComu(userComuJuan).execute();
                updateSecurityData(USER_JUAN.getUserName(), USER_JUAN.getPassword());
                incidImportanciaIntent = incidenciaDao.seeIncidImportancia(incidImportancia_0.getIncidencia().getIncidenciaId()).getIncidImportancia();
                resolBundle = new IncidAndResolBundle(incidImportanciaIntent, false);
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
        // Usuario sin registro previo de resolBundle.
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
}