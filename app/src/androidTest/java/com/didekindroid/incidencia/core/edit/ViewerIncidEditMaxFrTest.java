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
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

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
import static com.didekindroid.testutil.ActivityTestUtil.checkComuInSpinner;
import static com.didekindroid.testutil.ActivityTestUtil.checkSubscriptionsOnStop;
import static com.didekindroid.testutil.ActivityTestUtil.isResourceIdDisplayed;
import static com.didekindroid.testutil.ActivityTestUtil.isToastInView;
import static com.didekindroid.usuariocomunidad.repository.UserComuDao.userComuDao;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.makeListTwoUserComu;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.regTwoUserComuSameUser;
import static com.didekinlib.model.usuariocomunidad.Rol.ADMINISTRADOR;
import static com.didekinlib.model.usuariocomunidad.Rol.PROPIETARIO;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 07/04/17
 * Time: 10:08
 */
@SuppressWarnings("WeakerAccess")
@RunWith(AndroidJUnit4.class)
public class ViewerIncidEditMaxFrTest {

    IncidEditAc activity;
    IncidenciaDataDbHelper dbHelper;
    static IncidAndResolBundle resolBundle;
    static UsuarioComunidad comuRealJuan;
    static UsuarioComunidad comuPlazuelaJuan;
    IncidEditMaxFr fragment;


    @Rule
    public IntentsTestRule<IncidEditAc> activityRule = new IntentsTestRule<IncidEditAc>(IncidEditAc.class) {
        @Override
        protected Intent getActivityIntent()
        {
            return new Intent().putExtra(INCID_RESOLUCION_BUNDLE.key, resolBundle);
        }
    };

    @BeforeClass
    public static void setUpStatic() throws Exception
    {
        regTwoUserComuSameUser(makeListTwoUserComu());
        List<UsuarioComunidad> userComus = userComuDao.seeUserComusByUser().blockingGet();
        comuRealJuan = userComus.get(0);
        comuPlazuelaJuan = userComus.get(1);
        // Perfil pro, iniciador de la incidencia. Incidencia sin resolución abierta.
        resolBundle = new IncidAndResolBundle(insertGetIncidImportancia(comuRealJuan, (short) 3), false);
    }

    @Before
    public void setUp() throws Exception
    {
        activity = activityRule.getActivity();
        fragment = (IncidEditMaxFr) activity.getSupportFragmentManager().findFragmentByTag(IncidEditMaxFr.class.getName());
        dbHelper = new IncidenciaDataDbHelper(activity);
        waitAtMost(4, SECONDS).until(() -> fragment != null && fragment.viewer != null);
    }

    @After
    public void tearDown() throws Exception
    {
        dbHelper.close();
    }

    @AfterClass
    public static void cleanStatic()
    {
        cleanOptions(CLEAN_JUAN);
    }

    //    ============================  TESTS  ===================================

    @Test
    public void testDoViewInViewer_1()
    {
        // testNewViewerIncidEditMaxFr
        assertThat(fragment.viewer.getController(), instanceOf(CtrlerIncidenciaCore.class));
        assertThat(fragment.viewer.getParentViewer(), is(activity.getInjectedParentViewer()));
        assertThat(fragment.viewer.viewerAmbitoIncidSpinner, notNullValue());
        assertThat(fragment.viewer.viewerImportanciaSpinner, notNullValue());

        // Preconditions.
        assertThat(fragment.viewer.hasResolucion.get(), is(false));

        IncidImportancia incidImportancia = fragment.viewer.resolBundle.getIncidImportancia();
        assertThat(incidImportancia, is(resolBundle.getIncidImportancia()));
        assertThat(fragment.viewer.incidenciaBean.getCodAmbitoIncid(), is(incidImportancia.getIncidencia().getAmbitoIncidencia().getAmbitoId()));
        assertThat(fragment.viewer.incidImportanciaBean.getImportancia(), is(incidImportancia.getImportancia()));

        checkScreenEditMaxPowerFrErase(resolBundle);
        checkDataEditMaxPowerFr(dbHelper, activity, incidImportancia);

        // testSaveState
        Bundle bundleTest = new Bundle();
        fragment.viewer.viewerAmbitoIncidSpinner.setSelectedItemId(11);
        fragment.viewer.viewerImportanciaSpinner.setSelectedItemId((short) 31);
        fragment.viewer.saveState(bundleTest);

        assertThat(bundleTest.getLong(AMBITO_INCIDENCIA_POSITION.key), is(11L));
        assertThat(bundleTest.getLong(INCID_IMPORTANCIA_NUMBER.key), is(31L));

        // testClearSubscriptions
        checkSubscriptionsOnStop(activity, fragment.viewer.viewerAmbitoIncidSpinner.getController(),
                fragment.viewer.getController());

    }

    @Test
    public void testDoViewInViewer_2()
    {
        // Preconditions.
        final IncidAndResolBundle newResolBundle = new IncidAndResolBundle(resolBundle.getIncidImportancia(), true);
        // Exec.
        activity.runOnUiThread(() -> fragment.viewer.doViewInViewer(new Bundle(0), newResolBundle));
        // Checks: NOT erase button.
        waitAtMost(4, SECONDS).untilTrue(fragment.viewer.hasResolucion);
        checkScreenEditMaxPowerFrNotErase(newResolBundle);
    }

    @Test
    public void testOnClickButtonModify()
    {
        // ERROR.
        activity.runOnUiThread(() -> {
            fragment.viewer.incidImportanciaBean.setImportancia((short) 11);
            fragment.viewer.onClickButtonModify();
        });
        waitAtMost(4, SECONDS).until(isToastInView(R.string.error_validation_msg, activity, R.string.incid_reg_importancia));

        // OK.
        activity.runOnUiThread(() -> {
            fragment.viewer.incidImportanciaBean.setImportancia((short) 1);
            fragment.viewer.onClickButtonModify();
        });
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(incidSeeByComuAcLayout));
    }

    @Test
    public void testOnSuccessModifyIncidImportancia()
    {
        checkComuInSpinner(comuRealJuan);
        // Exec with the other comunidad as parameter.
        fragment.viewer.onSuccessModifyIncidImportancia(comuPlazuelaJuan.getComunidad());
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(incidSeeByComuAcLayout));
        // Check comuSpinner initialization with the comunidad in the method parameter.
        waitAtMost(4, SECONDS).until(isComuSpinnerWithText(comuPlazuelaJuan.getComunidad().getNombreComunidad()));
    }

    @Test
    public void test_CanUserEraseIRncidencia()
    {
        fragment.viewer.hasResolucion.set(false);
        assertThat(fragment.viewer.canUserEraseIncidencia(doIncidImportancia("noAdm_name", PROPIETARIO.function)), is(false));
        assertThat(fragment.viewer.canUserEraseIncidencia(doIncidImportancia("initiator_name", PROPIETARIO.function)), is(true));
        assertThat(fragment.viewer.canUserEraseIncidencia(doIncidImportancia("adm_name", ADMINISTRADOR.function)), is(true));

        fragment.viewer.hasResolucion.set(true);
        assertThat(fragment.viewer.canUserEraseIncidencia(doIncidImportancia("noAdm_name", PROPIETARIO.function)), is(false));
        assertThat(fragment.viewer.canUserEraseIncidencia(doIncidImportancia("initiator_name", PROPIETARIO.function)), is(false));
        assertThat(fragment.viewer.canUserEraseIncidencia(doIncidImportancia("adm_name", ADMINISTRADOR.function)), is(false));

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