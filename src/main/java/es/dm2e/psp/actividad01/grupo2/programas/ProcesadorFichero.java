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

        // ======================================================
        // =            RECOGIDA STREAM DE ENTRADA              =
        // ======================================================
        String directorio = "";
        String fichero = "";
        int nTransferencias = 0;
        int nHilos = 0;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {

            directorio = br.readLine();
            fichero = br.readLine();
            nTransferencias = Integer.parseInt(br.readLine());
            nHilos = Integer.parseInt(br.readLine());

        } catch (IOException e) {
            System.exit(200);
        }

        // ======================================================
        // =        CREACIÓN MONITOR: CUENTA DEL BANCO          =
        // ======================================================
        CuentaBanco cuentaBanco = new CuentaBanco();

        // ======================================================
        // =      LECTURA DEL FICHERO DE TRANSFERENCIAS         =
        // ======================================================
        ColeccionTransferencias transferencias = new ColeccionTransferencias();
        Path pathFicheroTransferencias = Paths.get(directorio, fichero);
        try (BufferedReader br = new BufferedReader(new FileReader(pathFicheroTransferencias.toFile()))) {

            String transferencia;
            while ((transferencia = br.readLine()) != null) {
                transferencias.offerTransferencia(transferencia);
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException("Fichero no encontrado", e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // ======================================================
        // =          APERTURA DE STREAMS (FICHEROS)            =
        // ======================================================
        try (PrintWriter escrituraSinSaldo = new PrintWriter(new FileWriter(pathFicheroTransferencias + ".sinsaldo", false));
             PrintWriter escrituraInternas = new PrintWriter(new FileWriter(pathFicheroTransferencias + ".internas", false));
             PrintWriter escrituraExternas = new PrintWriter(new FileWriter(pathFicheroTransferencias + ".externas", false));
             BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {

            // ======================================================
            // =          CREACIÓN DE HILOS PROCESADORES            =
            // ======================================================
            List<HiloProcesador> hilos = new ArrayList<>();

            for (int i = 0; i < nHilos; i++) {
                hilos.add(new HiloProcesador(
                        "Hilo " + i, transferencias, cuentaBanco, escrituraSinSaldo, escrituraInternas, escrituraExternas
                ));
            }

            for (HiloProcesador hilo : hilos) {
                hilo.start();
            }

            try {
                for (HiloProcesador hilo : hilos) {
                    hilo.join();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            String input;
            while ((input = br.readLine()) != null) {
                System.out.println(input);
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException("Error al escribir en los ficheros", e);
        } catch (IOException e) {
            throw new RuntimeException("Error al leer stream del hilo", e);
        }

    }

}
