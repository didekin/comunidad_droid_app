package com.didekindroid.usuario.firebase;


import com.didekindroid.api.CtrlerIdentityIf;

/**
 * User: pedro@didekin
 * Date: 17/01/17
 * Time: 14:01
 */

public interface CtrlerFirebaseTokenIf extends CtrlerIdentityIf {

    String IS_GCM_TOKEN_SENT_TO_SERVER = "isGcmTokenSentToServer";

    boolean checkGcmToken();

    void checkGcmTokenSync();

    boolean isGcmTokenSentServer();

    void updateIsGcmTokenSentServer(boolean isSentToServer);

}
