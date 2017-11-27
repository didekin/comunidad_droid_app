package com.didekindroid.util;

import com.didekindroid.R;

import org.junit.Test;

import java.util.List;

import static com.didekindroid.AppInitializer.creator;
import static com.didekindroid.util.IoHelper.doArrayFromFile;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 19/06/15
 * Time: 20:33
 */
public class IoHelperTest {

    private static final int TIPO_VIA_FILE_SIZE = 323;

    @Test
    public void testDoArrayFromFile() throws Exception
    {
        List<String> tipos = doArrayFromFile(creator.get().getContext(), R.raw.tipos_vias);
        assertThat(tipos.size(),is(TIPO_VIA_FILE_SIZE));
        assertThat(tipos,hasItems("Acces","Galeria","Zumardi"));
    }
}