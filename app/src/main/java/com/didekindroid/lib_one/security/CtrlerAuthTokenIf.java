package com.didekindroid.lib_one.security;


import com.didekindroid.lib_one.api.ControllerIf;
import com.didekindroid.lib_one.api.Viewer;

/**
 * User: pedro@didekin
 * Date: 23/01/17
 * Time: 12:14
 */
public interface CtrlerAuthTokenIf extends ControllerIf {

    boolean updateTkCacheFromRefreshTk(String refreshToken, Viewer viewer);

    void refreshAccessToken(Viewer viewer);
}
