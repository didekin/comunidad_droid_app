package com.didekinaar.gcm;

import android.app.Activity;
import android.content.Intent;

import com.didekinaar.usuario.AarFBRegIntentService;

import static com.didekinaar.utils.UIutils.isGcmTokenSentServer;

/**
 * User: pedro
 * Date: 04/07/15
 * Time: 17:36
 */
public final class GcmUtils {

    private GcmUtils()
    {
    }

    //    =============================== GOOGLE SERVICES =================================

    public static void getGcmToken(Activity myActivity)
    {
        if (!isGcmTokenSentServer(myActivity)) {
            myActivity.startService(new Intent(myActivity, AarFBRegIntentService.class));
        }
    }
}
