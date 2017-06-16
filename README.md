# Para utilizar el sistema de horarios se debe realizar los siguientes pasos.


Indicar al servidor server.jar los path's de los tres ficheros que necesita
para cargar los horarios.

Student con los siguientes campos formato CSV

### DNI Name Apellido LOCATION EXPIRED


* Cuando se lea el campo expired si la fecha ya ha expirado el servidor
* dara un warning para que se actualice la fecha de expiracion de ese
* estudiante.


Groups con los siguientes campos formato CSV.

### IDGRUPO ASIGNATURA PROFESOR AULA PLANTA DIA HBEGIN MBEGIN HEND MEND


* Solo se chequeara que el dia este bien escrito las horas deberan estar
* bien escritas por el administrador, HBEGIN MEBGIN < HEND MEND y las horas
* deberan ir entre las 00 : 00 a las 24 : 00


Times relacion entre el estudiantes y los grupos a los que esta inscrito.

DNI GROUPID


* Se debera poner un DNI y un GROUPID existente, si no el resultado del 
* sistema es completamente inesperado.



Instalacion.

server.jar debera tener la libreria structures.jar y messages.jar

El proyecto en android del cliente deberá tener las librerias structures.jar 
y messages.jar para que funcione correctamente.


# Tests

### Test de protocolo

Para realizar el test del protocolo se debe iniciar el servidor y lanzar
el test desde el cliente.

### All Tests and UI Test

Los demas test se lanzan desde el cliente y prueban tanto la BBDD como
la app UI.

# TimeTable
