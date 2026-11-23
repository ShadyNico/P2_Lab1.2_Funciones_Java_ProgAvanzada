package ec.edu.espe.pa.funcional.reglas;

import ec.edu.espe.pa.funcional.contrato.ReglaIncidencia;
import ec.edu.espe.pa.funcional.modelo.Incidencia;

public class PrioridadAlta implements ReglaIncidencia {
    @Override
    public boolean evaluar(Incidencia incidencia) {
        return incidencia.prioridad() >= 4;
    }
}
