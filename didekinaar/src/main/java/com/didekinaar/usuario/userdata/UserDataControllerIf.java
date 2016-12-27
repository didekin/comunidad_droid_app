package com.didekinaar.usuario.userdata;

import com.didekin.usuario.Usuario;
import com.didekinaar.usuario.userdata.UserDataAcObservable.UserDataUpdateSubscriber;

import rx.subscriptions.CompositeSubscription;

/**
 * User: pedro@didekin
 * Date: 29/11/16
 * Time: 17:37
 */
interface UserDataControllerIf {

    void loadUserData();

    void modifyUserName();

    void modifyOnlyAlias();
}
