package ec.edu.espe.pa.funcional;

import ec.edu.espe.pa.funcional.modelo.Incidencia;
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
    }
}
