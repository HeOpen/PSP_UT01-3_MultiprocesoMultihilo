package es.dm2e.psp.actividad01.grupo2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {

    // ======================================================
    // =                UTILS / CONSTANTES                  =
    // ======================================================
    private static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {

        // ======================================================
        // =            INPUT DE USUARIO: DIRECTORIO            =
        // ======================================================
        String preguntaDirectorio = """
                 ---------------------------------------------
                | Introduce la ruta del directorio de trabajo |
                 ---------------------------------------------
                """;
        System.out.print(preguntaDirectorio);
        String directorio = SCANNER.nextLine().trim();

        Path directorioPath = Paths.get(directorio);

        if (Files.exists(directorioPath) && !Files.isDirectory(directorioPath)) {
            System.out.printf("La ruta para <%s> existe y no es un directorio", directorio);
            // fixme: los problemas relacionados con el sistema de archivos suelen lanzar código 3, hacerlo?? o 0 ??
            System.exit(3);
        }

        if (!Files.exists(directorioPath)) {
            try {
                Files.createDirectory(directorioPath);
            } catch (IOException e) {
                System.out.printf("Fallo al crear el directorio en <%s>", directorioPath.toAbsolutePath());
                // fixme: los problemas relacionados con el sistema de archivos suelen lanzar código 3, hacerlo?? o 0 ??
                System.exit(3);
            }
        }

        // ======================================================
        // =              INPUT DE USUARIO: FICHERO             =
        // ======================================================
        String preguntaFichero = """
                 -------------------------------------------------
                | Introduce la ruta del fichero de transferencias |
                 -------------------------------------------------
                """;
        System.out.print(preguntaFichero);
        String fichero = SCANNER.nextLine().trim();

        Path ficheroPath = Paths.get(directorio, fichero);

        if (Files.exists(ficheroPath) && !Files.isDirectory(ficheroPath)) {
            try {
                Files.delete(ficheroPath);
            } catch (IOException e) {
                System.out.printf("Fallo al eliminar el fichero existente en <%s>", ficheroPath.toAbsolutePath());
                // fixme: los problemas relacionados con el sistema de archivos suelen lanzar código 3, hacerlo?? o 0 ??
                System.exit(3);
            }
        }

        // ======================================================
        // =     INPUT DE USUARIO: NÚMERO DE TRANSFERENCIAS     =
        // ======================================================
        String preguntaTransferencias = """
                 -------------------------------------------------
                | Introduce el número de transferencias a generar |
                 -------------------------------------------------
                """;
        System.out.print(preguntaTransferencias);
        String transferencias = SCANNER.nextLine().trim();
        int nTransferencias = 0;
        boolean nTransferenciasValido = false;

        do {
            try {
                nTransferencias = Integer.parseInt(transferencias);
                if (nTransferencias <= 0) {
                    System.out.println("El número de transferencias no pueden ser menor o igual a 0\nIntroduzca un nuevo número:");
                    transferencias = SCANNER.nextLine().trim();
                } else {
                    nTransferenciasValido = true;
                }
            } catch (NumberFormatException e) {
                System.out.printf("<%s> no es un número\nIntroduzca un nuevo número:\n", transferencias);
                transferencias = SCANNER.nextLine().trim();
            }
        } while (!nTransferenciasValido);

        // ======================================================
        // =         INPUT DE USUARIO: NÚMERO DE HILOS          =
        // ======================================================
        String preguntaHilos = """
                 -------------------------------------
                | Introduce el número de hilos a usar |
                 -------------------------------------
                """;
        System.out.print(preguntaHilos);
        String hilos = SCANNER.nextLine().trim();
        int nHilos = 0;
        boolean nHilosValido = false;

        do {
            try {
                nHilos = Integer.parseInt(transferencias);
                if (nHilos <= 0) {
                    System.out.println("El número de hilos no puede ser menor o igual a 0\nIntroduzca un nuevo número:");
                    hilos = SCANNER.nextLine().trim();
                } else {
                    nHilosValido = true;
                }
            } catch (NumberFormatException e) {
                System.out.printf("<%s> no es un número\nIntroduzca un nuevo número:\n", hilos);
                hilos = SCANNER.nextLine().trim();
            }
        } while (!nHilosValido);

        // ======================================================
        // =     ARRANCAR PROCESO GENERADOR TRANSFERENCIAS      =
        // ======================================================
        ProcessBuilder procesoGenerador = new ProcessBuilder("java", "es.dm2e.psp.actividad01.grupo2.programas.GeneradorFichero");
        procesoGenerador.environment().put("JAVA_HOME", System.getProperty("java.home"));
        procesoGenerador.environment().put("CLASSPATH", System.getProperty("java.class.path"));

        try {
            Process process = procesoGenerador.start();

            try (PrintWriter pw = new PrintWriter(process.getOutputStream())) {
                pw.println(directorio);
                pw.println(fichero);
                pw.println(nTransferencias);
                pw.flush();
            }

            int exitValue = process.waitFor();
            System.out.println(exitValue);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        // ======================================================
        // =     ARRANCAR PROCESO PROCESADOR TRANSFERENCIAS     =
        // ======================================================
        ProcessBuilder procesoProcesador = new ProcessBuilder("java", "es.dm2e.psp.actividad01.grupo2.programas.ProcesadorFichero");
        procesoGenerador.environment().put("JAVA_HOME", System.getProperty("java.home"));
        procesoGenerador.environment().put("CLASSPATH", System.getProperty("java.class.path"));

        try {
            Process process = procesoProcesador.start();

            try (PrintWriter pw = new PrintWriter(process.getOutputStream());
                 BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                pw.println(directorio);
                pw.println(fichero);
                pw.println(nTransferencias);
                pw.println(nHilos);
                pw.flush();
            }

            int exitValue = process.waitFor();
            System.out.println(exitValue);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
