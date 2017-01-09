package com.didekinaar.utils;

import android.content.Context;

import com.didekinaar.R;

import org.junit.Test;

import java.util.List;

import static com.didekinaar.utils.IoHelper.doArrayFromFile;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 19/06/15
 * Time: 20:33
 */
public abstract class IoHelperTest {

    private static final int TIPO_VIA_FILE_SIZE = 323;

    protected abstract Context getContext();

    @Test
    public void testDoArrayFromFile() throws Exception
    {
        List<String> tipos = doArrayFromFile(getContext(), R.raw.tipos_vias);
        assertThat(tipos.size(),is(TIPO_VIA_FILE_SIZE));
        assertThat(tipos,hasItems("Acces","Galeria","Zumardi"));
    }
}