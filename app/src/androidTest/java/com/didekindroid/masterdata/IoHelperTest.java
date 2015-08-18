package com.didekindroid.masterdata;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.didekindroid.DidekindroidApp;
import com.didekindroid.common.IoHelper;
import com.didekindroid.usuario.comunidad.ComuSearchAc;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 19/06/15
 * Time: 20:33
 */
@RunWith(AndroidJUnit4.class)
public class IoHelperTest {

    @Test
    public void testDoArrayFromFile() throws Exception
    {
        List<String> tipos = IoHelper.doArrayFromFile(DidekindroidApp.getContext());
        assertThat(tipos.size(),is(IoHelper.TIPO_VIA_FILE_SIZE));
        assertThat(tipos,hasItems("Acces","Galeria","Zumardi"));
    }
}