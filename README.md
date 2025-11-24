### Procesos
#### Generar fichero de transferencias
- Simulación de fallo en la generación.
- Recogerá el stream de entrada.
- Genera el fichero.
- Finaliza con un código de éxito o de error.
#### Procesar fichero de transferencias
- Recoge el stream de entrada.
- Crear un monitor que represente la cuenta del banco.
- Lee el fichero de transferencias anterior.
- Abre 3 streams:
  1. Transferencias sin saldo.
  2. Transferencias internas.
  3. Transferencias externas.
- Crear hilos para procesar las transferencias.
- Esperar a que los hilos terminen.
- Mostrar el importe procesado.
- Mostrar el saldo restante.
- Terminará.
#### Hilos que procesarán las transferencias
- Se ejecutan mientras haya trasnferencias que procesar.
- Procesan una trasnferencia por ciclo.
- Se paran cuando:
  - Se hayan procesado todas las transferencias.
  - Se haya acabado el saldo.
  - Sean interrumpidos.
#### Lanzamiento de los anteriores desde un tercer proceso
- Preguntará al usuario el nombre del directorio de trabajo, donde estarán los ficheros.
- Preguntará al usuario el nombre del fichero con las transferencias.
- Preguntará al usuario el número de transferencias a generar.
- Preguntará al usuario el número de hilos que se deben usar para procesar el fichero.
- Arrancará el programa que genera el fichero de transferencias.
- Esperará a que este proceso termine y si falla, avisará.
- Arrancará el programa que lee y procesa el fichero de transferencias.
- Esperará a que este proceso termine, mostrando por consola los mensajes del proceso lanzado.