package com.didekindroid.api;

import android.view.View;

import com.didekindroid.security.IdentityCacher;

import timber.log.Timber;

import static com.didekindroid.security.TokenIdentityCacher.TKhandler;

/**
 * User: pedro@didekin
 * Date: 21/02/17
 * Time: 10:43
 */
public class CtrlerIdentity<T extends View> extends Controller<T> implements CtrlerIdentityIf {

    protected final IdentityCacher identityCacher;

    protected CtrlerIdentity(ViewerIf<T, ? extends ControllerIf> viewer)
    {
        this(viewer, TKhandler);
    }

    public CtrlerIdentity(ViewerIf<T, ? extends ControllerIf> viewer, IdentityCacher identityCacher)
    {
        super(viewer);
        this.identityCacher = identityCacher;
    }

    @Override
    public boolean isRegisteredUser()
    {
        Timber.d("isRegisteredUser()");
        return identityCacher.isRegisteredUser();
    }

    @Override
    public void updateIsRegistered(boolean isRegisteredUser)
    {
        Timber.d("updateIsRegistered()");
        identityCacher.updateIsRegistered(isRegisteredUser);
    }

    @Override
    public IdentityCacher getIdentityCacher()
    {
        return identityCacher;
    }
}
