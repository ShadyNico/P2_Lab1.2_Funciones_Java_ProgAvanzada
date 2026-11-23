# lab21-funciones-estudiante

Laboratorio 2.1 (Programación Avanzada, ESPE): funciones y contratos funcionales en Java 21.

- `modelo`: record `Incidencia` (prioridad 1..5, id y estado obligatorios).
- `contrato`: `ReglaIncidencia` (@FunctionalInterface) y `FabricaIncidencia`.
- `reglas`: `PrioridadAlta` (clase) y `ReglasIncidencia` (funciones con nombre propio).
- `imperativo`: solución de partida (referencia, no se modifica).
- `funcional`: `ProcesadorFuncional`, recibe Predicate/Function/Consumer como parámetros.

Ejecutar: `mvn test` y `mvn -q compile && java -cp target/classes ec.edu.espe.pa.funcional.App`.
Todos los datos son sintéticos.
