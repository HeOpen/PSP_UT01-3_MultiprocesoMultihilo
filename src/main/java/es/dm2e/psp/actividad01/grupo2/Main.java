package es.dm2e.psp.actividad01.grupo2;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Main {

    // ======================================================
    // =                UTILS / CONSTANTES                  =
    // ======================================================

    private static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {

        // Creación del proceso para iniciar GeneradorFichero
        ProcessBuilder pb = new ProcessBuilder("java", "es.dm2e.psp.actividad01.grupo2.GeneradorFichero");
        pb.environment().put("JAVA_HOME", System.getProperty("java.home"));
        pb.environment().put("CLASSPATH", System.getProperty("java.class.path"));

        try {
            Process process = pb.start();

            try (PrintWriter writer = new PrintWriter(process.getOutputStream())) {

                writer.println("data");
                writer.flush();
                writer.println("file.txt");
                writer.flush();
                writer.println(5);
            }

            process.waitFor();
            System.out.println(process.exitValue());

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
