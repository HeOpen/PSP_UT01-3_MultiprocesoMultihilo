package es.dm2e.psp.actividad01.grupo2.programas;

import es.dm2e.psp.actividad01.grupo2.hilos.HiloProcesador;
import es.dm2e.psp.actividad01.grupo2.monitores.ColeccionTransferencias;
import es.dm2e.psp.actividad01.grupo2.monitores.CuentaBanco;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ProcesadorFichero {

    public static void main(String[] args) {

        String directorio = "";
        String fichero = "";
        int nTransferencias = 0;
        int nHilos = 0;

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            directorio = br.readLine();
            fichero = br.readLine();
            nTransferencias = Integer.parseInt(br.readLine());
            nHilos = Integer.parseInt(br.readLine());
            // No cerramos br aquí explícitamente, dejamos que el proceso muera.
        } catch (IOException e) {
            System.exit(200);
        }

        // 2. PREPARAR ENTITADES
        CuentaBanco cuentaBanco = new CuentaBanco();
        //fixme: sout para comprobar el saldo inicial
        System.out.printf("El saldo inicial es de: %.2f €\n", cuentaBanco.getSaldo());
        ColeccionTransferencias transferencias = new ColeccionTransferencias();
        Path pathFicheroTransferencias = Paths.get(directorio, fichero);

        // 3. CARGAR FICHERO EN MEMORIA
        try (BufferedReader br = new BufferedReader(new FileReader(pathFicheroTransferencias.toFile()))) {
            String transferencia;
            while ((transferencia = br.readLine()) != null) {
                transferencias.offerTransferencia(transferencia);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error leyendo fichero transferencias", e);
        }

        // 4. PROCESAR (ESCRIBIR SALIDA)
        try (PrintWriter escrituraSinSaldo = new PrintWriter(new FileWriter(pathFicheroTransferencias + ".sinsaldo", false));
             PrintWriter escrituraInternas = new PrintWriter(new FileWriter(pathFicheroTransferencias + ".internas", false));
             PrintWriter escrituraExternas = new PrintWriter(new FileWriter(pathFicheroTransferencias + ".externas", false))) {

            List<HiloProcesador> hilos = new ArrayList<>();

            System.out.println("Procesador dice: Iniciando " + nHilos + " hilos...");

            for (int i = 0; i < nHilos; i++) {
                hilos.add(new HiloProcesador(
                        "Hilo " + i, transferencias, cuentaBanco, escrituraSinSaldo, escrituraInternas, escrituraExternas
                ));
            }

            for (HiloProcesador hilo : hilos) {
                hilo.start();
            }

            // Esperar a que terminen
            for (HiloProcesador hilo : hilos) {
                try {
                    hilo.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            System.out.println("Procesador dice: Todos los hilos han terminado.\n");
        } catch (IOException e) {
            throw new RuntimeException("Error en ficheros de salida", e);
        }

        System.out.printf("El saldo final del banco es de %.2f €", cuentaBanco.getSaldo());

    }
}