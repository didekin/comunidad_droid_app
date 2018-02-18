package com.didekindroid.incidencia.core.edit;

import android.content.Intent;
import android.os.Build;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.incidencia.list.IncidSeeByComuAc;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.incidencia.IncidenciaDataDbHelper;
import com.didekindroid.usuariocomunidad.data.UserComuDataAc;
import com.didekinlib.model.incidencia.dominio.ImportanciaUser;
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
import java.util.concurrent.atomic.AtomicBoolean;

import static android.app.TaskStackBuilder.create;
import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.incidencia.IncidenciaDao.incidenciaDao;
import static com.didekindroid.incidencia.core.edit.IncidEditAcMaxTest.checkOnStop;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetIncidImportancia;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkDataEditMinFr;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkImportanciaUser;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.doImportanciaSpinner;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidSeeByComuAcLayout;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_CLOSED_LIST_FLAG;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_RESOLUCION_BUNDLE;
import static com.didekindroid.lib_one.security.SecurityTestUtils.updateSecurityData;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_JUAN_AND_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_JUAN;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.testutil.ActivityTestUtil.checkBack;
import static com.didekindroid.testutil.ActivityTestUtil.checkUp;
import static com.didekindroid.testutil.ActivityTestUtil.cleanTasks;
import static com.didekindroid.testutil.ActivityTestUtil.isResourceIdDisplayed;
import static com.didekindroid.usuariocomunidad.UserComuMockDao.userComuMockDao;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_REAL_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_REAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.makeUsuarioComunidad;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.seeUserComuByUserFrRsId;
import static com.didekindroid.usuariocomunidad.UserComuBundleKey.USERCOMU_LIST_OBJECT;
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
 * Usuario inicial en sesión SIN permisos para modificar o borrar una incidencia.
 */
@RunWith(AndroidJUnit4.class)
public class IncidEditAcMinTest {

    IncidImportancia incidImportancia_0;
    IncidAndResolBundle resolBundle;
    @Rule
    public IntentsTestRule<IncidEditAc> activityRule = new IntentsTestRule<IncidEditAc>(IncidEditAc.class) {
        @Override
        protected Intent getActivityIntent()
        {
            try {
                incidImportancia_0 = insertGetIncidImportancia(COMU_REAL_PEPE);
                // Registro userComu en misma comunidad.
                UsuarioComunidad userComuJuan = makeUsuarioComunidad(incidImportancia_0.getIncidencia().getComunidad(), USER_JUAN,
                        "portal", "esc", "plantaX", "door12", PROPIETARIO.function);
                userComuMockDao.regUserAndUserComu(userComuJuan).execute();
                updateSecurityData(USER_JUAN.getUserName(), USER_JUAN.getPassword());
                resolBundle = incidenciaDao.seeIncidImportancia(incidImportancia_0.getIncidencia().getIncidenciaId());
            } catch (IOException | UiException e) {
                fail();
            }

            if (Build.VERSION.SDK_INT >= LOLLIPOP) {
                Intent intent0 = new Intent(getTargetContext(), UserComuDataAc.class)
                        .putExtra(USERCOMU_LIST_OBJECT.key,
                                new UsuarioComunidad.UserComuBuilder(
                                        resolBundle.getIncidImportancia().getUserComu().getComunidad(),
                                        resolBundle.getIncidImportancia().getUserComu().getUsuario()
                                ).userComuRest(COMU_REAL_JUAN).build()
                        );
                Intent intent1 = new Intent(getTargetContext(), IncidSeeByComuAc.class).putExtra(INCID_CLOSED_LIST_FLAG.key, false);
                create(getTargetContext()).addNextIntent(intent0).addNextIntentWithParentStack(intent1).startActivities();
            }
            return new Intent().putExtra(INCID_RESOLUCION_BUNDLE.key, resolBundle);
        }
    };
    IncidEditAc activity;
    IncidenciaDataDbHelper dbHelper;

    @Before
    public void setUp() throws Exception
    {
        activity = activityRule.getActivity();
        dbHelper = new IncidenciaDataDbHelper(activity);
        // Preconditions.
        assertThat(resolBundle.getIncidImportancia().getImportancia(), is((short) 0));
        assertThat(resolBundle.getIncidImportancia().getUserComu().hasAdministradorAuthority(), is(false));
    }

    @After
    public void tearDown() throws Exception
    {
        dbHelper.close();
        cleanOptions(CLEAN_JUAN_AND_PEPE);
        if (Build.VERSION.SDK_INT >= LOLLIPOP) {
            cleanTasks(activity);
        }
    }

//  ======================================= INTEGRATION TESTS  =====================================

    @Test
    public void testOnCreate_1() throws Exception
    {
        checkDataEditMinFr(dbHelper, activity, resolBundle.getIncidImportancia());
        AtomicBoolean isChecked = new AtomicBoolean(false);
        isChecked.compareAndSet(false,
                checkImportanciaUser(new ImportanciaUser(USER_PEPE.getAlias(), incidImportancia_0.getImportancia()), activity));
        waitAtMost(4, SECONDS).untilTrue(isChecked);

        checkUp(incidSeeByComuAcLayout);
    }

    @Test
    public void testModifyIncidImportanciaPressUp()
    {
        // Modificamos importancia y UP.
        short newImportancia = 1;
        doImportanciaSpinner(activity, newImportancia);
        // Modify.
        onView(withId(R.id.incid_edit_fr_modif_button)).perform(click());
        // Check.
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(incidSeeByComuAcLayout));

        if (Build.VERSION.SDK_INT >= LOLLIPOP) {
            checkUp(seeUserComuByUserFrRsId);
        }
    }

    @Test
    public void testModifyIncidImportanciaPressBack() throws InterruptedException
    {
        // Modify.
        onView(withId(R.id.incid_edit_fr_modif_button)).perform(click());
        // Check.
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(incidSeeByComuAcLayout));

        if (Build.VERSION.SDK_INT >= LOLLIPOP) {
            checkBack(onView(withId(incidSeeByComuAcLayout)));
            checkDataEditMinFr(dbHelper, activity, resolBundle.getIncidImportancia());
        }
    }

    //  ======================================== UNIT TESTS  =======================================

    @Test
    public void testOnCreate_2() throws Exception
    {
        IncidEditMinFr fragment = (IncidEditMinFr) activity.getSupportFragmentManager().findFragmentByTag(IncidEditMinFr.class.getName());
        assertThat(fragment.viewerInjector, instanceOf(IncidEditAc.class));
        assertThat(fragment.viewer.getParentViewer(), CoreMatchers.is(activity.viewer));
    }

    @Test
    public void testOnStop()
    {
        checkOnStop(activity);
    }
}