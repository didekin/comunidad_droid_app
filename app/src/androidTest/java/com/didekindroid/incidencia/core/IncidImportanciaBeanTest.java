package com.didekindroid.incidencia.core;

import android.content.res.Resources;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.didekindroid.R;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.incidencia.dominio.AmbitoIncidencia;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Incidencia;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.didekindroid.testutil.ActivityTestUtils.doFragmentTextView;
import static com.didekindroid.util.UIutils.getErrorMsgBuilder;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 31/03/17
 * Time: 14:18
 */
@RunWith(AndroidJUnit4.class)
public class IncidImportanciaBeanTest {

    Resources resources;
    StringBuilder errors;
    IncidImportanciaBean incidImportanciaBean;

    @Before
    public void doBefore()
    {
        resources = getTargetContext().getResources();
        errors = getErrorMsgBuilder(getTargetContext());
        incidImportanciaBean = new IncidImportanciaBean();
    }

    @Test
    public void testValidateRange() throws Exception
    {
        incidImportanciaBean.setImportancia((short) 4);
        assertThat(incidImportanciaBean.validateRange(errors, resources), is(true));

        incidImportanciaBean.setImportancia((short) 5);
        assertThat(incidImportanciaBean.validateRange(errors, resources), is(false));
    }

    @Test
    public void testMakeIncidImportancia_A() throws Exception
    {
        Incidencia incidencia = new Incidencia.IncidenciaBuilder()
                .comunidad(new Comunidad.ComunidadBuilder().c_id(11L).build())
                .incidenciaId(12L)
                .descripcion("test description")
                .ambitoIncid(new AmbitoIncidencia((short) 4))
                .build();

        IncidImportancia incidImportancia = new IncidImportancia.IncidImportanciaBuilder(incidencia).build();
        assertThat(incidImportanciaBean.makeIncidImportancia(errors, resources, incidImportancia), notNullValue());

        incidImportanciaBean.setImportancia((short) 99);
        try {
            incidImportanciaBean.makeIncidImportancia(errors, resources, incidImportancia);
            fail();
        } catch (IllegalStateException ie) {
        }
    }

    @Test
    public void makeIncidImportancia_B() throws Exception
    {
        IncidenciaBean incidenciaBean = new IncidenciaBean()
                .setCodAmbitoIncid((short) 49)
                .setComunidadId(2L);

        final View fragmentView = doFragmentTextView(R.layout.mock_incid_desc_edit_fr, "Description valid");

        incidImportanciaBean.setImportancia((short) 2);
        assertThat(incidImportanciaBean.makeIncidImportancia(errors, resources, fragmentView, incidenciaBean), notNullValue());

        incidImportanciaBean.setImportancia((short) 99);
        assertThat(incidImportanciaBean.makeIncidImportancia(errors, resources, fragmentView, incidenciaBean), nullValue());
    }

    @Test
    public void makeIncidImportancia_C() throws Exception
    {
        Incidencia incidencia = new Incidencia.IncidenciaBuilder()
                .comunidad(new Comunidad.ComunidadBuilder().c_id(11L).build())
                .incidenciaId(12L)
                .descripcion("Old description")
                .ambitoIncid(new AmbitoIncidencia((short) 4))
                .build();

        IncidenciaBean incidenciaBean = new IncidenciaBean()
                .setCodAmbitoIncid((short) 49)
                .setComunidadId(2L);

        final View fragmentView = doFragmentTextView(R.layout.mock_incid_desc_edit_fr, "Description valid");

        assertThat(incidImportanciaBean.makeIncidImportancia(errors, resources, fragmentView, incidenciaBean, incidencia), notNullValue());
        assertThat(incidImportanciaBean.makeIncidImportancia(errors, resources, fragmentView, incidenciaBean, incidencia).getIncidencia().getDescripcion(), is("Description valid"));

        incidImportanciaBean.setImportancia((short) 99);
        try {
            incidImportanciaBean.makeIncidImportancia(errors, resources, fragmentView, incidenciaBean, incidencia);
            fail();
        } catch (IllegalStateException ie) {
        }
    }
}