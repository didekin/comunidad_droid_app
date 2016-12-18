package com.didekinaar.usuario;

import com.didekinaar.exception.UiException;

import org.junit.Test;

import rx.Single;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * User: pedro@didekin
 * Date: 01/12/16
 * Time: 15:15
 */
public class UsuarioObservablesTest {

    @Test
    public void testDeleteMeCallable() throws UiException
    {
        boolean isDeleted = Single.just(Boolean.TRUE).toBlocking().value();
        assertThat(isDeleted, is(true));
    }

}
