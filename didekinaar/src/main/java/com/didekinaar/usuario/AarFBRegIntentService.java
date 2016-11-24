package com.didekinaar.usuario;

import android.app.IntentService;
import android.content.Intent;

import com.didekinaar.exception.UiAarException;
import com.google.firebase.iid.FirebaseInstanceId;

import timber.log.Timber;

import static com.didekinaar.usuario.AarUsuarioService.AarUserServ;
import static com.didekinaar.utils.UIutils.isRegisteredUser;
import static com.didekinaar.utils.UIutils.updateIsGcmTokenSentServer;

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

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Timber.d("onHandleIntent()");

        if(!isRegisteredUser(this)){
            return;
        }
        try {
            String token = FirebaseInstanceId.getInstance().getToken();
                AarUserServ.modifyUserGcmToken(token);
                updateIsGcmTokenSentServer(true, this);
                Timber.i("onHandleIntent(), GCM token registered: %s%n", token);
        } catch (UiAarException e) {
            updateIsGcmTokenSentServer(false, this);
            Timber.e("onHandleIntent(), exception: %s%n", e.getErrorBean().getMessage());
        }
    }
}