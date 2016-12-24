package com.didekindroid.usuario.delete;

import android.app.Activity;
import android.os.Bundle;

import com.didekinaar.usuario.delete.DeleteMeAc;
import com.didekindroid.comunidad.ComuSearchAc;

public class DeleteMeAppAc extends DeleteMeAc {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setDefaultActivityClassToGo(ComuSearchAc.class);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setDefaultActivityClassToGo(Class<? extends Activity> activityClassToGo)
    {
        defaultActivityClassToGo = activityClassToGo;
    }
}
