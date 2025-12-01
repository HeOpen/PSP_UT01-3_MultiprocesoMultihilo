package es.dm2e.psp.actividad01.grupo2;

// ======================================================
// =         GENERAR FICHERO DE TRANSFERENCIAS          =
// ======================================================

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static es.dm2e.psp.actividad01.grupo2.RandomGenerator.randomFloat;
import static es.dm2e.psp.actividad01.grupo2.RandomGenerator.randomInt;

public class GeneradorFichero {

    // ======================================================
    // =                UTILS / CONSTANTES                  =
    // ======================================================

    private static final int MIN_RANDOM_GENERATION_NUMBER = 0;
    private static final int MAX_RANDOM_GENERATION_NUMBER = 100;
    private static final int NUMBER_THRESHOLD = 30;
    private static final int MIN_ACCOUNT_NUMBER = 100_000_000;
    private static final int MAX_ACCOUNT_NUMBER = 299_999_999;
    private static final int MIN_PAYROLL_AMOUNT = 1_500;
    private static final int MAX_PAYROLL_AMOUNT = 3000;

    public static void main(String[] args) {
        // Generamos un número random
        int randomFailure = randomInt(MIN_RANDOM_GENERATION_NUMBER, MAX_RANDOM_GENERATION_NUMBER);

        // Si es mayor continua su ejecución, si es menor se 'rompe' el programa
        if (isGreater(randomFailure, NUMBER_THRESHOLD)) {
            getDataFromMain();
        } else {
            System.out.println("Random failure!");
            System.exit(100);
        }
    }

    // ======================================================
    // =         SIMULACIÓN DE FALLO EN LA GENERACIÓN       =
    // ======================================================

    // Checkear si el número es mayor a otro (30 en nuestro caso)
    private static boolean isGreater(int value, int threshold) {
        return value >= threshold;
    }

    // ======================================================
    // =            RECOGER STREAM DE ENTRADA               =
    // ======================================================

    private static void getDataFromMain(){
        // Usamos BufferedReader porque es el Stream recomendado para texto.
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {

            // El path del directorio de trabajo. Se asume que es correcto, no validar.
            String directoryName = br.readLine();

            // El nombre del fichero de transferencias. Se asume que es correcto, no validar.
            String fileName = br.readLine();

            // Path completo
            Path completePath = Path.of(directoryName, fileName);

            // El número de transferencias a generar. Se asume que es correcto, convertir sin validar.
            int transferencias = Integer.parseInt(br.readLine());

            // Generar el fichero con los datos suministrados
            generateFile(Paths.get(completePath.toUri()), transferencias);

            // fixme: por defecto no se sale con status 0 ??? (Si, pero lo añado por si acaso)
            // Si no se han producido excepciones salimos con status 0
            System.exit(0);

        } catch (IOException e) {
            // Enseñamos un mensaje descriptivo del error y salimos de este programa con status 200
            System.out.println("Fallo al leer el stream enviado desde Main");
            System.exit(200);
        }
    }

    // ======================================================
    // =                GENERAR EL FICHERO                  =
    // ======================================================

    private static void generateFile(Path path, int nTransfers){
        // Usamos PrintWriter porque es el más efectivo y con más funcionalidades
        try(PrintWriter pw = new PrintWriter(Files.newBufferedWriter(path))){

            // Iteramos para generar los datos random i veces
            for(int i = 0; i < nTransfers; i++){
                pw.write(String.format("%d;%f",
                        randomInt(MIN_ACCOUNT_NUMBER, MAX_ACCOUNT_NUMBER),
                        randomFloat(MIN_PAYROLL_AMOUNT, MAX_PAYROLL_AMOUNT)));
            }

        } catch (IOException e) {
            // Enseñamos un mensaje descriptivo del error y salimos de este programa con status 200
            System.out.println("Fallo al escribir en el fichero de transferencias");
            System.exit(200);
        }
    }

}
