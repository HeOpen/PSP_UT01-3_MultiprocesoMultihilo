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

        // Inputs de usuario
        String directorio = getDirectorio();
        String fichero = getFichero(directorio);
        int nTransferencias = getNumeroTransferencias();
        int nHilos = getNumeroHilos();

        // Inicio de procesos
        int exitValueGenerador = iniciarGeneradorTransferencias(directorio, fichero, nTransferencias);

        if (exitValueGenerador == 0) {
            iniciarProcesadorTransferencias(directorio, fichero, nTransferencias, nHilos);
        }
    }

    private static String getDirectorio() {
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
            System.exit(3);
        }

        if (!Files.exists(directorioPath)) {
            try {
                Files.createDirectories(directorioPath);
            } catch (IOException e) {
                System.out.printf("Fallo al crear el directorio en <%s>", directorioPath.toAbsolutePath());
                System.exit(3);
            }
        }
        return directorio;
    }

    private static String getFichero(String directorio) {
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
                System.exit(3);
            }
        }
        return fichero;
    }

    private static int getNumeroTransferencias() {
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
        return nTransferencias;
    }

    private static int getNumeroHilos() {
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
                nHilos = Integer.parseInt(hilos);
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
        return nHilos;
    }

    private static int iniciarGeneradorTransferencias(String directorio, String fichero, int nTransferencias) {
        int exitValue = 0;
        String javaBin = System.getProperty("java.home") + java.io.File.separator + "bin" + java.io.File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        String className = "es.dm2e.psp.actividad01.grupo2.programas.GeneradorFichero";

        // Construimos el comando completo
        ProcessBuilder procesoGenerador = new ProcessBuilder(javaBin, "-cp", classpath, className);

        // Heredar errores para verlos en consola si falla el hijo
        procesoGenerador.redirectError(ProcessBuilder.Redirect.INHERIT);

        try {
            System.out.println("Main -> Iniciando programa de generación de transferencias");
            Process process = procesoGenerador.start();

            try (PrintWriter pw = new PrintWriter(process.getOutputStream())) {
                System.out.println("Main -> Pasando parámetros al programa de generación de transferencias");
                pw.println(directorio);
                pw.println(fichero);
                pw.println(nTransferencias);
                pw.flush();
            }

            System.out.println("Main -> Esperando a que el programa de generación de transferencias termine");
            exitValue = process.waitFor();
            System.out.println("Main -> Generador terminó con código: " + exitValue);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return exitValue;
    }

    private static void iniciarProcesadorTransferencias(String directorio, String fichero, int nTransferencias, int nHilos) {
        String javaBin = System.getProperty("java.home") + java.io.File.separator + "bin" + java.io.File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        String className = "es.dm2e.psp.actividad01.grupo2.programas.ProcesadorFichero";

        ProcessBuilder procesoProcesador = new ProcessBuilder(javaBin, "-cp", classpath, className);
        procesoProcesador.redirectError(ProcessBuilder.Redirect.INHERIT);

        try {
            System.out.println("Main -> Iniciando programa de procesamiento de transferencias");
            Process process = procesoProcesador.start();

            try (PrintWriter pw = new PrintWriter(process.getOutputStream());
                 BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()))) {

                System.out.println("Main -> Pasando parámetros al programa de procesamiento de transferencias");
                pw.println(directorio);
                pw.println(fichero);
                pw.println(nTransferencias);
                pw.println(nHilos);
                pw.flush();

                String output;
                System.out.println("Main -> Mostrando mensajes generados por el programa que procesa el fichero");
                while ((output = br.readLine()) != null) {
                    System.out.println("Main - Procesador -> " + output);
                }
            }

            System.out.println("Main -> Esperando a que el programa de procesamiento de transferencias termine");
            int exitValue = process.waitFor();
            System.out.println("Main -> Procesador terminó con código: " + exitValue);
            if (exitValue == 0) {
                System.out.println("Main -> Terminado programa principal correctamente.");
            }

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
