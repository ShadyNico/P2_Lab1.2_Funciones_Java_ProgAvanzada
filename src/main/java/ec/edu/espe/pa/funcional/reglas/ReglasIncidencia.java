package ec.edu.espe.pa.funcional.reglas;

import ec.edu.espe.pa.funcional.modelo.Incidencia;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.*;

public final class ReglasIncidencia {
    private ReglasIncidencia() { }

    public static final int UMBRAL_PRIORIDAD_ALTA = 4;

    // 1. Decide (Incidencia -> boolean): Predicate
    public static final Predicate<Incidencia> ESTA_ABIERTA =
            incidencia -> "ABIERTA".equals(incidencia.estado());

    // 2. Decide (Incidencia -> boolean): Predicate
    public static final Predicate<Incidencia> PRIORIDAD_ALTA =
            incidencia -> incidencia.prioridad() >= UMBRAL_PRIORIDAD_ALTA;

    // 3. Combina las dos reglas anteriores
    public static final Predicate<Incidencia> REQUIERE_ATENCION =
            ESTA_ABIERTA.and(PRIORIDAD_ALTA);

    // 4. Transforma (Incidencia -> String): Function
    public static final Function<Incidencia, String> RESUMEN =
            incidencia -> incidencia.id() + " - " + incidencia.titulo();

    // 5. Calcula una categoria (Incidencia -> String): Function
    public static final Function<Incidencia, String> CATEGORIA = incidencia ->
            switch (incidencia.prioridad()) {
                case 5 -> "CRITICA";
                case 4 -> "ALTA";
                case 2, 3 -> "MEDIA";
                default -> "BAJA";
            };

    // 6. Actua sin devolver valor (Incidencia -> void): Consumer
    public static final Consumer<Incidencia> NOTIFICAR =
            incidencia -> System.out.println("[ATENCION] " + RESUMEN.apply(incidencia));

    // 7. Provee datos sin recibir entrada (() -> Incidencia): Supplier
    //    Es ademas un ejemplo de funcion como valor de retorno
    public static Supplier<Incidencia> generadorSintetico() {
        AtomicInteger contador = new AtomicInteger(1);
        String[] estados = {"ABIERTA", "EN_PROCESO", "CERRADA"};
        return () -> {
            int n = contador.getAndIncrement();
            return new Incidencia("INC-%03d".formatted(n), "Incidencia sintetica " + n,
                    estados[n % estados.length], (n % 5) + 1);
        };
    }
}
