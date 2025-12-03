package es.dm2e.psp.actividad01.grupo2.programas;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Random;

public class GeneradorFichero {

    // ======================================================
    // =                UTILS / CONSTANTES                  =
    // ======================================================
    private static final Random RANDOM = new Random();

    public static void main(String[] args) {

        // ======================================================
        // =                SIMULACIÓN DE FALLO                 =
        // ======================================================
        int porcentajeFallo = RANDOM.nextInt(0, 101);

        if (porcentajeFallo < 30) {
            System.exit(100);
        }

        // ======================================================
        // =            RECOGIDA STREAM DE ENTRADA              =
        // ======================================================
        String directorio = "";
        String fichero = "";
        int nTransferencias = 0;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {

            directorio = br.readLine();
            fichero = br.readLine();
            nTransferencias = Integer.parseInt(br.readLine());

        } catch (IOException e) {
            System.exit(200);
        }

        // ======================================================
        // =      GENERACIÓN DE FICHERO DE TRANSFERENCIAS       =
        // ======================================================
        String pathFichero = String.format("%s/%s", directorio, fichero);

        try {
            Files.createFile(Paths.get(pathFichero));

            try (PrintWriter pw = new PrintWriter(new FileWriter(pathFichero))) {

                for (int i = 0; i < nTransferencias; i++) {
                    int numeroCuenta = RANDOM.nextInt(100_000_000, 300_000_000);
                    float ingreso = RANDOM.nextFloat(0, 1.01f) * (3_000 - 1_500) + 1_500;
                    String transferencia = String.format(Locale.US, "%d;%.2f", numeroCuenta, ingreso);

                    pw.println(transferencia);
                }

                pw.flush();

            } catch (IOException e) {
                System.exit(200);
            }

        } catch (IOException e) {
            System.out.printf("Fallo al crear el fichero en <%s>", pathFichero);
            // fixme: los problemas relacionados con el sistema de archivos suelen lanzar código 3, hacerlo?? o 0 ??
            System.exit(3);
        }

    }
}
