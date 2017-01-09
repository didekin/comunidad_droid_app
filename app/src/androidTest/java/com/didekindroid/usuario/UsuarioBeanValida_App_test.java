package com.didekindroid.usuario;

import android.content.res.Resources;
import android.support.test.runner.AndroidJUnit4;

import com.didekinaar.usuario.UsuarioBeanValidaTests;

import org.junit.runner.RunWith;

import static com.didekinaar.AppInitializer.creator;

/**
 * User: pedro@didekin
 * Date: 01/01/17
 * Time: 11:40
 */
@RunWith(AndroidJUnit4.class)
public class UsuarioBeanValida_App_test extends UsuarioBeanValidaTests {

    @Override
    protected Resources getResources()
    {
        return creator.get().getContext().getResources();
    }
}
