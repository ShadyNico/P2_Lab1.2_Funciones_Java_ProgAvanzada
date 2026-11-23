package ec.edu.espe.pa.funcional;

import ec.edu.espe.pa.funcional.funcional.ProcesadorFuncional;
import ec.edu.espe.pa.funcional.imperativo.ProcesadorImperativo;
import ec.edu.espe.pa.funcional.modelo.Incidencia;
import ec.edu.espe.pa.funcional.reglas.ReglasIncidencia;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class EquivalenciaTest {

    private final ProcesadorImperativo imperativo = new ProcesadorImperativo();
    private final ProcesadorFuncional funcional = new ProcesadorFuncional();

    static Stream<List<Incidencia>> escenarios() {
        return Stream.of(
                List.of(),                                                       // lista vacia
                List.of(new Incidencia("INC-001", "Caída del servidor", "ABIERTA", 5)),
                List.of(new Incidencia("INC-004", "Lentitud en reportes", "ABIERTA", 3),   // debajo del umbral
                        new Incidencia("INC-005", "Error de acceso", "ABIERTA", 4),        // en el umbral
                        new Incidencia("INC-003", "Fallo de respaldo", "CERRADA", 5)),     // cerrada
                List.of(new Incidencia("INC-006", "Mínima", "ABIERTA", 1),
                        new Incidencia("INC-007", "En proceso", "EN_PROCESO", 4)),
                Stream.generate(ReglasIncidencia.generadorSintetico()).limit(50).toList());
    }

    @ParameterizedTest
    @MethodSource("escenarios")
    void ambasSolucionesProducenElMismoResultado(List<Incidencia> datos) {
        List<Incidencia> copia = List.copyOf(datos);

        List<String> esperado = imperativo.titulosQueRequierenAtencion(datos);
        List<String> obtenido = funcional.titulosQueRequierenAtencion(datos);

        assertEquals(esperado, obtenido);   // mismos elementos y en el mismo orden
        assertEquals(copia, datos);         // los datos originales no cambiaron
    }

    @Test
    void listaVaciaProduceListaVaciaEnAmbasVersiones() {
        assertTrue(imperativo.titulosQueRequierenAtencion(List.of()).isEmpty());
        assertTrue(funcional.titulosQueRequierenAtencion(List.of()).isEmpty());
    }

    @Test
    void elValorLimiteDePrioridad4SeIncluyeEnAmbasVersiones() {
        List<Incidencia> datos = List.of(new Incidencia("INC-005", "En el umbral", "ABIERTA", 4));
        assertEquals(List.of("En el umbral"), imperativo.titulosQueRequierenAtencion(datos));
        assertEquals(List.of("En el umbral"), funcional.titulosQueRequierenAtencion(datos));
    }

    @Test
    void seleccionarEsReutilizableConReglasNuevasSinTocarElProcesador() {
        List<Incidencia> datos = List.of(
                new Incidencia("INC-001", "A", "ABIERTA", 5),
                new Incidencia("INC-002", "B", "CERRADA", 1));
        List<String> ids = funcional.seleccionar(datos,
                ReglasIncidencia.ESTA_ABIERTA.negate(), Incidencia::id);
        assertEquals(List.of("INC-002"), ids);
    }
}
