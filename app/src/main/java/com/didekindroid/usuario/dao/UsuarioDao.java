package com.didekindroid.usuario.dao;

import com.didekindroid.exception.UiException;
import com.didekinlib.http.oauth2.SpringOauthToken;
import com.didekinlib.http.retrofit.UsuarioEndPoints;
import com.didekinlib.model.usuario.Usuario;

/**
 * User: pedro@didekin
 * Date: 20/12/16
 * Time: 17:23
 */
@SuppressWarnings("WeakerAccess")
public interface UsuarioDao {

    UsuarioEndPoints getEndPoint();

    boolean deleteAccessToken(String oldAccessToken) throws UiException;

    boolean deleteUser() throws UiException;

    String getGcmToken() throws UiException;

    Usuario getUserData() throws UiException;

    boolean loginInternal(String userName, String password) throws UiException;

    int modifyUserGcmToken(String gcmToken) throws UiException;

    int modifyUser(Usuario usuario) throws UiException;

    int modifyUserWithToken(SpringOauthToken oauthToken, Usuario usuario) throws UiException;

    int passwordChange(String newPassword) throws UiException;

    boolean sendPassword(String email) throws UiException;
}
