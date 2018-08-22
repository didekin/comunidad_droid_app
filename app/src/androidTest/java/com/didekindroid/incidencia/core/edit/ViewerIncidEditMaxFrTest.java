package com.didekindroid.incidencia.core.edit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.incidencia.core.CtrlerIncidenciaCore;
import com.didekindroid.lib_one.incidencia.IncidenciaDataDbHelper;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Incidencia;
import com.didekinlib.model.usuario.Usuario;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_IMPORTANCIA_NUMBER;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_RESOLUCION_BUNDLE;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkDataEditMaxPowerFr;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkScreenEditMaxPowerFrErase;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkScreenEditMaxPowerFrNotErase;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.isComuSpinnerWithText;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidSeeByComuAcLayout;
import static com.didekindroid.incidencia.testutils.IncidTestData.insertGetIncidImportancia;
import static com.didekindroid.lib_one.incidencia.spinner.IncidenciaSpinnerKey.AMBITO_INCIDENCIA_POSITION;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.testutil.ActivityTestUtil.checkSubscriptionsOnStop;
import static com.didekindroid.testutil.ActivityTestUtil.isResourceIdDisplayed;
import static com.didekindroid.testutil.ActivityTestUtil.isToastInView;
import static com.didekindroid.usuariocomunidad.repository.UserComuDao.userComuDao;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.makeListTwoUserComu;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.regTwoUserComuSameUser;
import static com.didekinlib.model.usuariocomunidad.Rol.ADMINISTRADOR;
import static com.didekinlib.model.usuariocomunidad.Rol.PROPIETARIO;
import static io.reactivex.Single.just;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 07/04/17
 * Time: 10:08
 */
@SuppressWarnings("WeakerAccess")
@RunWith(AndroidJUnit4.class)
public class ViewerIncidEditMaxFrTest {

    IncidEditAc activity;
    ViewerIncidEditMaxFr viewer;
    IncidenciaDataDbHelper dbHelper;
    IncidAndResolBundle resolBundle;
    UsuarioComunidad comuRealJuan;
    UsuarioComunidad comuPlazuelaJuan;


    @Rule
    public IntentsTestRule<IncidEditAc> activityRule = new IntentsTestRule<IncidEditAc>(IncidEditAc.class) {
        @Override
        protected Intent getActivityIntent()
        {
            try {
                regTwoUserComuSameUser(makeListTwoUserComu());
            } catch (Exception e) {
                fail();
            }
            List<UsuarioComunidad> userComus = userComuDao.seeUserComusByUser().blockingGet();
            comuRealJuan = userComus.get(0);
            comuPlazuelaJuan = userComus.get(1);
            // Perfil pro, iniciador de la incidencia. Incidencia sin resolución abierta.
            resolBundle = new IncidAndResolBundle(insertGetIncidImportancia(comuRealJuan, (short) 3), false);

            return new Intent().putExtra(INCID_RESOLUCION_BUNDLE.key, resolBundle);
        }
    };

    @Before
    public void setUp() throws Exception
    {
        activity = activityRule.getActivity();
        IncidEditMaxFr fragment = (IncidEditMaxFr) activity.getSupportFragmentManager().findFragmentByTag(IncidEditMaxFr.class.getName());
        dbHelper = new IncidenciaDataDbHelper(activity);

        AtomicReference<ViewerIncidEditMaxFr> viewerAtomic = new AtomicReference<>(null);
        viewerAtomic.compareAndSet(null, fragment.viewer);
        waitAtMost(4, SECONDS).untilAtomic(viewerAtomic, notNullValue());
        viewer = viewerAtomic.get();
    }

    @After
    public void tearDown() throws Exception
    {
        dbHelper.close();
        cleanOptions(CLEAN_JUAN);
    }

    //    ============================  TESTS  ===================================

    @Test
    public void testDoViewInViewer_1()
    {
        // testNewViewerIncidEditMaxFr
        assertThat(viewer.getController(), instanceOf(CtrlerIncidenciaCore.class));
        assertThat(viewer.getParentViewer(), is(activity.getInjectedParentViewer()));
        assertThat(viewer.viewerAmbitoIncidSpinner, notNullValue());
        assertThat(viewer.viewerImportanciaSpinner, notNullValue());

        // Preconditions.
        assertThat(viewer.hasResolucion.get(), is(false));

        IncidImportancia incidImportancia = viewer.resolBundle.getIncidImportancia();
        assertThat(incidImportancia, is(resolBundle.getIncidImportancia()));
        assertThat(viewer.incidenciaBean.getCodAmbitoIncid(), is(incidImportancia.getIncidencia().getAmbitoIncidencia().getAmbitoId()));
        assertThat(viewer.incidImportanciaBean.getImportancia(), is(incidImportancia.getImportancia()));

        checkScreenEditMaxPowerFrErase(resolBundle);
        checkDataEditMaxPowerFr(dbHelper, activity, incidImportancia);

        // testSaveState
        Bundle bundleTest = new Bundle();
        viewer.viewerAmbitoIncidSpinner.setSelectedItemId(11);
        viewer.viewerImportanciaSpinner.setSelectedItemId((short) 31);
        viewer.saveState(bundleTest);

        assertThat(bundleTest.getLong(AMBITO_INCIDENCIA_POSITION.key), is(11L));
        assertThat(bundleTest.getLong(INCID_IMPORTANCIA_NUMBER.key), is(31L));

        // testClearSubscriptions
        checkSubscriptionsOnStop(activity, viewer.viewerAmbitoIncidSpinner.getController(),
                viewer.getController());

    }

    @Test
    public void testDoViewInViewer_2()
    {
        // Preconditions.
        final IncidAndResolBundle newResolBundle = new IncidAndResolBundle(resolBundle.getIncidImportancia(), true);
        // Exec.
        activity.runOnUiThread(() -> viewer.doViewInViewer(new Bundle(0), newResolBundle));
        // Checks: NOT erase button.
        waitAtMost(4, SECONDS).untilTrue(viewer.hasResolucion);
        checkScreenEditMaxPowerFrNotErase(newResolBundle);
    }

    @Test
    public void testOnClickButtonModify_1()
    {
        activity.runOnUiThread(() -> {
            viewer.incidImportanciaBean.setImportancia((short) 11);
            viewer.onClickButtonModify();
        });
        waitAtMost(4, SECONDS).until(isToastInView(R.string.error_validation_msg, activity, R.string.incid_reg_importancia));
    }

    @Test
    public void testOnClickButtonModify_2()
    {
        activity.runOnUiThread(() -> {
            viewer.incidImportanciaBean.setImportancia((short) 1);
            viewer.onClickButtonModify();
        });
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(incidSeeByComuAcLayout));
    }

    @Test
    public void testOnClickButtonErase()
    {
        // Preconditions.
        checkScreenEditMaxPowerFrErase(activity.resolBundle);
        final AtomicBoolean isRun = new AtomicBoolean(false);

        activity.runOnUiThread(() -> {
            viewer.onClickButtonErase();
            isRun.compareAndSet(false, true);
        });
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(incidSeeByComuAcLayout));
    }

    @Test
    public void testOnSuccessModifyIncidImportancia()
    {
        checkComuInSpinner();
        // Exec with the other comunidad as parameter.
        viewer.onSuccessModifyIncidImportancia(comuPlazuelaJuan.getComunidad());
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(incidSeeByComuAcLayout));
        // Check comuSpinner initialization with the comunidad in the method parameter.
        waitAtMost(4, SECONDS).until(isComuSpinnerWithText(comuPlazuelaJuan.getComunidad().getNombreComunidad()));
    }

    @Test
    public void testOnSuccessEraseIncidencia()
    {
        checkComuInSpinner();
        // Exec with the other comunidad as parameter.
        viewer.onSuccessEraseIncidencia(comuPlazuelaJuan.getComunidad());
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(incidSeeByComuAcLayout));
        // Check comuSpinner initialization with the comunidad in the method parameter.
        waitAtMost(4, SECONDS).until(isComuSpinnerWithText(comuPlazuelaJuan.getComunidad().getNombreComunidad()));
    }

    @Test
    public void test_EraseIncidenciaObserver()
    {
        just(1).subscribeWith(viewer.new EraseIncidenciaObserver(comuRealJuan.getComunidad()));
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(incidSeeByComuAcLayout));
    }

    @Test
    public void test_CanUserEraseIRncidencia()
    {
        viewer.hasResolucion.set(false);
        assertThat(viewer.canUserEraseIncidencia(doIncidImportancia("noAdm_name", PROPIETARIO.function)), is(false));
        assertThat(viewer.canUserEraseIncidencia(doIncidImportancia("initiator_name", PROPIETARIO.function)), is(true));
        assertThat(viewer.canUserEraseIncidencia(doIncidImportancia("adm_name", ADMINISTRADOR.function)), is(true));

        viewer.hasResolucion.set(true);
        assertThat(viewer.canUserEraseIncidencia(doIncidImportancia("noAdm_name", PROPIETARIO.function)), is(false));
        assertThat(viewer.canUserEraseIncidencia(doIncidImportancia("initiator_name", PROPIETARIO.function)), is(false));
        assertThat(viewer.canUserEraseIncidencia(doIncidImportancia("adm_name", ADMINISTRADOR.function)), is(false));

    }

    //    ............................... HELPERS .................................

    @NonNull
    private IncidImportancia doIncidImportancia(String ratingUserName, String rol)
    {
        return new IncidImportancia.IncidImportanciaBuilder(
                new Incidencia.IncidenciaBuilder()
                        .incidenciaId(999L)
                        .comunidad(new Comunidad.ComunidadBuilder().c_id(111L).build())
                        .userName("initiator_name").build())
                .usuarioComunidad(
                        new UsuarioComunidad.UserComuBuilder(
                                new Comunidad.ComunidadBuilder().c_id(111L).build(),
                                new Usuario.UsuarioBuilder().userName(ratingUserName).build())
                                .roles(rol)
                                .build())
                .build();
    }

    private void checkComuInSpinner()
    {
        // Precondition: comuRealJuan is shown in screen.
        waitAtMost(4, SECONDS).until(() -> {
            onView(allOf(
                    withId(R.id.incid_comunidad_txt),
                    withText(comuRealJuan.getComunidad().getNombreComunidad())
            )).check(matches(isDisplayed()));
            return true;
        });
    }
}