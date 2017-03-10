package com.didekindroid.incidencia.core;

import com.didekindroid.api.ManagerIf;

/**
 * User: pedro@didekin
 * Date: 17/01/17
 * Time: 14:01
 */

public interface ControllerFirebaseTokenIf extends ManagerIf.ControllerIdentityIf {

    String IS_GCM_TOKEN_SENT_TO_SERVER = "isGcmTokenSentToServer";

    void checkGcmToken();

    void checkGcmTokenSync();

    boolean isGcmTokenSentServer();

    void updateIsGcmTokenSentServer(boolean isSentToServer);

    interface FirebaseTokenReactorIf {

        boolean checkGcmToken(ControllerFirebaseTokenIf controller);

        void checkGcmTokenSync(ControllerFirebaseTokenIf controller);
    }
}
