# lab21-funciones-reyes

Laboratorio 2.1 (Programación Avanzada, ESPE): funciones y contratos funcionales en Java 21.

- `modelo`: record `Incidencia` (prioridad 1..5, id y estado obligatorios).
- `contrato`: `ReglaIncidencia` (@FunctionalInterface) y `FabricaIncidencia`.
- `reglas`: `PrioridadAlta` (clase) y `ReglasIncidencia` (funciones con nombre propio).
- `imperativo`: solución de partida (referencia, no se modifica).
- `funcional`: `ProcesadorFuncional`, recibe Predicate/Function/Consumer como parámetros.

Desde la carpeta que contiene `pom.xml`, con Java 21 y Maven:

```text
mvn -v
mvn test
java -cp target/classes ec.edu.espe.pa.funcional.Demostracion
java -cp target/classes ec.edu.espe.pa.funcional.App
```
Todos los datos son sintéticos.
