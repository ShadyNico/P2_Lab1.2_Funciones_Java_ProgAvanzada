package ec.edu.espe.pa.funcional;

import ec.edu.espe.pa.funcional.modelo.Incidencia;
import ec.edu.espe.pa.funcional.contrato.ReglaIncidencia;
import ec.edu.espe.pa.funcional.reglas.PrioridadAlta;
import java.util.*;
import java.util.function.*;

public class Demostracion {

    public static void main(String[] args) {
        System.out.println("=== Fase 2: las cuatro interfaces estandar ===");
        Predicate<Incidencia> estaAbierta =
                incidencia -> "ABIERTA".equals(incidencia.estado());
        Function<Incidencia, String> obtenerResumen =
                incidencia -> incidencia.id() + " - " + incidencia.titulo();
        Consumer<Incidencia> mostrar =
                incidencia -> System.out.println(incidencia.titulo());
        Supplier<Incidencia> crearEjemplo =
                () -> new Incidencia("INC-001", "Error de acceso", "ABIERTA", 3);

        // Definir una lambda no ejecuta nada: el codigo corre al invocar el metodo abstracto
        System.out.println("(lambdas definidas, aun no se ha ejecutado ninguna)");
        Incidencia ejemplo = crearEjemplo.get();                     // Supplier -> get()
        boolean abierta = estaAbierta.test(ejemplo);                 // Predicate -> test()
        String resumen = obtenerResumen.apply(ejemplo);              // Function -> apply()
        System.out.print("Consumer.accept -> ");
        mostrar.accept(ejemplo);                                     // Consumer -> accept()
        System.out.println("Supplier.get    -> " + ejemplo);
        System.out.println("Predicate.test  -> " + abierta);
        System.out.println("Function.apply  -> " + resumen);

        // Sintaxis de lambdas
        Predicate<Incidencia> expresion = i -> i.prioridad() >= 4;
        Predicate<Incidencia> bloque = i -> {
            int p = i.prioridad();
            return p >= 4;
        };
        Predicate<Incidencia> tipada = (Incidencia i) -> i.prioridad() >= 4;
        int umbral = 4;                                  // efectivamente final
        Predicate<Incidencia> superaUmbral = i -> i.prioridad() >= umbral;
        System.out.println("Lambda de expresion/bloque/tipada/captura -> "
                + expresion.test(ejemplo) + "/" + bloque.test(ejemplo) + "/"
                + tipada.test(ejemplo) + "/" + superaUmbral.test(ejemplo));

        System.out.println();
        System.out.println("=== Fase 3: contrato propio ReglaIncidencia ===");
        // 1) Clase con nombre
        ReglaIncidencia prioridadAltaClase = new PrioridadAlta();

        // 2) Clase anonima
        ReglaIncidencia abiertaAnonima = new ReglaIncidencia() {
            @Override
            public boolean evaluar(Incidencia incidencia) {
                return "ABIERTA".equals(incidencia.estado());
            }
        };

        // 3) Expresion lambda
        ReglaIncidencia abiertaLambda = incidencia -> "ABIERTA".equals(incidencia.estado());

        // Combinacion con el metodo default y adaptacion a Predicate mediante referencia a metodo
        ReglaIncidencia requiereAtencion = abiertaLambda.y(new PrioridadAlta());
        Predicate<Incidencia> comoPredicado = requiereAtencion::evaluar;

        Incidencia alta = new Incidencia("INC-010", "Caida del servidor", "ABIERTA", 5);
        Incidencia cerrada = new Incidencia("INC-011", "Fallo de respaldo", "CERRADA", 5);
        for (Incidencia i : List.of(alta, cerrada)) {
            System.out.println(i.id() + " | clase=" + prioridadAltaClase.evaluar(i)
                    + " | anonima=" + abiertaAnonima.evaluar(i)
                    + " | lambda=" + abiertaLambda.evaluar(i)
                    + " | y(...)=" + requiereAtencion.evaluar(i)
                    + " | comoPredicado=" + comoPredicado.test(i));
        }
    }
}
