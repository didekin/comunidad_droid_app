package com.didekindroid.usuario.delete;

import android.view.View;

import com.didekindroid.ControllerIdentityAbs;
import com.didekindroid.ManagerIf.ViewerIf;
import com.didekindroid.security.IdentityCacher;

import timber.log.Timber;

import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.usuario.UsuarioAssertionMsg.user_should_have_been_deleted;
import static com.didekindroid.usuario.delete.ReactorDeleteMe.deleteReactor;
import static com.didekindroid.util.UIutils.assertTrue;

/**
 * User: pedro@didekin
 * Date: 21/02/17
 * Time: 12:53
 */
class ControllerDeleteMe extends ControllerIdentityAbs implements ControllerDeleteMeIf {

    private final ReactorDeleteMeIf reactor;
    private final ViewerIf<View, Object> viewer;

    ControllerDeleteMe(ViewerIf<View, Object> viewer)
    {
        this(viewer, deleteReactor, TKhandler);
    }

    @SuppressWarnings("WeakerAccess")
    ControllerDeleteMe(ViewerIf<View, Object> viewer, ReactorDeleteMeIf reactor, IdentityCacher identityCacher)
    {
        super(identityCacher);
        this.reactor = reactor;
        this.viewer = viewer;
    }

    @Override
    public boolean unregisterUser()
    {
        Timber.d("unregisterUser()");
        return reactor.deleteMeInRemote(this);
    }

    @Override
    public void processBackDeleteMeRemote(boolean isDeleted)
    {
        Timber.d("processBackDeleteMeRemote()");
        assertTrue(isDeleted, user_should_have_been_deleted);
        viewer.getManager().replaceRootView(null);
    }

    @Override
    public ViewerIf getViewer()
    {
        Timber.d("getViewer()");
        return viewer;
    }
}
