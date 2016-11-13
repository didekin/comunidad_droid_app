package com.didekindroid.common.utils;

import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.DidekinApp;
import com.didekindroid.R;

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

    public static final int TIPO_VIA_FILE_SIZE = 323;

    @Test
    public void testDoArrayFromFile() throws Exception
    {
        List<String> tipos = IoHelper.doArrayFromFile(DidekinApp.getContext(), R.raw.tipos_vias);
        assertThat(tipos.size(),is(TIPO_VIA_FILE_SIZE));
        assertThat(tipos,hasItems("Acces","Galeria","Zumardi"));
    }
}