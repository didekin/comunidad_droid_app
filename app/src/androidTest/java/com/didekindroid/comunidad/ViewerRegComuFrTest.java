package com.didekindroid.comunidad;

import android.content.Intent;
import android.os.Bundle;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.comunidad.spinner.MunicipioSpinnerEventItemSelect;
import com.didekindroid.comunidad.spinner.TipoViaValueObj;
import com.didekindroid.exception.UiException;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.comunidad.ComunidadAutonoma;
import com.didekinlib.model.comunidad.Municipio;
import com.didekinlib.model.comunidad.Provincia;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.comunidad.testutil.ComuDataTestUtil.COMU_LA_FUENTE;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.checkMunicipioSpinner;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.checkProvinciaSpinner;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.checkRegComuFrView;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.doComunAutonomaSpinner;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.doMunicipioSpinner;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.doProvinciaSpinner;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.doTipoViaSpinner;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.typeComuCalleNumero;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.regComuFrLayout;
import static com.didekindroid.comunidad.utils.ComuBundleKey.COMUNIDAD_AUTONOMA_ID;
import static com.didekindroid.comunidad.utils.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.comunidad.utils.ComuBundleKey.MUNICIPIO_SPINNER_EVENT;
import static com.didekindroid.comunidad.utils.ComuBundleKey.PROVINCIA_ID;
import static com.didekindroid.comunidad.utils.ComuBundleKey.TIPO_VIA_ID;
import static com.didekindroid.testutil.ActivityTestUtils.addSubscription;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpWithTkGetComu;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 09/05/17
 * Time: 13:12
 */
@RunWith(AndroidJUnit4.class)
public class ViewerRegComuFrTest {

    ViewerRegComuFr viewer;
    Comunidad comunidad;
    @Rule
    public ActivityTestRule<ComuDataAc> activityRule = new ActivityTestRule<ComuDataAc>(ComuDataAc.class) {
        @Override
        protected Intent getActivityIntent()
        {
            try {
                comunidad = signUpWithTkGetComu(COMU_PLAZUELA5_JUAN);
            } catch (IOException | UiException e) {
                fail();
            }
            Intent intent = new Intent();
            intent.putExtra(COMUNIDAD_ID.key, comunidad.getC_Id());
            return intent;
        }
    };
    ComuDataAc activity;
    RegComuFr fragment;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        fragment = (RegComuFr) activity.getSupportFragmentManager().findFragmentById(regComuFrLayout);

        AtomicReference<ViewerRegComuFr> viewerAtomic = new AtomicReference<>(null);
        viewerAtomic.compareAndSet(null, fragment.viewer);
        waitAtMost(4, SECONDS).untilAtomic(viewerAtomic, notNullValue());
        viewer = viewerAtomic.get();
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(CLEAN_JUAN);
    }

    @Test
    public void test_NewViewerRegComuFr() throws Exception
    {
        assertThat(CtrlerRegComuFr.class.cast(viewer.getController()), notNullValue());
        assertThat(viewer.tipoViaSpinner, notNullValue());
        assertThat(viewer.comuAutonomaSpinner, notNullValue());
        assertThat(viewer.provinciaSpinner, notNullValue());
        assertThat(viewer.municipioSpinner, notNullValue());
    }

    @Test
    public void test_DoViewInViewer() throws Exception
    {
        // Case: ComuDataAc instance; viewBean != null.
        onView(withId(regComuFrLayout)).check(matches(isDisplayed()));
        checkRegComuFrView(comunidad, "Galicia");
    }

    @Test
    public void test_ClearSubscriptions() throws Exception
    {
        addSubscription(viewer.comuAutonomaSpinner.getController());
        addSubscription(viewer.provinciaSpinner.getController());
        addSubscription(viewer.municipioSpinner.getController());
        addSubscription(viewer.tipoViaSpinner.getController());

        assertThat(viewer.clearSubscriptions(), is(0));
        assertThat(viewer.tipoViaSpinner.getController().getSubscriptions().size(), is(0));
        assertThat(viewer.comuAutonomaSpinner.getController().getSubscriptions().size(), is(0));
        assertThat(viewer.provinciaSpinner.getController().getSubscriptions().size(), is(0));
        assertThat(viewer.municipioSpinner.getController().getSubscriptions().size(), is(0));
    }

    @Test
    public void test_SaveState() throws Exception
    {
        Bundle bundle = new Bundle(4);
        viewer.comuAutonomaSpinner.setItemSelectedId(2L);
        viewer.provinciaSpinner.setItemSelectedId(11L);
        viewer.municipioSpinner.setSpinnerEvent(new Municipio((short) 23, new Provincia((short) 34)));
        viewer.tipoViaSpinner.setItemSelectedId(12L);

        viewer.saveState(bundle);
        assertThat(bundle.getLong(COMUNIDAD_AUTONOMA_ID.key), is(2L));
        assertThat(bundle.getLong(PROVINCIA_ID.key), is(11L));
        assertThat(bundle.getLong(TIPO_VIA_ID.key), is(12L));
        final MunicipioSpinnerEventItemSelect spinnerEventItem = MunicipioSpinnerEventItemSelect.class.cast(bundle.getSerializable(MUNICIPIO_SPINNER_EVENT.key));
        assertThat(spinnerEventItem.getSpinnerItemIdSelect(), is(23L));
        assertThat(spinnerEventItem.getMunicipio().getProvincia().getProvinciaId(), is((short) 34));
    }

    @Test
    public void test_DoOnClickItemId_1() throws Exception
    {
        checkMunicipioSpinner(comunidad.getMunicipio().getNombre()); // Esperamos por los viejos datos.
        // Cambiamos comunidad autónoma: cambio en provincias y municipios, posiciones 0 o iniciales.
        doComunAutonomaSpinner(new ComunidadAutonoma((short) 11, "Extremadura"));
        checkProvinciaSpinner("Badajoz");
        checkMunicipioSpinner("Acedera");
    }

    @Test
    public void test_DoOnClickItemId_2() throws Exception
    {
        // Cambiamos provincia: cambian los municipios.
        checkMunicipioSpinner(comunidad.getMunicipio().getNombre()); // Esperamos por los viejos datos.
        doProvinciaSpinner(new Provincia((short) 36, "Pontevedra"));
        checkMunicipioSpinner("Agolada");
    }

    @Test
    public void test_OnSuccessLoadComunidad() throws Exception
    {
        checkMunicipioSpinner(comunidad.getMunicipio().getNombre()); // Esperamos por los viejos datos.
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                viewer.onSuccessLoadComunidad(COMU_LA_FUENTE, null);
            }
        });
        checkMunicipioSpinner(COMU_LA_FUENTE.getMunicipio().getNombre());
        checkRegComuFrView(COMU_LA_FUENTE, "Valencia");
    }

    @Test
    public void test_GetComunidadFromViewer_OK() throws Exception
    {
        checkMunicipioSpinner(comunidad.getMunicipio().getNombre()); // Esperamos por los viejos datos.

        doTipoViaSpinner(new TipoViaValueObj(275, "Ronda"));
        typeComuCalleNumero(comunidad.getNombreVia(), String.valueOf(comunidad.getNumero()), comunidad.getSufijoNumero());
        doComunAutonomaSpinner(new ComunidadAutonoma((short) 12, "Galicia"));
        doProvinciaSpinner(comunidad.getMunicipio().getProvincia());
        doMunicipioSpinner(comunidad.getMunicipio());

        assertThat(viewer.getComunidadFromViewer(new StringBuilder(activity.getText(R.string.error_validation_msg))), is(comunidad));
    }

    @Test
    public void test_GetComunidadFromViewer_Error() throws Exception
    {
        checkMunicipioSpinner(comunidad.getMunicipio().getNombre()); // Esperamos por los viejos datos.

        doTipoViaSpinner(new TipoViaValueObj(0, activity.getText(R.string.tipo_via_spinner).toString())); // Valor por defecto: no selección.

        final StringBuilder errors = new StringBuilder(activity.getText(R.string.error_validation_msg));
        assertThat(viewer.getComunidadFromViewer(errors), nullValue());
        assertThat(errors.toString(), containsString(activity.getText(R.string.tipo_via).toString()));
    }
}