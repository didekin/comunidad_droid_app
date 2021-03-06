package com.didekindroid.comunidad;

import android.content.Intent;
import android.os.Bundle;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.lib_one.comunidad.spinner.MunicipioSpinnerEventItemSelect;
import com.didekindroid.lib_one.comunidad.spinner.TipoViaValueObj;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.comunidad.ComunidadAutonoma;
import com.didekinlib.model.comunidad.Municipio;
import com.didekinlib.model.comunidad.Provincia;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.checkMunicipioSpinner;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.checkProvinciaSpinner;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.checkRegComuFrView;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.doComunAutonomaSpinner;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.doMunicipioSpinner;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.doProvinciaSpinner;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.doTipoViaSpinner;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.typeComuCalleNumero;
import static com.didekindroid.comunidad.testutil.ComuTestData.COMU_LA_FUENTE;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.regComuFrLayout;
import static com.didekindroid.comunidad.util.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.lib_one.comunidad.spinner.ComunidadSpinnerKey.COMUNIDAD_AUTONOMA_ID;
import static com.didekindroid.lib_one.comunidad.spinner.ComunidadSpinnerKey.MUNICIPIO_SPINNER_EVENT;
import static com.didekindroid.lib_one.comunidad.spinner.ComunidadSpinnerKey.PROVINCIA_ID;
import static com.didekindroid.lib_one.comunidad.spinner.ComunidadSpinnerKey.TIPO_VIA_ID;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.testutil.ActivityTestUtil.checkSubscriptionsOnStop;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.signUpGetComu;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 09/05/17
 * Time: 13:12
 */
@RunWith(AndroidJUnit4.class)
public class ViewerRegComuFrTest {

    static Comunidad comunidad;
    private ComuDataAc activity;
    private RegComuFr fragment;

    @Rule
    public ActivityTestRule<ComuDataAc> activityRule = new ActivityTestRule<ComuDataAc>(ComuDataAc.class) {
        @Override
        protected Intent getActivityIntent()
        {
            return new Intent().putExtra(COMUNIDAD_ID.key, comunidad.getC_Id());
        }
    };

    @BeforeClass
    public static void setStatic() throws Exception
    {
        comunidad = signUpGetComu(COMU_PLAZUELA5_JUAN);
    }

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        fragment = (RegComuFr) activity.getSupportFragmentManager().findFragmentById(regComuFrLayout);
        waitAtMost(4, SECONDS).until(() -> fragment.viewer != null);
    }

    @AfterClass
    public static void tearDown()
    {
        cleanOptions(CLEAN_JUAN);
    }

    //    =============================================================================================

    @Test
    public void test_DoViewInViewer()
    {
        // test_NewViewerRegComuFr
        assertThat(CtrlerComunidad.class.cast(fragment.viewer.getController()), notNullValue());
        assertThat(fragment.viewer.tipoViaSpinner, notNullValue());
        assertThat(fragment.viewer.comuAutonomaSpinner, notNullValue());
        assertThat(fragment.viewer.provinciaSpinner, notNullValue());
        assertThat(fragment.viewer.municipioSpinner, notNullValue());

        // Case: ComuDataAc instance; viewBean != null.
        onView(withId(regComuFrLayout)).check(matches(isDisplayed()));
        checkRegComuFrView(comunidad, "Galicia");

        // test_OnActivityCreated
        assertThat(fragment.viewerInjector.getInjectedParentViewer().getChildViewer(fragment.viewer.getClass()), is(fragment.viewer));

        // test_SaveState
        Bundle bundle = new Bundle(4);
        fragment.viewer.comuAutonomaSpinner.setSelectedItemId(2L);
        fragment.viewer.provinciaSpinner.setSelectedItemId(11L);
        fragment.viewer.municipioSpinner.setSpinnerEvent(new Municipio((short) 23, new Provincia((short) 34)));
        fragment.viewer.tipoViaSpinner.setSelectedItemId(12L);

        fragment.viewer.saveState(bundle);
        assertThat(bundle.getLong(COMUNIDAD_AUTONOMA_ID.key), is(2L));
        assertThat(bundle.getLong(PROVINCIA_ID.key), is(11L));
        assertThat(bundle.getLong(TIPO_VIA_ID.key), is(12L));
        final MunicipioSpinnerEventItemSelect spinnerEventItem = MunicipioSpinnerEventItemSelect.class.cast(bundle.getSerializable(MUNICIPIO_SPINNER_EVENT.key));
        assertThat(spinnerEventItem.getSpinnerItemIdSelect(), is(23L));
        assertThat(spinnerEventItem.getMunicipio().getProvincia().getProvinciaId(), is((short) 34));

        // test_ClearSubscriptions()
        checkSubscriptionsOnStop(activity, fragment.viewer.tipoViaSpinner.getController(),
                fragment.viewer.comuAutonomaSpinner.getController(),
                fragment.viewer.provinciaSpinner.getController(),
                fragment.viewer.municipioSpinner.getController(),
                fragment.viewer.getController());
    }

    @Test
    public void test_DoOnClickItemId_1()
    {
        checkMunicipioSpinner(comunidad.getMunicipio().getNombre()); // Esperamos por los viejos datos.
        // Cambiamos comunidad autónoma: cambio en provincias y municipios, posiciones 0 o iniciales.
        doComunAutonomaSpinner(new ComunidadAutonoma((short) 11, "Extremadura"));
        checkProvinciaSpinner("Badajoz");
        checkMunicipioSpinner("Acedera");
    }

    @Test
    public void test_DoOnClickItemId_2()
    {
        // Cambiamos provincia: cambian los municipios.
        checkMunicipioSpinner(comunidad.getMunicipio().getNombre()); // Esperamos por los viejos datos.
        doProvinciaSpinner(new Provincia((short) 36, "Pontevedra"));
        checkMunicipioSpinner("Agolada");
    }

    @Test
    public void test_OnSuccessLoadComunidad()
    {
        checkMunicipioSpinner(comunidad.getMunicipio().getNombre()); // Esperamos por los viejos datos.
        activity.runOnUiThread(() -> fragment.viewer.onSuccessLoadComunidad(COMU_LA_FUENTE, null));
        checkMunicipioSpinner(COMU_LA_FUENTE.getMunicipio().getNombre());
        checkRegComuFrView(COMU_LA_FUENTE, "Valencia");
    }

    @Test
    public void test_GetComunidadFromViewer_OK() throws Exception
    {
        checkMunicipioSpinner(comunidad.getMunicipio().getNombre()); // Esperamos por los viejos datos.

        doTipoViaSpinner(new TipoViaValueObj(275, "Ronda"));
        typeComuCalleNumero(comunidad.getNombreVia(), String.valueOf(comunidad.getNumero()), comunidad.getSufijoNumero());
        SECONDS.sleep(1);
        doComunAutonomaSpinner(new ComunidadAutonoma((short) 12, "Galicia"));
        SECONDS.sleep(1);
        doProvinciaSpinner(comunidad.getMunicipio().getProvincia());
        SECONDS.sleep(1);
        doMunicipioSpinner(comunidad.getMunicipio());

        Comunidad comunidadFromView = fragment.viewer.getComunidadFromViewer(new StringBuilder(activity.getText(R.string.error_validation_msg)));
        assertThat(comunidadFromView, is(comunidad));
        // Check explicitly for comunidadAutonoma.
        assertThat(comunidadFromView.getMunicipio().getProvincia().getComunidadAutonoma(), is(new ComunidadAutonoma((short) 12, "Galicia")));
    }

    @Test
    public void test_GetComunidadFromViewer_Error()
    {
        checkMunicipioSpinner(comunidad.getMunicipio().getNombre()); // Esperamos por los viejos datos.

        doTipoViaSpinner(new TipoViaValueObj(0, activity.getText(R.string.tipo_via_spinner).toString())); // Valor por defecto: no selección.

        final StringBuilder errors = new StringBuilder(activity.getText(R.string.error_validation_msg));
        assertThat(fragment.viewer.getComunidadFromViewer(errors), nullValue());
        assertThat(errors.toString(), containsString(activity.getText(R.string.tipo_via).toString()));
    }
}