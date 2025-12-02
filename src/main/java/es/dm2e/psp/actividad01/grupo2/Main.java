package es.dm2e.psp.actividad01.grupo2;

import es.dm2e.psp.actividad01.grupo2.utils.FileManager;
import es.dm2e.psp.actividad01.grupo2.utils.Validator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Scanner;

public class Main {

    // ======================================================
    // =                UTILS / CONSTANTES                  =
    // ======================================================
    private static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {

        // ======================================================
        // =          RECOGIDA DE INPUTS DE USUARIO             =
        // ======================================================
        String workingDirectory = askForWorkingDirectory();
        String workingFile = askForWorkingFile(workingDirectory);
        int numTransfers = askForNumTransfers();


        // ======================================================
        // =          LANZAMIENTO DE PROGRAMAS HIJO             =
        // ======================================================
        launchTransferGenerator(workingDirectory, workingFile, numTransfers);

    }

    // ======================================================
    // =          MÉTODOS AUXILIARES PARA MAIN              =
    // ======================================================

    // Método de comprobaciones y para devolver el directorio de trabajo
    private static String askForWorkingDirectory() {
        String askText = """
                 ---------------------------------------------
                | Introduce la ruta del directorio de trabajo |
                 ---------------------------------------------
                """;

        System.out.println(askText);
        String workingDirectory = SCANNER.nextLine();

        // Comprobar que la ruta existe
        if (Validator.validateRute(workingDirectory)) {

            // Comprobar si es un fichero
            if (Validator.validateFile(workingDirectory)) {
                System.out.printf("ERROR: <%s> no es un directorio, es un archivo\nTerminando programa...\n", workingDirectory);
                System.exit(0);
            }

            // Si no existe entonces creamos un directorio en ella
        } else {
            FileManager.createDir(workingDirectory);
        }
        return workingDirectory;
    }

    // Método de comprobaciones y para devolver el archivo de transferencias
    private static String askForWorkingFile(String dir) {
        String askText = """
                 -------------------------------------------------
                | Introduce la ruta del fichero de transferencias |
                 -------------------------------------------------
                """;

        System.out.println(askText);
        String workingFile = String.format("%s/%s", dir, SCANNER.nextLine());

        // Validar que es un archivo existente
        if (Validator.validateFile(workingFile)) {
            FileManager.deleteFile(workingFile);
        } else {
            FileManager.createFile(workingFile);
        }

        return workingFile;
    }

    // Método para recoger el número de transferencias que deben generarse
    private static int askForNumTransfers() {
        String askText = """
                 -------------------------------------------------
                | Introduce el número de transferencias a generar |
                 -------------------------------------------------
                """;

        // Validar que es un input valido
        String numTransfers;
        do {
            System.out.println(askText);
            numTransfers = SCANNER.nextLine();
        } while (!Validator.validateNumber(numTransfers));

        return Integer.parseInt(numTransfers);
    }

    private static void launchTransferGenerator(String workingDirectory, String workingFile, int numTransfers) {

        // Creación del proceso para iniciar GeneradorFichero
        ProcessBuilder pb = new ProcessBuilder("java", "es.dm2e.psp.actividad01.grupo2.GeneradorFichero");
        pb.environment().put("JAVA_HOME", System.getProperty("java.home"));
        pb.environment().put("CLASSPATH", System.getProperty("java.class.path"));

        try {
            Process process = pb.start();

            try (PrintWriter writer = new PrintWriter(process.getOutputStream());
                 BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {

                writer.println(workingDirectory);
                writer.flush();
                writer.println(workingFile);
                writer.flush();
                writer.println(numTransfers);
            }

            process.waitFor();
            System.out.println(process.exitValue());

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
