package com.didekindroid.usuario.login;

import android.app.Activity;
import android.os.Bundle;

import com.didekinaar.usuario.login.LoginAc;
import com.didekindroid.R;
import com.didekindroid.comunidad.ComuSearchAc;

/**
 * User: pedro@didekin
 * Date: 29/11/16
 * Time: 13:19
 */

public class LoginAppAc extends LoginAc {

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setDefaultActivityClassToGo(ComuSearchAc.class);
    }

    @Override
    protected void setDefaultActivityClassToGo(Class<? extends Activity> activityClassToGo)
    {
         defaultActivityClassToGo = activityClassToGo;
    }

    @Override
    public int getDialogThemeId()
    {
        return R.style.alertDialogTheme;
    }
}
