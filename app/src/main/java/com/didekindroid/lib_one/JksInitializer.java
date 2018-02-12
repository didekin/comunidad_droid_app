package com.didekindroid.lib_one;

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

public class JksInitializer {

    public static final AtomicReference<JksInitializer> jksInitializer = new AtomicReference<>();
    private final JksInClient jksInClient;
    final Resources appResources;

    public JksInitializer(Context contextIn, int jksPswdResourceId, int jksNameResourceId)
    {
        appResources = contextIn.getResources();
        jksInClient = new JksInClientApp(
                appResources.getString(jksPswdResourceId),
                appResources.getIdentifier(appResources.getString(jksNameResourceId), "raw", contextIn.getPackageName())
        );
    }

    public JksInClient getJksInClient()
    {
        return jksInClient;
    }

    static class JksInClientApp implements JksInClient{

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
            return jksInitializer.get().appResources.openRawResource(jksResourceId);
        }

        @Override
        public String getJksPswd()
        {
            return jksPswd;
        }
    }
}
