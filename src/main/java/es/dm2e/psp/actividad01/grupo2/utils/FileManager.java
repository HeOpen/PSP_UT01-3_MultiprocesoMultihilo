package es.dm2e.psp.actividad01.grupo2.utils;

// ======================================================
// =                UTILS / CONSTANTES                  =
// ======================================================

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileManager {

    // Constructor privado para evitar instanciación (igual que en RandomGenerator)
    private FileManager() {
        throw new AssertionError("No se debe instanciar esta clase!");
    }

    // Crear un directorio
    public static void createDir(String path) {
        try {
            Files.createDirectories(Paths.get(path));
        } catch (IOException e) {
            System.out.printf("ERROR: no se pudo crear el directorio <%s>\n", path);
            System.exit(0);
        }
    }

    // Eliminar un fichero
    public static void deleteFile(String path) {
        try {
            Files.delete(Paths.get(path));
        } catch (IOException e) {
            System.out.printf("ERROR: no se pudo eliminar el fichero <%s>\n", path);
            System.exit(0);
        }
    }

    // Crear un fichero
    public static void createFile(String path) {
        try {
            Files.createFile(Paths.get(path));
        } catch (IOException e) {
            System.out.printf("ERROR: no se pudo crear el fichero <%s>\n", path);
            System.exit(0);
        }
    }
}
