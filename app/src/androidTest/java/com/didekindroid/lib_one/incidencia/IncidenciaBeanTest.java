package com.didekindroid.lib_one.incidencia;

import android.content.res.Resources;
import android.support.test.runner.AndroidJUnit4;
import android.widget.EditText;

import com.didekindroid.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.didekindroid.lib_one.testutil.UiTestUtil.doEditTextView;
import static com.didekindroid.lib_one.util.UiUtil.getErrorMsgBuilder;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
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
    private StringBuilder errors;

    @Before
    public void doBefore()
    {
        resources = getTargetContext().getResources();
        errors = getErrorMsgBuilder(getTargetContext());
    }

    @Test
    public void testValidateBean_1()
    {
        IncidenciaBean incidenciaBean = new IncidenciaBean()
                .setCodAmbitoIncid((short) 99)
                .setDescripcion("Descripcion incidencia = test");

        assertThat(incidenciaBean.validateBean(errors, resources), is(false));
        assertThat(errors.toString(), allOf(
                containsString(resources.getText(R.string.incid_reg_descripcion).toString()),
                containsString(resources.getText(R.string.incid_reg_ambitoIncidencia).toString()),
                containsString(resources.getText(R.string.comunidad_null_in_register).toString())
                )
        );
    }

    @Test
    public void testValidateBean_2()
    {
        IncidenciaBean incidenciaBean = new IncidenciaBean()
                .setCodAmbitoIncid((short) 49)
                .setDescripcion("Descripcion incidencia ? test");

        // No tiene asociada comunidad.
        assertThat(incidenciaBean.validateBean(errors, resources), is(false));
        assertThat(errors.toString(),
                containsString(resources.getText(R.string.comunidad_null_in_register).toString())
        );
    }

    @Test
    public void testValidateBean_3()
    {
        IncidenciaBean incidenciaBean = new IncidenciaBean()
                .setCodAmbitoIncid((short) 49)
                .setComunidadId(2L)
                .setDescripcion("Descripcion incidencia ok");

        assertThat(incidenciaBean.validateBean(errors, resources), is(true));
    }

    @Test
    public void testMakeIncidenciaFromView_1() throws Exception
    {
        final EditText editText = doEditTextView(R.layout.mock_incid_desc_edit_fr, "Description valid");
        assertThat(doIncidenciaBean().makeIncidenciaFromView(editText, errors, resources), notNullValue());
    }

    @Test
    public void testMakeIncidenciaFromView_2() throws Exception
    {
        final EditText editText = doEditTextView(R.layout.mock_incid_desc_edit_fr, "No valid = ** description"); // Check.
        assertThat(doIncidenciaBean().makeIncidenciaFromView(editText, errors, resources), nullValue());

        assertThat(errors.toString(), containsString(resources.getText(R.string.incid_reg_descripcion).toString()));
    }


    private IncidenciaBean doIncidenciaBean()
    {
        return new IncidenciaBean()
                .setCodAmbitoIncid((short) 49)
                .setComunidadId(2L);
    }
}