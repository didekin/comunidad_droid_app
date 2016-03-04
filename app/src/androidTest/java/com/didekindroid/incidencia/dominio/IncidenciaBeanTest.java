package com.didekindroid.incidencia.dominio;

import android.content.res.Resources;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.didekindroid.common.utils.UIutils.getErrorMsgBuilder;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;

/**
 * User: pedro@didekin
 * Date: 18/11/15
 * Time: 13:06
 */
@RunWith(AndroidJUnit4.class)
public class IncidenciaBeanTest {

    private Resources resources;

    @Before
    public void doBefore()
    {
        resources = getTargetContext().getResources();
    }

    @Test
    public void testValidateBean_1()
    {
        IncidenciaBean incidenciaBean = new IncidenciaBean()
                .setCodAmbitoIncid((short) 99)
                .setDescripcion("Descripcion incidencia = test");
        StringBuilder errors = getErrorMsgBuilder(getTargetContext());
        assertThat(incidenciaBean.validateBean(errors, resources), is(false));
        Assert.assertThat(errors.toString(), allOf(
                        containsString(resources.getText(R.string.incid_reg_descripcion).toString()),
                        containsString(resources.getText(R.string.incid_reg_ambitoIncidencia).toString()),
                        containsString(resources.getText(R.string.reg_usercomu_comunidad_null).toString())
                )
        );
    }

    @Test
    public void testValidateBean_2()
    {
        IncidenciaBean incidenciaBean = new IncidenciaBean()
                .setCodAmbitoIncid((short) 49)
                .setDescripcion("Descripcion incidencia ? test");
        StringBuilder errors = getErrorMsgBuilder(getTargetContext());
        // No tiene asociada comunidad.
        assertThat(incidenciaBean.validateBean(errors, resources), is(false));
        Assert.assertThat(errors.toString(),
                        containsString(resources.getText(R.string.reg_usercomu_comunidad_null).toString())
        );
    }

    @Test
    public void testValidateBean_3()
    {
        IncidenciaBean incidenciaBean = new IncidenciaBean()
                .setCodAmbitoIncid((short) 49)
                .setComunidadId(2L)
                .setDescripcion("Descripcion incidencia ? test");
        StringBuilder errors = getErrorMsgBuilder(getTargetContext());
        // No tiene asociada comunidad.
        assertThat(incidenciaBean.validateBean(errors, resources), is(true));
    }
}