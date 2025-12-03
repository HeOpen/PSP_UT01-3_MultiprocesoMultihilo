package es.dm2e.psp.actividad01.grupo2.programas;

import es.dm2e.psp.actividad01.grupo2.monitores.ColeccionTransferencias;
import es.dm2e.psp.actividad01.grupo2.monitores.CuentaBanco;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        try(BufferedReader br = new BufferedReader(new FileReader(pathFicheroTransferencias.toFile()))) {

            transferencias.addTransferencia(br.readLine());

        } catch (FileNotFoundException e) {
            throw new RuntimeException("Fichero no encontrado", e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
