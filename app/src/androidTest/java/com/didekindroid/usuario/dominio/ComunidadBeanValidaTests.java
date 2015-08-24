package com.didekindroid.usuario.dominio;

import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import com.didekindroid.R;
import com.didekindroid.masterdata.dominio.Municipio;
import com.didekindroid.masterdata.dominio.Provincia;
import com.didekindroid.usuario.comunidad.dominio.ComunidadBean;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 19/05/15
 * Time: 11:50
 */
@RunWith(AndroidJUnit4.class)
public class ComunidadBeanValidaTests {

    private final String TAG = ComunidadBeanValidaTests.class.getCanonicalName();

    private static final String TIPO_VIA_ERROR = "errorTipoVia";
    private static final String ERROR_GENERIC = "genericError";
    private StringBuilder errors;
    private Resources resources;

    @Before
    public void doBefore()
    {
        Log.d(TAG, "doBefore()");
        resources = InstrumentationRegistry.getTargetContext().getResources();
        errors = new StringBuilder(resources.getText(R.string.error_validation_msg));
        Log.d(TAG, "doBefore()" + " capacity= " + errors.capacity());
    }

    @Test
    public void testPreconditions()
    {
        Log.d(TAG, "testPreconditions()");
        assertThat(errors, notNullValue());
        assertThat(resources, notNullValue());
    }

    @Test
    public void testValidate() throws Exception
    {
        ComunidadBean comunidadBean = new ComunidadBean("select *", "24 de Otoño", "00A1", "",
                new Municipio(new Provincia((short) 12), (short) 53));

        boolean isValid = comunidadBean.validate(resources, errors);
        assertThat(isValid, is(false));
        assertThat(errors.toString(), containsString(resources.getText(R.string.numero_en_via).toString()));
    }

    @Test
    public void testGetNombreVia() throws Exception
    {
        /*"[0-9a-zA-ZñÑáéíóúüÜ[\s]]{2,150}*/

        ComunidadBean comunidadBean = new ComunidadBean("tipoVia1", "avenida de las 25 ñs", "", "", new Municipio());
        assertThat(comunidadBean.validateNombreVia(ERROR_GENERIC, errors), equalTo(true));
        comunidadBean.setNombreVia("avenid@ de lo$");
        assertThat(comunidadBean.validateNombreVia(ERROR_GENERIC, errors), equalTo(false));
    }

    @Test
    public void testGetNumeroEnVia() throws Exception
    {
        ComunidadBean comunidadBean = new ComunidadBean("tipoVia1", "avenida de las 25 ñs", "001", "", new Municipio());
        assertThat(comunidadBean.validateNumeroEnVia(ERROR_GENERIC, errors), equalTo(true));

        comunidadBean = new ComunidadBean("tipoVia1", "avenida de las 25 ñs", "23412", "", new Municipio());
        assertThat(comunidadBean.validateNumeroEnVia(ERROR_GENERIC, errors), equalTo(true));

        comunidadBean = new ComunidadBean("tipoVia1", "avenida de las 25 ñs", "select *", "", new Municipio());
        assertThat(comunidadBean.validateNumeroEnVia(ERROR_GENERIC, errors), equalTo(false));

        comunidadBean = new ComunidadBean("", "", "AB", "", new Municipio());
        assertThat(comunidadBean.validateNumeroEnVia(resources.getText(R.string.numero_en_via), errors),
                equalTo(false));
        assertThat(errors.toString(), containsString(resources.getText(R.string.numero_en_via).toString()));
    }

    @Test
    public void testValidateSufijo() throws Exception
    {
        ComunidadBean comunidadBean = new ComunidadBean("", "", "", "ñÍs", new Municipio());
        assertThat(comunidadBean.validateSufijo(TIPO_VIA_ERROR, errors), equalTo(true));
        comunidadBean = new ComunidadBean("", "", "", "w 2", new Municipio());
        assertThat(comunidadBean.validateSufijo(TIPO_VIA_ERROR, errors), equalTo(false));
    }

    @After
    public void doAfter()
    {
    }
}