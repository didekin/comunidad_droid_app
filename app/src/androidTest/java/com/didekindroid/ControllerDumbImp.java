package com.didekindroid;

import com.didekindroid.usuario.firebase.ViewerFirebaseTokenIf;

/**
 * User: pedro@didekin
 * Date: 24/02/17
 * Time: 17:57
 */

public class ControllerDumbImp<P extends ManagerIf.ViewerIf> extends ControllerAbs implements ManagerIf.ControllerIf {

    private final P viewer;

    public ControllerDumbImp(P viewer)
    {
        this.viewer = viewer;
    }


    @Override
    public ViewerFirebaseTokenIf getViewer()
    {
        return viewer;
    }
}
