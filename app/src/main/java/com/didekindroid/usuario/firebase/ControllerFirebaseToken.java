package com.didekindroid.usuario.firebase;

import android.content.Context;
import android.content.SharedPreferences;

import com.didekindroid.api.ControllerIdentityAbs;
import com.didekindroid.incidencia.core.ControllerFirebaseTokenIf;
import com.didekindroid.incidencia.list.ManagerIncidSeeIf;
import com.didekindroid.security.IdentityCacher;

import timber.log.Timber;

import static android.content.Context.MODE_PRIVATE;
import static com.didekindroid.security.IdentityCacher.SharedPrefFiles.app_preferences_file;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.usuario.UsuarioAssertionMsg.user_should_be_registered;
import static com.didekindroid.usuario.firebase.FirebaseTokenReactor.tokenReactor;
import static com.didekindroid.util.UIutils.assertTrue;

/**
 * User: pedro@didekin
 * Date: 03/03/17
 * Time: 14:23
 */
@SuppressWarnings("WeakerAccess")
public class ControllerFirebaseToken extends ControllerIdentityAbs implements ControllerFirebaseTokenIf {

    private final ViewerFirebaseTokenIf viewer;
    private final FirebaseTokenReactorIf reactor;


    public ControllerFirebaseToken(ViewerFirebaseTokenIf viewer)
    {
        this(viewer, tokenReactor, TKhandler);
    }

    ControllerFirebaseToken(ViewerFirebaseTokenIf viewer, FirebaseTokenReactorIf reactor, IdentityCacher identityCacher)
    {
        super(identityCacher);
        this.viewer = viewer;
        this.reactor = reactor;
    }

    @Override
    public void checkGcmToken()
    {
        Timber.d("checkGcmToken()");
        if (!identityCacher.isRegisteredUser() || isGcmTokenSentServer()) {
            return;
        }
        reactor.checkGcmToken(this);
    }

    @Override
    public void checkGcmTokenSync()
    {
        Timber.d("checkGcmTokenSync()");
        if (identityCacher.isRegisteredUser()) {
            reactor.checkGcmTokenSync(this);
        }
    }

    @Override
    public boolean isGcmTokenSentServer()
    {
        Timber.d("isGcmTokenSentServer()");
        Context context = viewer.getManager().getActivity();
        SharedPreferences sharedPref = context.getSharedPreferences(app_preferences_file.toString(), MODE_PRIVATE);
        return sharedPref.getBoolean(IS_GCM_TOKEN_SENT_TO_SERVER, false);
    }

    @Override
    public void updateIsGcmTokenSentServer(boolean isSentToServer)
    {
        Timber.d("updateIsGcmTokenSentServer(), isSentToServer = %b", isSentToServer);
        assertTrue(isRegisteredUser(), user_should_be_registered);
        Context context = viewer.getManager().getActivity();
        SharedPreferences sharedPref = context.getSharedPreferences(app_preferences_file.toString(), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(IS_GCM_TOKEN_SENT_TO_SERVER, isSentToServer);
        editor.apply();
    }

    @Override
    public ManagerIncidSeeIf.ViewerIf getViewer()
    {
        return viewer;
    }
}
