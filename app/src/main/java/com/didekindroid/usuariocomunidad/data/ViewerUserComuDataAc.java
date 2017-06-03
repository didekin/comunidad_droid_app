package com.didekindroid.usuariocomunidad.data;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.didekindroid.api.ViewerParent;
import com.didekindroid.usuariocomunidad.register.CtrlerUserReg;

/**
 * User: pedro@didekin
 * Date: 01/06/17
 * Time: 09:27
 */

class ViewerUserComuDataAc extends ViewerParent<View, CtrlerUserReg> {  // TODO: ¿controller type?

    public ViewerUserComuDataAc(View view, Activity activity)
    {
        super(view, activity);
    }

    public void replaceComponent(@NonNull Bundle bundle)
    {
        // TODO:
    }
}
