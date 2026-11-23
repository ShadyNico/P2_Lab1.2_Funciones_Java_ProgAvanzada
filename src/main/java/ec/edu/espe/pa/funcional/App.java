package ec.edu.espe.pa.funcional;

import ec.edu.espe.pa.funcional.funcional.ProcesadorFuncional;
import ec.edu.espe.pa.funcional.imperativo.ProcesadorImperativo;
import ec.edu.espe.pa.funcional.modelo.Incidencia;
import java.util.List;

import static ec.edu.espe.pa.funcional.reglas.ReglasIncidencia.*;

public class App {
    public static void main(String[] args) {
        List<Incidencia> datos = List.of(
                new Incidencia("INC-001", "Caida del servidor", "ABIERTA", 5),
                new Incidencia("INC-002", "Error tipografico en el menu", "ABIERTA", 2),
                new Incidencia("INC-003", "Fallo de respaldo nocturno", "CERRADA", 5),
                new Incidencia("INC-004", "Lentitud en reportes", "ABIERTA", 3),
                new Incidencia("INC-005", "Error de acceso al sistema", "ABIERTA", 4),
                new Incidencia("INC-006", "Actualizar certificado", "EN_PROCESO", 4));

        System.out.println("== Version imperativa ==");
        new ProcesadorImperativo().mostrarQueRequierenAtencion(datos);

        ProcesadorFuncional procesador = new ProcesadorFuncional();
        System.out.println("== Version funcional ==");
        procesador.titulosQueRequierenAtencion(datos).forEach(System.out::println);

        System.out.println("== Notificaciones ==");
        procesador.ejecutar(datos, REQUIERE_ATENCION, NOTIFICAR);

        System.out.println("== Incidencias abiertas con su categoria ==");
        procesador.seleccionar(datos, ESTA_ABIERTA,
                        i -> RESUMEN.apply(i) + " [" + CATEGORIA.apply(i) + "]")
                .forEach(System.out::println);
    }
}
