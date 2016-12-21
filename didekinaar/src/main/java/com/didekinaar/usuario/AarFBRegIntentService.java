package com.didekinaar.usuario;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;

import com.didekinaar.exception.UiException;
import com.google.firebase.iid.FirebaseInstanceId;

import timber.log.Timber;

import static com.didekinaar.usuario.UsuarioDaoRemote.usuarioDaoRemote;
import static com.didekinaar.utils.UIutils.isGcmTokenSentServer;
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

    public static void getGcmToken(Activity myActivity)
    {
        if (!isGcmTokenSentServer(myActivity)) {
            myActivity.startService(new Intent(myActivity, AarFBRegIntentService.class));
        }
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
                usuarioDaoRemote.modifyUserGcmToken(token);
                updateIsGcmTokenSentServer(true, this);
                Timber.i("onHandleIntent(), GCM token registered: %s%n", token);
        } catch (UiException e) {
            updateIsGcmTokenSentServer(false, this);
            Timber.e("onHandleIntent(), exception: %s%n", e.getErrorBean().getMessage());
        }
    }
}