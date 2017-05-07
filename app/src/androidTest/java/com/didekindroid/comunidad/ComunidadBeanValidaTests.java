package com.didekindroid.comunidad;

import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.comunidad.spinner.TipoViaValueObj;
import com.didekinlib.model.comunidad.Municipio;
import com.didekinlib.model.comunidad.Provincia;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 19/05/15
 * Time: 11:50
 */
@RunWith(AndroidJUnit4.class)
public class ComunidadBeanValidaTests {

    private static final String TIPO_VIA_ERROR = "errorTipoVia";
    private static final String ERROR_GENERIC = "genericError";
    private StringBuilder errors;
    private Resources resources;

    @Before
    public void doBefore()
    {

        resources = InstrumentationRegistry.getTargetContext().getResources();
        errors = new StringBuilder(resources.getText(R.string.error_validation_msg));
    }

    @Test
    public void testPreconditions()
    {
        assertThat(errors, notNullValue());
        assertThat(resources, notNullValue());
    }

    @Test
    public void testValidate() throws Exception
    {
        ComunidadBean comunidadBean = new ComunidadBean(new TipoViaValueObj(0, "select *"), "24 de Otoño", "00A1", "",
                new Municipio((short) 53, new Provincia((short) 12)));

        boolean isValid = comunidadBean.validate(resources, errors);
        assertThat(isValid, is(false));
        assertThat(errors.toString(), containsString(resources.getText(R.string.numero_en_via).toString()));
    }

    @Test
    public void testGetNombreVia() throws Exception
    {
        /*"[0-9a-zA-ZñÑáéíóúüÜ[\s]]{2,150}*/

        ComunidadBean comunidadBean = new ComunidadBean(new TipoViaValueObj(0, "tipoVia1"), "avenida de las 25 ñs", "", "",
                new Municipio((short) 112));
        assertThat(comunidadBean.validateNombreVia(ERROR_GENERIC, errors), equalTo(true));
        comunidadBean.setNombreVia("avenid@ de lo$");
        assertThat(comunidadBean.validateNombreVia(ERROR_GENERIC, errors), equalTo(false));
    }

    @Test
    public void testGetNumeroEnVia() throws Exception
    {
        ComunidadBean comunidadBean = new ComunidadBean(new TipoViaValueObj(0, "tipoVia1"), "avenida de las 25 ñs", "001", "",
                new Municipio((short) 118));
        assertThat(comunidadBean.validateNumeroEnVia(ERROR_GENERIC, errors), equalTo(true));

        comunidadBean = new ComunidadBean(new TipoViaValueObj(0, "tipoVia1"), "avenida de las 25 ñs", "23412", "", new Municipio((short) 114));
        assertThat(comunidadBean.validateNumeroEnVia(ERROR_GENERIC, errors), equalTo(true));

        comunidadBean = new ComunidadBean(new TipoViaValueObj(0, "tipoVia1"), "avenida de las 25 ñs", "select *", "",
                new Municipio((short) 113));
        assertThat(comunidadBean.validateNumeroEnVia(ERROR_GENERIC, errors), equalTo(false));

        comunidadBean = new ComunidadBean(new TipoViaValueObj(0, ""), "", "AB", "", new Municipio((short) 115));
        assertThat(comunidadBean.validateNumeroEnVia(resources.getText(R.string.numero_en_via), errors),
                equalTo(false));
        assertThat(errors.toString(), containsString(resources.getText(R.string.numero_en_via).toString()));
    }

    @Test
    public void testValidateSufijo() throws Exception
    {
        ComunidadBean comunidadBean = new ComunidadBean(new TipoViaValueObj(0, ""), "", "", "ñÍs", new Municipio((short) 116));
        assertThat(comunidadBean.validateSufijo(TIPO_VIA_ERROR, errors), equalTo(true));
        comunidadBean = new ComunidadBean(new TipoViaValueObj(0, ""), "", "", "w 2", new Municipio((short) 117));
        assertThat(comunidadBean.validateSufijo(TIPO_VIA_ERROR, errors), equalTo(false));
    }

    @After
    public void doAfter()
    {
    }
}