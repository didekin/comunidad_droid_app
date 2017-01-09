package com.didekindroid.usuario;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;

import com.didekinaar.exception.UiException;
import com.google.firebase.iid.FirebaseInstanceId;

import timber.log.Timber;

import static com.didekinaar.security.TokenIdentityCacher.TKhandler;
import static com.didekinaar.usuario.UsuarioDaoRemote.usuarioDaoRemote;

/**
 * User: pedro@didekin
 * Date: 27/05/16
 * Time: 10:44
 */
public class AarFBRegIntentService extends IntentService {

    public AarFBRegIntentService()
    {
        super(AarFBRegIntentService.class.getCanonicalName());
    }

    public static void getGcmToken(Activity myActivity)
    {
        if (!TKhandler.isGcmTokenSentServer()) {
            myActivity.startService(new Intent(myActivity, AarFBRegIntentService.class));
        }
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Timber.d("onHandleIntent()");

        if(!TKhandler.isRegisteredUser()){
            return;
        }
        try {
            String token = FirebaseInstanceId.getInstance().getToken();
                usuarioDaoRemote.modifyUserGcmToken(token);
            TKhandler.updateIsGcmTokenSentServer(true);
                Timber.i("onHandleIntent(), GCM token registered: %s%n", token);
        } catch (UiException e) {
            TKhandler.updateIsGcmTokenSentServer(false);
            Timber.e("onHandleIntent(), exception: %s%n", e.getErrorBean().getMessage());
        }
    }
}