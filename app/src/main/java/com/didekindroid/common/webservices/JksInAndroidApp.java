package com.didekindroid.common.webservices;

import com.didekin.common.controller.JksInClient;
import com.didekindroid.DidekindroidApp;

import java.io.InputStream;

/**
 * User: pedro@didekin
 * Date: 13/05/16
 * Time: 12:02
 */
public final class JksInAndroidApp implements JksInClient {

    private final String jksPswd;
    private final int jksResourceId;

    public JksInAndroidApp(String jksPswd, int jksResourceId)
    {
        this.jksPswd = jksPswd;
        this.jksResourceId = jksResourceId;
    }

    @Override
    public InputStream getInputStream()
    {
        return DidekindroidApp.getContext().getResources().openRawResource(jksResourceId);
    }

    @Override
    public String getJksPswd()
    {
        return jksPswd;
    }
}
