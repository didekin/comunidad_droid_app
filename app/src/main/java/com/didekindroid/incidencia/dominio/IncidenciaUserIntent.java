package com.didekindroid.incidencia.dominio;

import com.didekin.incidservice.domain.IncidenciaUser;
import com.didekin.serviceone.domain.UsuarioComunidad;
import com.didekindroid.common.utils.SerialNumber;
import com.didekindroid.usuario.dominio.FullUsuarioComuidadIntent;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * User: pedro@didekin
 * Date: 16/12/15
 * Time: 12:29
 */
public class IncidenciaUserIntent implements Serializable {

    private static final long serialVersionUID = SerialNumber.INCID_USERCOMU_INTENT.number;

    private final transient IncidenciaUser incidenciaUser;

    public IncidenciaUserIntent(final IncidenciaUser incidenciaUser)
    {
        this.incidenciaUser = incidenciaUser;
    }

    public IncidenciaUser getIncidenciaUser()
    {
        return incidenciaUser;
    }

    private Object writeReplace()
    {
        return new InnerSerial(incidenciaUser);
    }

    private void readObject(ObjectInputStream inputStream) throws InvalidObjectException
    {
        throw new InvalidObjectException("Use innerSerial to serialize");
    }

    private static class InnerSerial implements Serializable {

        private final IncidenciaIntent incidenciaIntent;
        private final FullUsuarioComuidadIntent usuarioComunidadIntent;
        private final short importancia;
        private final Timestamp fechaAlta;

        public InnerSerial(IncidenciaUser incidenciaUser)
        {
            incidenciaIntent = new IncidenciaIntent(incidenciaUser.getIncidencia());
            usuarioComunidadIntent = new FullUsuarioComuidadIntent(
                    new UsuarioComunidad.UserComuBuilder(
                            incidenciaUser.getIncidencia().getComunidad(),
                            incidenciaUser.getUsuario()).build());
            importancia = incidenciaUser.getImportancia();
            fechaAlta = incidenciaUser.getFechaAlta();
        }

        private Object readResolve()
        {
            IncidenciaUser incidenciaUser = new IncidenciaUser.IncidenciaUserBuilder(incidenciaIntent.getIncidencia())
                    .usuario(usuarioComunidadIntent.getUsuarioComunidad().getUsuario())
                    .importancia(importancia)
                    .fechaAlta(fechaAlta)
                    .build();
            return new IncidenciaUserIntent(incidenciaUser);
        }
    }
    // TODO: hay que revisar y testar todos los intents.
}
