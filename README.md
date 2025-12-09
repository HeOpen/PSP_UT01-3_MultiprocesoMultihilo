### 📦 Procesos
#### 📑 Generar fichero de transferencias
- ✅ Simulación de fallo en la generación.
- ✅ Testeado.
- ✅ Recogerá el stream de entrada.
- ✅ Testeado.
- ✅ Genera el fichero.
- ✅ Testeado.
- ✅ Finaliza con un código de éxito o de error.
- ✅ Testeado.
#### 📑 Procesar fichero de transferencias
- ✅ Recoge el stream de entrada.
- ✅ Testeado.
- ✅ Crear un monitor que represente la cuenta del banco.
- ✅ Testeado.
- ✅ Lee el fichero de transferencias anterior.
- ✅ Testeado.
- Abre 3 streams:
  1. ✅ Transferencias sin saldo.
  2. ✅ Transferencias internas.
  3. ✅ Transferencias externas.
- ✅ Testeado.
- ✅ Crear hilos para procesar las transferencias.
- ✅ Testeado.
- ✅ Esperar a que los hilos terminen.
- [ ] Testeado.
- ✅ Mostrar el importe procesado.
- ✅ Testeado.
- [ ] Mostrar el saldo restante.
- [ ] Testeado.
- [ ] Terminará.
- [ ] Testeado.
#### 📑 Hilos que procesarán las transferencias
- ✅ Se ejecutan mientras haya trasnferencias que procesar.
- [ ] Testeado.
- ✅ Procesan una transferencia por ciclo.
- [ ] Testeado.
- Se paran cuando:
  - [ ] Se hayan procesado todas las transferencias.
  - [ ] Se haya acabado el saldo.
  - [ ] Sean interrumpidos.
- [ ] Testeado.
#### 📑 Lanzamiento de los anteriores desde un tercer proceso
- ✅ Preguntará al usuario el nombre del directorio de trabajo, donde estarán los ficheros.
- ✅ Testeado.
- ✅ Preguntará al usuario el nombre del fichero con las transferencias.
- ✅ Testeado.
- ✅ Preguntará al usuario el número de transferencias a generar.
- ✅ Testeado.
- ✅ Preguntará al usuario el número de hilos que se deben usar para procesar el fichero.
- [ ] Testeado.
- ✅ Arrancará el programa que genera el fichero de transferencias.
- ✅ Testeado.
- ✅ Esperará a que este proceso termine y si falla, avisará.
- [ ] Testeado.
- ✅ Arrancará el programa que lee y procesa el fichero de transferencias.
- [ ] Testeado.
- [ ] Esperará a que este proceso termine, mostrando por consola los mensajes del proceso lanzado.
- [ ] Testeado.

# Clases que utilizó el profesor:
7 Clases
-
- BankAccount
- FileGenerator
- FileProcessor
- Main
- Transfer - Numero de cuenta y cantidad €
- TransferProcessorThread - Procesa las transferencias
- TransferQueue - Monitor de las transferencias
