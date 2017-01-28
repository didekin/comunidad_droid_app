package com.didekindroid.security;


import com.didekinlib.http.JksInClient;

import java.io.InputStream;

import static com.didekindroid.AppInitializer.creator;

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
        return creator.get().getContext().getResources().openRawResource(jksResourceId);
    }

    @Override
    public String getJksPswd()
    {
        return jksPswd;
    }
}
