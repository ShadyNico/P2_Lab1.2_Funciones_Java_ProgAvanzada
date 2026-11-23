package ec.edu.espe.pa.funcional.contrato;

import ec.edu.espe.pa.funcional.modelo.Incidencia;

// java.util.function no ofrece una interfaz estandar con cuatro entradas
@FunctionalInterface
public interface FabricaIncidencia {
    Incidencia crear(String id, String titulo, String estado, int prioridad);
}
