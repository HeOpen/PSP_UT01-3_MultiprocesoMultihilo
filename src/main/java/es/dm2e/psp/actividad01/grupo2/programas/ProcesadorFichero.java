package es.dm2e.psp.actividad01.grupo2.programas;

import es.dm2e.psp.actividad01.grupo2.hilos.HiloProcesador;
import es.dm2e.psp.actividad01.grupo2.monitores.ColeccionTransferencias;
import es.dm2e.psp.actividad01.grupo2.monitores.CuentaBanco;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
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
        Path pathFicheroSinSaldo = Paths.get(directorio, fichero + ".sinsaldo");
        Path pathFicheroTransferenciasInternas = Paths.get(directorio, fichero + ".internas");
        Path pathFicheroTransferenciasExternas = Paths.get(directorio, fichero + ".externas");

        if (Files.exists(pathFicheroTransferencias)) {
            try {
                Files.deleteIfExists(pathFicheroSinSaldo);
                Files.deleteIfExists(pathFicheroTransferenciasInternas);
                Files.deleteIfExists(pathFicheroTransferenciasExternas);
                Files.createFile(pathFicheroSinSaldo);
                Files.createFile(pathFicheroTransferenciasInternas);
                Files.createFile(pathFicheroTransferenciasExternas);
            } catch (IOException e) {
                throw new RuntimeException("Error al configurar la estructura de ficheros", e);
            }
        }

        // ======================================================
        // =          CREACIÓN DE HILOS PROCESADORES            =
        // ======================================================
        List<HiloProcesador> hilos = new ArrayList<>();
        try (PrintWriter escrituraSinSaldo = new PrintWriter(pathFicheroSinSaldo.toFile());
             PrintWriter escrituraInternas = new PrintWriter(pathFicheroTransferenciasInternas.toFile());
             PrintWriter escrituraExternas = new PrintWriter(pathFicheroTransferenciasExternas.toFile());
             BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {

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

            String output;
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException("Error al encontrar los ficheros", e);
        } catch (IOException e) {
            throw new RuntimeException("Error al configurar los streams", e);
        }

    }

}
