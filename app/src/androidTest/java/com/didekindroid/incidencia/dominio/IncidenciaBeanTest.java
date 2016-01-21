package com.didekindroid.incidencia.dominio;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.common.utils.UIutils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

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

    private Context context;

    @Before
    public void doBefore()
    {
        context = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void testValidateBean_1()
    {
        IncidenciaBean incidenciaBean = new IncidenciaBean().setCodAmbitoIncid((short) 99)
                .setDescripcion("Descripcion incidencia = test")
                .setImportanciaIncid((short) 11);
        StringBuilder errors = UIutils.getErrorMsgBuilder(context);
        assertThat(incidenciaBean.validateBean(errors), is(false));
        Assert.assertThat(errors.toString(), allOf(
                        containsString(context.getResources().getText(R.string.incid_reg_descripcion).toString()),
                        containsString(context.getResources().getText(R.string.incid_reg_importancia).toString()),
                        containsString(context.getResources().getText(R.string.incid_reg_ambitoIncidencia).toString()),
                        containsString(context.getResources().getText(R.string.reg_usercomu_comunidad_null).toString())
                )
        );
    }

    @Test
    public void testValidateBean_2()
    {
        IncidenciaBean incidenciaBean = new IncidenciaBean().setCodAmbitoIncid((short) 49)
                .setDescripcion("Descripcion incidencia ? test")
                .setImportanciaIncid((short) 1);
        StringBuilder errors = UIutils.getErrorMsgBuilder(context);
        // No tiene asociada comunidad.
        assertThat(incidenciaBean.validateBean(errors), is(false));
        Assert.assertThat(errors.toString(),
                        containsString(context.getResources().getText(R.string.reg_usercomu_comunidad_null).toString())
        );
    }

    @Test
    public void testValidateBean_3()
    {
        IncidenciaBean incidenciaBean = new IncidenciaBean().setCodAmbitoIncid((short) 49)
                .setComunidadId(2L)
                .setDescripcion("Descripcion incidencia ? test")
                .setImportanciaIncid((short) 1);
        StringBuilder errors = UIutils.getErrorMsgBuilder(context);
        // No tiene asociada comunidad.
        assertThat(incidenciaBean.validateBean(errors), is(true));
    }
}