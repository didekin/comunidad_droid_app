package com.didekindroid.common.gcm;

import android.app.IntentService;
import android.content.Intent;

import com.didekindroid.common.activity.UiException;
import com.google.firebase.iid.FirebaseInstanceId;

import timber.log.Timber;

import static com.didekindroid.common.utils.UIutils.isRegisteredUser;
import static com.didekindroid.common.utils.UIutils.updateIsGcmTokenSentServer;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;

/**
 * User: pedro@didekin
 * Date: 27/05/16
 * Time: 10:44
 */
public class GcmRegistrationIntentService extends IntentService {

    public GcmRegistrationIntentService()
    {
        super(GcmRegistrationIntentService.class.getCanonicalName());
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Timber.d("onHandleIntent()");

        if(!isRegisteredUser(this)){
            return;
        }
        try {
            String token = FirebaseInstanceId.getInstance().getToken();
                ServOne.modifyUserGcmToken(token);
                updateIsGcmTokenSentServer(true, this);
                Timber.i("onHandleIntent(), GCM token registered: %s%n", token);
        } catch (UiException e) {
            updateIsGcmTokenSentServer(false, this);
            Timber.e("onHandleIntent(), exception: %s%n", e.getErrorBean().getMessage());
        }
    }
}