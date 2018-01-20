package com.didekindroid.incidencia.core.edit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekindroid.incidencia.core.CtrlerIncidenciaCore;
import com.didekindroid.incidencia.core.IncidenciaDataDbHelper;
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

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.makeRegGetIncidImportancia;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkDataEditMaxPowerFr;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkScreenEditMaxPowerFrErase;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkScreenEditMaxPowerFrNotErase;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.isComuSpinnerWithText;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidSeeByComuAcLayout;
import static com.didekindroid.incidencia.utils.IncidBundleKey.AMBITO_INCIDENCIA_POSITION;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_IMPORTANCIA_NUMBER;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_BUNDLE;
import static com.didekindroid.testutil.ActivityTestUtils.checkSubscriptionsOnStop;
import static com.didekindroid.testutil.ActivityTestUtils.isResourceIdDisplayed;
import static com.didekindroid.testutil.ActivityTestUtils.isToastInView;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.repository.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.makeListTwoUserComu;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.regTwoUserComuSameUser;
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
                List<UsuarioComunidad> userComus = userComuDaoRemote.seeUserComusByUser();
                comuRealJuan = userComus.get(0);
                comuPlazuelaJuan = userComus.get(1);
                // Perfil pro, iniciador de la incidencia. Incidencia sin resolución abierta.
                resolBundle = new IncidAndResolBundle(makeRegGetIncidImportancia(comuRealJuan, (short) 3), false);
            } catch (IOException | UiException e) {
                fail();
            }

            Intent intent = new Intent();
            intent.putExtra(INCID_RESOLUCION_BUNDLE.key, resolBundle);
            return intent;
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
    public void testNewViewerIncidEditMaxFr() throws Exception
    {
        assertThat(viewer.getController(), instanceOf(CtrlerIncidenciaCore.class));
        assertThat(viewer.getParentViewer(), is(activity.getParentViewer()));
        assertThat(viewer.viewerAmbitoIncidSpinner, notNullValue());
        assertThat(viewer.viewerImportanciaSpinner, notNullValue());
    }

    @Test
    public void testDoViewInViewer_1() throws Exception
    {
        // Preconditions.
        assertThat(viewer.hasResolucion.get(), is(false));

        IncidImportancia incidImportancia = viewer.resolBundle.getIncidImportancia();
        assertThat(incidImportancia, is(resolBundle.getIncidImportancia()));
        assertThat(viewer.incidenciaBean.getCodAmbitoIncid(), is(incidImportancia.getIncidencia().getAmbitoIncidencia().getAmbitoId()));
        assertThat(viewer.incidImportanciaBean.getImportancia(), is(incidImportancia.getImportancia()));

        checkScreenEditMaxPowerFrErase(resolBundle);
        checkDataEditMaxPowerFr(dbHelper, activity, incidImportancia);
    }

    @Test
    public void testDoViewInViewer_2() throws Exception
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
    public void testOnClickButtonModify_1() throws Exception
    {
        activity.runOnUiThread(() -> {
            viewer.incidImportanciaBean.setImportancia((short) 11);
            viewer.onClickButtonModify();
        });
        waitAtMost(4, SECONDS).until(isToastInView(R.string.error_validation_msg, activity, R.string.incid_reg_importancia));
    }

    @Test
    public void testOnClickButtonModify_2() throws Exception
    {
        activity.runOnUiThread(() -> {
            viewer.incidImportanciaBean.setImportancia((short) 1);
            viewer.onClickButtonModify();
        });
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(incidSeeByComuAcLayout));
    }

    @Test
    public void testOnClickButtonErase() throws Exception
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
    public void testOnSuccessModifyIncidImportancia() throws Exception
    {
        // Precondition: comuRealJuan is shown in screen.
        waitAtMost(4, SECONDS).until(() -> {
            onView(allOf(
                    withId(R.id.incid_comunidad_txt),
                    withText(comuRealJuan.getComunidad().getNombreComunidad())
            )).check(matches(isDisplayed()));
            return true;
        });
        // Exec with the other comunidad as parameter.
        viewer.onSuccessModifyIncidImportancia(comuPlazuelaJuan.getComunidad());
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(incidSeeByComuAcLayout));
        // Check comuSpinner initialization with the comunidad in the method parameter.
        waitAtMost(4, SECONDS).until(isComuSpinnerWithText(comuPlazuelaJuan.getComunidad().getNombreComunidad()));
    }

    @Test
    public void testOnSuccessEraseIncidencia() throws Exception
    {
        viewer.onSuccessEraseIncidencia(1);
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(incidSeeByComuAcLayout));
    }

    @Test
    public void test_EraseIncidenciaObserver()
    {
        just(1).subscribeWith(viewer.new EraseIncidenciaObserver());
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(incidSeeByComuAcLayout));
    }

    @Test
    public void test_CanUserEraseIRncidencia() throws Exception
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

    @Test
    public void testSaveState() throws Exception
    {
        Bundle bundleTest = new Bundle();
        viewer.viewerAmbitoIncidSpinner.setSelectedItemId(11);
        viewer.viewerImportanciaSpinner.setSelectedItemId((short) 31);
        viewer.saveState(bundleTest);

        assertThat(bundleTest.getLong(AMBITO_INCIDENCIA_POSITION.key), is(11L));
        assertThat(bundleTest.getLong(INCID_IMPORTANCIA_NUMBER.key), is(31L));
    }

    //    ============================  LIFE CYCLE TESTS  ===================================

    /* We check that all the viewers' controllers are invoked, as the result of invoking the method viewer.clearSubscriptions.
     * It serves also as a test on the activity's onStop() method. */
    @Test
    public void testClearSubscriptions() throws Exception
    {
        checkSubscriptionsOnStop(activity, viewer.viewerAmbitoIncidSpinner.getController(),
                viewer.viewerImportanciaSpinner.getController(),
                viewer.getController());
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
}