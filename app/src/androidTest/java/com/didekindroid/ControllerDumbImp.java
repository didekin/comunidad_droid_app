package com.didekindroid;

import com.didekindroid.ControllerAbs;
import com.didekindroid.ManagerIf;

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
    public P getViewer()
    {
        return viewer;
    }
}
