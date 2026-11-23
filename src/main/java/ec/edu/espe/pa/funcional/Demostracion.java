package ec.edu.espe.pa.funcional;

import ec.edu.espe.pa.funcional.modelo.Incidencia;
import ec.edu.espe.pa.funcional.contrato.ReglaIncidencia;
import ec.edu.espe.pa.funcional.contrato.FabricaIncidencia;
import ec.edu.espe.pa.funcional.reglas.ReglasIncidencia;
import ec.edu.espe.pa.funcional.reglas.PrioridadAlta;
import java.util.*;
import java.util.function.*;
import static ec.edu.espe.pa.funcional.reglas.ReglasIncidencia.*;

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
        ReglaIncidencia prioridadAltaAnonima = new ReglaIncidencia() {
            @Override
            public boolean evaluar(Incidencia incidencia) {
                return incidencia.prioridad() >= UMBRAL_PRIORIDAD_ALTA;
            }
        };

        // 3) Expresion lambda
        ReglaIncidencia prioridadAltaLambda = i -> i.prioridad() >= UMBRAL_PRIORIDAD_ALTA;

        // Combinacion con el metodo default y adaptacion a Predicate mediante referencia a metodo
        ReglaIncidencia reglaAbierta = i -> "ABIERTA".equals(i.estado());
        ReglaIncidencia requiereAtencion = reglaAbierta.y(prioridadAltaClase);
        Predicate<Incidencia> comoPredicado = requiereAtencion::evaluar;

        Incidencia alta = new Incidencia("INC-010", "Caida del servidor", "ABIERTA", 5);
        Incidencia cerrada = new Incidencia("INC-011", "Fallo de respaldo", "CERRADA", 5);
        Incidencia baja = new Incidencia("INC-012", "Error tipográfico", "ABIERTA", 2);
        for (Incidencia i : List.of(alta, cerrada, baja)) {
            System.out.println(i.id() + " | clase=" + prioridadAltaClase.evaluar(i)
                    + " | anonima=" + prioridadAltaAnonima.evaluar(i)
                    + " | lambda=" + prioridadAltaLambda.evaluar(i)
                    + " | y(...)=" + requiereAtencion.evaluar(i)
                    + " | comoPredicado=" + comoPredicado.test(i));
        }

        System.out.println();
        System.out.println("=== Fase 5: composicion y referencias a metodos ===");
        List<Incidencia> incidencias = List.of(alta, cerrada, baja);

        // Predicate: and, or, negate
        Predicate<Incidencia> noAbierta = ESTA_ABIERTA.negate();
        Predicate<Incidencia> pendienteBaja = ESTA_ABIERTA.and(PRIORIDAD_ALTA.negate());
        Predicate<Incidencia> revisarHoy = REQUIERE_ATENCION.or(i -> i.prioridad() == 5);
        for (Incidencia i : incidencias) {
            System.out.println(i.id() + " | noAbierta=" + noAbierta.test(i)
                    + " | pendienteBaja=" + pendienteBaja.test(i)
                    + " | revisarHoy=" + revisarHoy.test(i));
        }

        // Function: andThen (primero this, luego la otra) y compose (primero la otra, luego this)
        Function<String, String> aMayusculas = String::toUpperCase;
        Function<Incidencia, String> resumenMayusculas = RESUMEN.andThen(aMayusculas);
        Function<Incidencia, String> mismoResultado = aMayusculas.compose(RESUMEN);
        Function<Incidencia, String> etiqueta = CATEGORIA.andThen(c -> "[" + c + "]");
        System.out.println("resumenMayusculas = " + resumenMayusculas.apply(alta));
        System.out.println("mismoResultado    = " + mismoResultado.apply(alta));
        System.out.println("etiqueta          = " + etiqueta.apply(alta));

        // Consumer: andThen encadena dos acciones sobre el mismo elemento
        List<String> bitacora = new ArrayList<>();
        Consumer<Incidencia> registrar = i -> bitacora.add(i.id());
        Consumer<Incidencia> notificarYRegistrar = NOTIFICAR.andThen(registrar);
        notificarYRegistrar.accept(alta);
        System.out.println("bitacora = " + bitacora);

        // Referencias a metodos: las cuatro variantes
        Predicate<Incidencia> esCritica = ReglasIncidencia::esCritica;      // estatico
        System.out.println("-- objeto::metodo (System.out::println)");
        incidencias.forEach(System.out::println);                          // objeto particular
        Function<Incidencia, String> titulo = Incidencia::titulo;          // instancia arbitraria
        Supplier<List<Incidencia>> nuevaLista = ArrayList::new;            // constructor
        FabricaIncidencia fabrica = Incidencia::new;                       // constructor 4 parametros
        System.out.println("esCritica(alta)  = " + esCritica.test(alta));
        System.out.println("titulo(alta)     = " + titulo.apply(alta));
        System.out.println("nuevaLista       = " + nuevaLista.get());
        System.out.println("fabrica.crear    = " + fabrica.crear("INC-013", "Creada con constructor", "ABIERTA", 1));
    }
}
