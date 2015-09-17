package com.didekindroid.usuario.webservices;

import com.didekin.serviceone.domain.Comunidad;
import com.didekin.serviceone.domain.Usuario;
import com.didekin.serviceone.domain.UsuarioComunidad;

import java.util.List;

/**
 * User: pedro@didekin
 * Date: 07/09/15
 * Time: 10:46
 */

/**
 * Convenience methods for registered users.
 * They delegate to a ServiceOneEndPoints method.
 */
public interface ServiceOneIf {

    boolean deleteComunidad(long comunidadId);

    boolean deleteUser();

    List<Comunidad> getComunidadesByUser();  // TODO: ¿a desaparecer?

    List<UsuarioComunidad> getUsuariosComunidad();

    Usuario getUserData();

    int modifyUser(Usuario usuario);

    boolean regComuAndUserComu(UsuarioComunidad usuarioComunidad);

    boolean regUserAndUserComu(UsuarioComunidad userCom);

    int regUserComu(UsuarioComunidad usuarioComunidad);

    List<UsuarioComunidad> seeUserComuByComu(long idComunidad);
}
