package com.didekindroid.usuario;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;

import com.didekindroid.exception.UiException;
import com.google.firebase.iid.FirebaseInstanceId;

import timber.log.Timber;

import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.usuario.UsuarioDaoRemote.usuarioDaoRemote;

/**
 * User: pedro@didekin
 * Date: 27/05/16
 * Time: 10:44
 */
public class RegGcmIntentService extends IntentService {

    public RegGcmIntentService()
    {
        super(RegGcmIntentService.class.getCanonicalName());
    }

    public static void getGcmToken(Activity myActivity)
    {
        if (!TKhandler.isGcmTokenSentServer()) {
            myActivity.startService(new Intent(myActivity, RegGcmIntentService.class));
        }
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Timber.d("onHandleIntent()");

        if (!TKhandler.isRegisteredUser()) {
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
