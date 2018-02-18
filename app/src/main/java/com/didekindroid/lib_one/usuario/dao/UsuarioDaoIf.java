package com.didekindroid.lib_one.usuario.dao;

import com.didekindroid.lib_one.api.exception.UiException;
import com.didekinlib.http.auth.SpringOauthToken;
import com.didekinlib.model.usuario.Usuario;

/**
 * User: pedro@didekin
 * Date: 20/12/16
 * Time: 17:23
 */
@SuppressWarnings("WeakerAccess")
public interface UsuarioDaoIf {

    boolean deleteAccessToken(String oldAccessToken) throws UiException;

    boolean deleteUser() throws UiException;

    String getGcmToken() throws UiException;

    Usuario getUserData() throws UiException;

    boolean loginInternal(String userName, String password) throws UiException;

    int modifyUserGcmToken(String gcmToken) throws UiException;

    int modifyUserWithToken(SpringOauthToken oauthToken, Usuario usuario) throws UiException;

    int passwordChange(SpringOauthToken oldOauthToken, String newPassword) throws UiException;

    boolean sendPassword(String email) throws UiException;
}
