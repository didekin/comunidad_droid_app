package com.didekindroid.lib_one.security;

import android.content.Context;
import android.content.res.Resources;

import com.didekinlib.http.JksInClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReference;

/**
 * User: pedro@didekin
 * Date: 09/02/2018
 * Time: 10:51
 */

public class SecInitializer implements SecInitializerIf {

    // Singleton.
    public static final AtomicReference<SecInitializer> secInitializer = new AtomicReference<>();

    //  ======================================================================================
    //    ................................ Instance methods ..............................
    //  ======================================================================================

    private final JksInClient jksInClient;
    private final IdentityCacherIf tkCacher;
    final Resources appResources;

    public SecInitializer(Context contextIn, int jksPswdResourceId, int jksNameResourceId)
    {
        appResources = contextIn.getResources();
        jksInClient = new JksInClientApp(
                appResources.getString(jksPswdResourceId),
                appResources.getIdentifier(appResources.getString(jksNameResourceId), "raw", contextIn.getPackageName())
        );
        tkCacher = new TokenIdentityCacher(contextIn);
    }

    @Override
    public JksInClient getJksInClient()
    {
        return jksInClient;
    }

    @Override
    public IdentityCacherIf getTkCacher()
    {
        return tkCacher;
    }

    static class JksInClientApp implements JksInClient {

        private final String jksPswd;
        private final int jksResourceId;

        JksInClientApp(String jksPswd, int jksResourceId)
        {
            this.jksPswd = jksPswd;
            this.jksResourceId = jksResourceId;
        }

        @Override
        public InputStream getInputStream() throws IOException
        {
            return secInitializer.get().appResources.openRawResource(jksResourceId);
        }

        @Override
        public String getJksPswd()
        {
            return jksPswd;
        }
    }
}
