package com.didekindroid.common.webservices;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;
import java.util.Arrays;

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
    public void testProviders_2(){
        Provider[] providers = Security.getProviders();
        String[] providerNames = new String[providers.length];
        for (int i = 0; i < providerNames.length; ++i){
            providerNames[i] = providers[i].getName();
        }
        Arrays.sort(providerNames);
        assertThat(Arrays.binarySearch(providerNames,"BC") >= 0, is(true));
    }

    @Test
    public void testKeyStore(){
        String keyStoreType =  KeyStore.getDefaultType();
        assertThat(keyStoreType, is("BKS"));
    }
}
