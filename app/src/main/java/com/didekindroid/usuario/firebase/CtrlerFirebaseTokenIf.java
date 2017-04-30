package com.didekindroid.usuario.firebase;


import com.didekindroid.api.ControllerIf;

/**
 * User: pedro@didekin
 * Date: 17/01/17
 * Time: 14:01
 */

public interface CtrlerFirebaseTokenIf extends ControllerIf {

    String IS_GCM_TOKEN_SENT_TO_SERVER = "isGcmTokenSentToServer";

    boolean checkGcmToken();

    boolean checkGcmTokenSync();

    boolean isGcmTokenSentServer();

    void updateIsGcmTokenSentServer(boolean isSentToServer);

}
