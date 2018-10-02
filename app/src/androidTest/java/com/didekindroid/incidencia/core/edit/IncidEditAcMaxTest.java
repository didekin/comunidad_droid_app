package com.didekindroid.incidencia.core.edit;

import android.content.Intent;
import android.os.Build;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.incidencia.list.IncidSeeByComuAc;
import com.didekindroid.lib_one.api.ControllerIf;
import com.didekindroid.lib_one.api.ViewerIf;
import com.didekindroid.lib_one.incidencia.IncidenciaDataDbHelper;
import com.didekindroid.lib_one.incidencia.spinner.AmbitoIncidValueObj;
import com.didekindroid.usuariocomunidad.data.UserComuDataAc;
import com.didekinlib.model.incidencia.dominio.ImportanciaUser;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.app.TaskStackBuilder.create;
import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_CLOSED_LIST_FLAG;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_RESOLUCION_BUNDLE;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkDataEditMaxPowerFr;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkImportanciaUser;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkScreenEditMaxPowerFrErase;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.doAmbitoAndDescripcion;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.doImportanciaSpinner;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidSeeByComuAcLayout;
import static com.didekindroid.incidencia.testutils.IncidTestData.insertGetIncidImportancia;
import static com.didekindroid.lib_one.testutil.UiTestUtil.cleanTasks;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.testutil.ActivityTestUtil.checkBack;
import static com.didekindroid.testutil.ActivityTestUtil.checkSubscriptionsOnStop;
import static com.didekindroid.testutil.ActivityTestUtil.checkUp;
import static com.didekindroid.testutil.ActivityTestUtil.isResourceIdDisplayed;
import static com.didekindroid.testutil.ActivityTestUtil.isToastInView;
import static com.didekindroid.usuariocomunidad.UserComuBundleKey.USERCOMU_LIST_OBJECT;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.seeUserComuByUserFrRsId;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_REAL_JUAN;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 17/03/16
 * Time: 15:51
 */
@RunWith(AndroidJUnit4.class)
public class IncidEditAcMaxTest {

    private IncidAndResolBundle resolBundle;
    @Rule
    public IntentsTestRule<IncidEditAc> activityRule = new IntentsTestRule<IncidEditAc>(IncidEditAc.class) {
        @Override
        protected Intent getActivityIntent()
        {
            // Perfil adm, iniciador de la incidencia.  NO resolución.
            try {
                resolBundle = new IncidAndResolBundle(insertGetIncidImportancia(COMU_PLAZUELA5_JUAN), false);
            } catch (Exception e) {
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
    private IncidEditAc activity;
    private IncidenciaDataDbHelper dbHelper;

    public static void checkOnStop(IncidEditAc activity)
    {
        List<ViewerIf> viewers = activity.viewer.getChildViewersFromSuperClass(ViewerIf.class);
        viewers.add(activity.viewer);
        ControllerIf[] controllers = new ControllerIf[viewers.size()];
        for (int i = 0; i < controllers.length; ++i) {
            controllers[i] = viewers.get(i).getController();
        }
        checkSubscriptionsOnStop(activity, controllers);
    }

    @Before
    public void setUp() throws Exception
    {
        activity = activityRule.getActivity();
        dbHelper = new IncidenciaDataDbHelper(activity);
    }

    @After
    public void tearDown() throws Exception
    {
        dbHelper.close();
        cleanOptions(CLEAN_JUAN);
        if (Build.VERSION.SDK_INT >= LOLLIPOP) {
            cleanTasks(activity);
        }
    }

    /*  ======================================= INTEGRATION TESTS  =====================================*/

    @Test
    public void testOnCreate_UP()
    {
        checkScreenEditMaxPowerFrErase(activity.resolBundle);
        checkDataEditMaxPowerFr(dbHelper, activity, activity.resolBundle.getIncidImportancia());
        checkImportanciaUser(new ImportanciaUser(
                resolBundle.getIncidImportancia().getUserComu().getUsuario().getAlias(),
                resolBundle.getIncidImportancia().getImportancia()), activity);

        checkUp(incidSeeByComuAcLayout);
    }

    @Test
    public void testModifyIncidencia_1()
    {
        // Cason NOT OK: descripción de incidencia no válida.
        onView(withId(R.id.incid_reg_desc_ed)).perform(replaceText("descripcion = not valid"));
        onView(withId(R.id.incid_edit_fr_modif_button)).perform(click());
        waitAtMost(2, SECONDS).until(isToastInView(R.string.error_validation_msg, activity, R.string.incid_reg_descripcion));
    }

    @Test
    public void testModifyIncidencia_2()
    {
        // Caso OK. Modificamos: importancia, ámbito y descripción. Hacemos UP.
        // Changes.
        short newImportancia = (short) 2;
        String newDesc = "descripcion es valida";
        doImportanciaSpinner(activity, newImportancia);
        AmbitoIncidValueObj ambitoObj = new AmbitoIncidValueObj((short) 10, "Calefacción comunitaria");
        doAmbitoAndDescripcion(ambitoObj, newDesc);
        // Exec.
        onView(withId(R.id.incid_edit_fr_modif_button)).perform(click());
        // Check.
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(incidSeeByComuAcLayout));
    }

    @Test
    public void testModifyIncidenciaPressBack()
    {
        // Caso OK. No cambiamos nada. Hacemos BACK.
        onView(withId(R.id.incid_edit_fr_modif_button)).perform(click());

        waitAtMost(6, SECONDS).until(isResourceIdDisplayed(incidSeeByComuAcLayout));
        checkBack(onView(withId(incidSeeByComuAcLayout)));
        checkScreenEditMaxPowerFrErase(activity.resolBundle);
    }

    @Test
    public void testDeleteIncidencia()
    {
        // CASO OK: borramos la incidencia.
        onView(withId(R.id.incid_edit_fr_borrar_button)).perform(click());
        waitAtMost(6, SECONDS).until(isResourceIdDisplayed(incidSeeByComuAcLayout));
    }

    @Test
    public void testDeleteAndPressBack()
    {
        //CASO OK: borramos incidencia y BACK.
        onView(withId(R.id.incid_edit_fr_borrar_button)).perform(click());
        // Check borrado.
        waitAtMost(6, SECONDS).until(isResourceIdDisplayed(incidSeeByComuAcLayout));
        if (Build.VERSION.SDK_INT >= LOLLIPOP) {
            // BACK: como ya no existe la incidencia, salta a la lista de incidencias nuevamente.
            checkBack(onView(withId(incidSeeByComuAcLayout)), incidSeeByComuAcLayout);
        }
    }

    @Test
    public void testDeleteAndPressUP()
    {
        //CASO OK: borramos incidencia y UP.
        onView(withId(R.id.incid_edit_fr_borrar_button)).perform(click());
        // Check borrado.
        waitAtMost(6, SECONDS).until(isResourceIdDisplayed(incidSeeByComuAcLayout));
        if (Build.VERSION.SDK_INT >= LOLLIPOP) {
            checkUp(seeUserComuByUserFrRsId);
        }
    }

    //  ======================================== UNIT TESTS  =======================================

    @Test
    public void testOnCreate_2()
    {
        IncidEditMaxFr fragment = (IncidEditMaxFr) activity.getSupportFragmentManager().findFragmentByTag(IncidEditMaxFr.class.getName());
        assertThat(fragment.viewerInjector, instanceOf(IncidEditAc.class));
        assertThat(fragment.viewer.getParentViewer(), is(activity.viewer));
    }

    @Test
    public void testOnStop()
    {
        checkOnStop(activity);
    }
}