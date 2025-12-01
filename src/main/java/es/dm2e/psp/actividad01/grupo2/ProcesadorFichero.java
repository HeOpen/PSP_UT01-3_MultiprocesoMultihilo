package es.dm2e.psp.actividad01.grupo2;

// ======================================================
// =         PROCESAR FICHERO DE TRANSFERENCIAS         =
// ======================================================

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ProcesadorFichero {

    // ======================================================
    // =                UTILS / CONSTANTES                  =
    // ======================================================

    public static void main(String[] args) {

    }

    // ======================================================
    // =            RECOGER STREAM DE ENTRADA               =
    // ======================================================

    private void getDataFromMain() {
        // Usamos BufferedReader porque es el Stream recomendado para texto.
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {

            // Path del directorio de trabajo
            String directoryName = br.readLine();

            // Nombre del fichero con las transferencias
            String fileName = br.readLine();

            // Número de transferencias que hay que procesar (NT)
            int nt = Integer.parseInt(br.readLine());

            // Número de hilos que deben procesar el fichero (NH)
            int nh = Integer.parseInt(br.readLine());

        } catch (IOException e) {
            // fixme: imagino que se captura el error con la misma lógica que en GeneradorFichero.java
            // Enseñamos un mensaje descriptivo del error y salimos de este programa con status 200
            System.out.println("Fallo al leer el stream enviado desde Main");
            System.exit(200);
        }
    }

}
