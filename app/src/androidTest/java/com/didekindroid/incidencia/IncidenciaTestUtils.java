package com.didekindroid.incidencia;

import com.didekin.incidservice.domain.AmbitoIncidencia;
import com.didekin.incidservice.domain.Incidencia;
import com.didekin.incidservice.domain.IncidenciaUser;
import com.didekin.serviceone.domain.Comunidad;
import com.didekin.serviceone.domain.Usuario;
import com.didekin.serviceone.domain.UsuarioComunidad;
import com.didekindroid.common.UiException;

import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 23/11/15
 * Time: 12:37
 */
public final class IncidenciaTestUtils {

    private IncidenciaTestUtils()
    {
    }

    public static Incidencia doIncidencia(String descripcion, long comunidadId, short ambitoId)
    {
        return new Incidencia.IncidenciaBuilder()
                .comunidad(new Comunidad.ComunidadBuilder().c_id(comunidadId).build())
                .descripcion(descripcion)
                .ambitoIncid(new AmbitoIncidencia(ambitoId))
                .build();
    }

    public static Incidencia doIncidenciaWithId(long incidenciaId, String descripcion, long comunidadId, short ambitoId)
    {
        return new Incidencia.IncidenciaBuilder()
                .incidenciaId(incidenciaId)
                .comunidad(new Comunidad.ComunidadBuilder().c_id(comunidadId).build())
                .descripcion(descripcion)
                .ambitoIncid(new AmbitoIncidencia(ambitoId))
                .build();
    }

    public static Incidencia insertGetIncidencia(UsuarioComunidad userComu, int importancia) throws UiException
    {
        IncidenciaUser incidUserComu = new IncidenciaUser.IncidenciaUserBuilder(doIncidencia("Incidencia One", userComu.getComunidad().getC_Id(), (short) 43))
                .usuario(userComu.getUsuario())
                .importancia((short) importancia)
                .build();
        assertThat(IncidenciaServ.regIncidenciaUser(incidUserComu), is(1));
        return IncidenciaServ.incidSeeByComu(userComu.getComunidad().getC_Id()).get(0);
    }

    public static Incidencia insertGetIncidenciaWithId(long incidenciaId, UsuarioComunidad userComu, int importancia) throws UiException
    {
        IncidenciaUser incidUserComu = new IncidenciaUser.IncidenciaUserBuilder(doIncidenciaWithId(incidenciaId, "descripcion", userComu.getComunidad().getC_Id(), (short) 43))
                .usuario(userComu.getUsuario())
                .importancia((short) importancia)
                .build();
        assertThat(IncidenciaServ.regUserInIncidencia(incidUserComu), is(1));
        return IncidenciaServ.incidSeeByComu(userComu.getComunidad().getC_Id()).get(0);
    }
}