package com.didekindroid.incidencia.dominio;

import com.didekin.incidservice.domain.AmbitoIncidencia;
import com.didekin.incidservice.domain.Incidencia;
import com.didekin.incidservice.domain.ResolucionIncid;
import com.didekindroid.common.utils.SerialNumber;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * User: pedro@didekin
 * Date: 16/12/15
 * Time: 12:29
 */
public class IncidenciaIntent implements Serializable {

    private static final long serialVersionUID = SerialNumber.INCIDENCIA_INTENT.number;

    private final transient Incidencia incidencia;

    public IncidenciaIntent(final Incidencia incidencia)
    {
        this.incidencia = incidencia;
    }

    public Incidencia getIncidencia()
    {
        return incidencia;
    }

    private Object writeReplace()
    {
        return new InnerSerial(incidencia);
    }

    private void readObject(ObjectInputStream inputStream) throws InvalidObjectException
    {
        throw new InvalidObjectException("Use innerSerial to serialize");
    }

    private static class InnerSerial implements Serializable {

        // TODO: ResolucionId no es serializable. Hay que hacer tests de la serialización en todas las clases intent.

        private final long incidenciaId;
        private final String descripcion;
        private final AmbitoIncidencia ambitoIncidencia;
        private final Timestamp fechaAlta;
        private final Timestamp fechaCierre;
        private final ResolucionIncid resolucionIncid;

        public InnerSerial(Incidencia incidencia)
        {
            incidenciaId = incidencia.getIncidenciaId();
            descripcion = incidencia.getDescripcion();
            ambitoIncidencia = incidencia.getAmbitoIncidencia();
            fechaAlta = incidencia.getFechaAlta();
            fechaCierre = incidencia.getFechaCierre();
            resolucionIncid = incidencia.getResolucionIncid();
        }

        private Object readResolve()
        {
            Incidencia incidencia = new Incidencia.IncidenciaBuilder()
                    .incidenciaId(incidenciaId)
                    .descripcion(descripcion)
                    .ambitoIncid(ambitoIncidencia)
                    .fechaAlta(fechaAlta)
                    .fechaCierre(fechaCierre)
                    .resolucion(resolucionIncid)
                    .build();
            return new IncidenciaIntent(incidencia);
        }
    }
}
