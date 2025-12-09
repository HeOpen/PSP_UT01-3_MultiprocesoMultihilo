package es.dm2e.psp.actividad01.grupo2;

import es.dm2e.psp.actividad01.grupo2.monitores.CuentaBanco;

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
        iniciarGeneradorTransferencias(directorio, fichero, nTransferencias);
        iniciarProcesadorTransferencias(directorio, fichero, nTransferencias, nHilos);
        //System.out.printf("El saldo final es de: %d€",);

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
                // fixme: los problemas relacionados con el sistema de archivos suelen lanzar código 3, hacerlo?? o 0 ??
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

    private static void iniciarGeneradorTransferencias(String directorio, String fichero, int nTransferencias) {
        // 1. Obtenemos la ruta absoluta al ejecutable de Java actual
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome + java.io.File.separator + "bin" + java.io.File.separator + "java";

        // 2. Obtenemos el Classpath para que encuentre tus clases
        String classpath = System.getProperty("java.class.path");

        // 3. Nombre completo de la clase a ejecutar
        String className = "es.dm2e.psp.actividad01.grupo2.programas.GeneradorFichero";

        // 4. Construimos el comando completo
        // Equivale a: /usr/bin/java -cp ... es.dm2e...GeneradorFichero
        ProcessBuilder procesoGenerador = new ProcessBuilder(javaBin, "-cp", classpath, className);

        // Opcional pero recomendado: Heredar errores para verlos en consola si falla el hijo
        procesoGenerador.redirectError(ProcessBuilder.Redirect.INHERIT);

        try {
            Process process = procesoGenerador.start();

            try (PrintWriter pw = new PrintWriter(process.getOutputStream())) {
                pw.println(directorio);
                pw.println(fichero);
                pw.println(nTransferencias);
                pw.flush();
            }

            int exitValue = process.waitFor();
            System.out.println("Generador terminó con código: " + exitValue);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private static void iniciarProcesadorTransferencias(String directorio, String fichero, int nTransferencias, int nHilos) {
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome + java.io.File.separator + "bin" + java.io.File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        String className = "es.dm2e.psp.actividad01.grupo2.programas.ProcesadorFichero";

        ProcessBuilder procesoProcesador = new ProcessBuilder(javaBin, "-cp", classpath, className);
        procesoProcesador.redirectError(ProcessBuilder.Redirect.INHERIT);

        try {
            Process process = procesoProcesador.start();

            try (PrintWriter pw = new PrintWriter(process.getOutputStream());
                 BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()))) {

                // Pasar parámetros
                pw.println(directorio);
                pw.println(fichero);
                pw.println(nTransferencias);
                pw.println(nHilos);
                pw.flush();

                // Leer salida
                String output;
                while ((output = br.readLine()) != null) {
                    System.out.println(output);
                }
            }

            int exitValue = process.waitFor();
            System.out.println("Procesador terminó con código: " + exitValue);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
