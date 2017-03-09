package com.didekindroid.usuario.login;

import android.view.View;

import com.didekindroid.ControllerIdentityAbs;
import com.didekindroid.incidencia.list.ManagerIncidSeeIf;
import com.didekindroid.security.IdentityCacher;
import com.didekinlib.model.usuario.Usuario;

import timber.log.Timber;

import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.usuario.login.ReactorLogin.loginReactor;
import static com.didekindroid.util.CommonAssertionMsg.bean_fromView_should_be_initialized;
import static com.didekindroid.util.UIutils.assertTrue;

/**
 * User: pedro@didekin
 * Date: 21/02/17
 * Time: 12:53
 */
class ControllerLogin extends ControllerIdentityAbs implements ControllerLoginIf {

    private final ReactorLoginIf reactor;
    private final ViewerLoginIf<View,Object> viewer;

    ControllerLogin(ViewerLoginIf<View,Object> viewer)
    {
        this(viewer, loginReactor, TKhandler);
    }

    @SuppressWarnings("WeakerAccess")
    ControllerLogin(ViewerLoginIf<View, Object> viewer, ReactorLoginIf reactor, IdentityCacher identityCacher)
    {
        super(identityCacher);
        this.viewer = viewer;
        this.reactor = reactor;
    }

    @Override
    public void validateLoginRemote(Usuario usuario)
    {
        Timber.i("validateLoginRemote()");
        assertTrue(usuario != null, bean_fromView_should_be_initialized);
        reactor.validateLogin(this, usuario);
    }

    @Override
    public void processBackLoginRemote(Boolean isLoginOk)
    {
        Timber.d("processBackLoginRemote()");
        viewer.processLoginBackInView(isLoginOk);
    }

    @Override
    public void doDialogPositiveClick(Usuario usuario)
    {
        Timber.d("doDialogPositiveClick()");
        assertTrue(usuario != null, bean_fromView_should_be_initialized);
        reactor.sendPasswordToUser(this, usuario);
    }

    @Override
    public void processBackDialogPositiveClick(Boolean isSendPassword)
    {
        Timber.d("processBackDialogPositiveClick()");
        viewer.processBackSendPswdInView(isSendPassword);
    }

    @Override
    public ManagerIncidSeeIf.ViewerIf getViewer()
    {
        Timber.d("getViewer()");
        return viewer;
    }
}
