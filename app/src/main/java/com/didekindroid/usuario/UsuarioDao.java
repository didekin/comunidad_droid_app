package com.didekindroid.usuario;

import com.didekin.usuario.Usuario;
import com.didekindroid.exception.UiException;

/**
 * User: pedro@didekin
 * Date: 20/12/16
 * Time: 17:23
 */
@SuppressWarnings("WeakerAccess")
public interface UsuarioDao {

    boolean deleteAccessToken(String oldAccessToken) throws UiException;

    boolean deleteUser() throws UiException;

    String getGcmToken() throws UiException;

    Usuario getUserData() throws UiException;

    boolean loginInternal(String userName, String password) throws UiException;

    int modifyUserGcmToken(String gcmToken) throws UiException;

    int modifyUser(Usuario usuario) throws UiException;

    int passwordChange(String newPassword) throws UiException;

    boolean sendPassword(String email) throws UiException;
}
