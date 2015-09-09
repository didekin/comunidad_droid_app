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
public interface ServiceOneIf {

    boolean deleteComunidad(long comunidadId);

    boolean deleteUser();

    List<Comunidad> getComunidadesByUser();  // TODO: ¿a desaparecer?

    List<UsuarioComunidad> getUsuariosComunidad();

    Usuario getUserData();

    boolean regComuAndUserComu(UsuarioComunidad usuarioComunidad);

    boolean regComuAndUserAndUserComu(UsuarioComunidad usuarioCom);

    int regUserComu(UsuarioComunidad usuarioComunidad);

    List<Comunidad> searchComunidades(Comunidad comunidad);

    List<UsuarioComunidad> seeUserComuByComu(long idComunidad);
}
