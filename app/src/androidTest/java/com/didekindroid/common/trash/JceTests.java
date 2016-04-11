package com.didekindroid.common.trash;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 06/04/16
 * Time: 20:15
 */
@RunWith(AndroidJUnit4.class)
public class JceTests {

    @Test
    public void testProviders(){
        Provider[] providers = Security.getProviders();
        assertThat(providers[0].getName(), is("AndroidOpenSSL"));
    }

    @Test
    public void testKeyStore(){
        String keyStoreType =  KeyStore.getDefaultType();
        assertThat(keyStoreType, is("BKS"));
    }
}
