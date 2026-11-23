package ec.edu.espe.pa.funcional;

import ec.edu.espe.pa.funcional.funcional.ProcesadorFuncional;
import ec.edu.espe.pa.funcional.modelo.Incidencia;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.*;
import java.util.function.*;

import static ec.edu.espe.pa.funcional.reglas.ReglasIncidencia.*;
import static org.junit.jupiter.api.Assertions.*;

class ReglasIncidenciaTest {

    private final Incidencia abiertaAlta = new Incidencia("INC-001", "Caída del servidor", "ABIERTA", 5);
    private final Incidencia abiertaBaja = new Incidencia("INC-002", "Error tipográfico", "ABIERTA", 2);
    private final Incidencia cerradaAlta = new Incidencia("INC-003", "Fallo de respaldo", "CERRADA", 5);
    private final Incidencia enProcesoAlta = new Incidencia("INC-007", "Certificado", "EN_PROCESO", 4);

    @Test
    void requiereAtencionSoloSiEstaAbiertaYEsDePrioridadAlta() {
        assertTrue(REQUIERE_ATENCION.test(abiertaAlta));
        assertFalse(REQUIERE_ATENCION.test(abiertaBaja));
        assertFalse(REQUIERE_ATENCION.test(cerradaAlta));
        assertFalse(REQUIERE_ATENCION.test(enProcesoAlta));
    }

    @ParameterizedTest
    @CsvSource({"1, false", "3, false", "4, true", "5, true"})
    void prioridadAltaRespetaElUmbral(int prioridad, boolean esperado) {
        Incidencia incidencia = new Incidencia("INC-L", "Limite", "ABIERTA", prioridad);
        assertEquals(esperado, PRIORIDAD_ALTA.test(incidencia));
    }

    @ParameterizedTest
    @CsvSource({"ABIERTA, true", "EN_PROCESO, false", "CERRADA, false"})
    void estaAbiertaDistingueLosEstados(String estado, boolean esperado) {
        assertEquals(esperado, ESTA_ABIERTA.test(new Incidencia("INC-E", "Estado", estado, 3)));
    }

    @Test
    void resumenYCategoriaTransformanCorrectamente() {
        assertEquals("INC-001 - Caída del servidor", RESUMEN.apply(abiertaAlta));
        assertEquals("CRITICA", CATEGORIA.apply(abiertaAlta));
        assertEquals("INC-001 - CAÍDA DEL SERVIDOR",
                RESUMEN.andThen(String::toUpperCase).apply(abiertaAlta));
    }

    // TODO 1 de la guia: CATEGORIA para las prioridades 1, 3, 4 y 5
    @ParameterizedTest
    @CsvSource({"1, BAJA", "2, MEDIA", "3, MEDIA", "4, ALTA", "5, CRITICA"})
    void categoriaSegunPrioridad(int prioridad, String esperada) {
        Incidencia incidencia = new Incidencia("INC-C", "Categoria", "ABIERTA", prioridad);
        assertEquals(esperada, CATEGORIA.apply(incidencia));
    }

    @Test
    void composicionConNegateOrYAndThen() {
        assertTrue(ESTA_ABIERTA.negate().test(cerradaAlta));
        assertFalse(ESTA_ABIERTA.negate().test(abiertaAlta));

        Predicate<Incidencia> pendienteBaja = ESTA_ABIERTA.and(PRIORIDAD_ALTA.negate());
        assertTrue(pendienteBaja.test(abiertaBaja));
        assertFalse(pendienteBaja.test(abiertaAlta));

        Predicate<Incidencia> revisarHoy = REQUIERE_ATENCION.or(i -> i.prioridad() == 5);
        assertTrue(revisarHoy.test(cerradaAlta));
        assertFalse(revisarHoy.test(abiertaBaja));

        Function<Incidencia, String> aMayusculas = RESUMEN.andThen(String::toUpperCase);
        Function<Incidencia, String> mismoResultado = ((Function<String, String>) String::toUpperCase).compose(RESUMEN);
        assertEquals(aMayusculas.apply(abiertaAlta), mismoResultado.apply(abiertaAlta));
    }

    @Test
    void elOrdenDeComposicionPuedeCambiarElResultado() {
        Function<Integer, Integer> sumarDos = x -> x + 2;
        Function<Integer, Integer> duplicar = x -> x * 2;
        assertEquals(12, sumarDos.andThen(duplicar).apply(4));   // (4+2)*2
        assertEquals(10, sumarDos.compose(duplicar).apply(4));   // (4*2)+2
    }

    @Test
    void consumerRegistraSoloLasSeleccionadas() {
        List<String> bitacora = new ArrayList<>();
        Consumer<Incidencia> registrar = i -> bitacora.add(i.id());
        new ProcesadorFuncional().ejecutar(List.of(abiertaAlta, abiertaBaja, cerradaAlta),
                REQUIERE_ATENCION, registrar);
        assertEquals(List.of("INC-001"), bitacora);
    }

    @Test
    void consumerAndThenEjecutaAmbasAcciones() {
        List<String> bitacora = new ArrayList<>();
        Consumer<Incidencia> primera = i -> bitacora.add("A:" + i.id());
        Consumer<Incidencia> segunda = i -> bitacora.add("B:" + i.id());
        primera.andThen(segunda).accept(abiertaAlta);
        assertEquals(List.of("A:INC-001", "B:INC-001"), bitacora);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 6})
    void rechazaPrioridadesFueraDeRango(int prioridad) {
        assertThrows(IllegalArgumentException.class,
                () -> new Incidencia("INC-X", "Inválida", "ABIERTA", prioridad));
    }

    @Test
    void rechazaIdYEstadoNulos() {
        assertThrows(NullPointerException.class, () -> new Incidencia(null, "t", "ABIERTA", 3));
        assertThrows(NullPointerException.class, () -> new Incidencia("INC-X", "t", null, 3));
    }

    // TODO 2 de la guia: Supplier generadorSintetico()
    @Test
    void generadorSinteticoEntregaIdsDistintosYPrioridadValida() {
        Supplier<Incidencia> generador = generadorSintetico();
        Incidencia primera = generador.get();
        Incidencia segunda = generador.get();
        assertNotEquals(primera.id(), segunda.id());
        assertTrue(primera.prioridad() >= 1 && primera.prioridad() <= 5);
        assertTrue(segunda.prioridad() >= 1 && segunda.prioridad() <= 5);
    }

    @Test
    void cadaGeneradorReiniciaSuPropioContador() {
        assertEquals("INC-001", generadorSintetico().get().id());
        assertEquals("INC-001", generadorSintetico().get().id());
    }
}
