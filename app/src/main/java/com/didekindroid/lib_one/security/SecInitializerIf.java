package com.didekindroid.lib_one.security;

import com.didekinlib.http.JksInClient;

/**
 * User: pedro@didekin
 * Date: 16/02/2018
 * Time: 10:25
 */

public interface SecInitializerIf {
    JksInClient getJksInClient();
    IdentityCacherIf getTkCacher();
}
