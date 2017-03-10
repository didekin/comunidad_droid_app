package com.didekindroid.api;

import com.didekindroid.security.IdentityCacher;

import timber.log.Timber;

import static com.didekindroid.security.TokenIdentityCacher.TKhandler;

/**
 * User: pedro@didekin
 * Date: 21/02/17
 * Time: 10:43
 */
@SuppressWarnings("ConstructorNotProtectedInAbstractClass")
public abstract class ControllerIdentityAbs extends ControllerAbs implements ManagerIf.ControllerIdentityIf {

    protected final IdentityCacher identityCacher;

    protected ControllerIdentityAbs()
    {
        super();
        identityCacher = TKhandler;
    }

    public ControllerIdentityAbs(IdentityCacher identityCacher)
    {
        super();
        this.identityCacher = identityCacher;
    }

    @Override
    public boolean isRegisteredUser()
    {
        Timber.d("isRegisteredUser()");
        return identityCacher.isRegisteredUser();
    }

    @Override
    public void updateIsRegistered(boolean isRegisteredUser){
        Timber.d("updateIsRegistered()");
        identityCacher.updateIsRegistered(isRegisteredUser);
    }

    @Override
    public IdentityCacher getIdentityCacher()
    {
        return identityCacher;
    }
}
