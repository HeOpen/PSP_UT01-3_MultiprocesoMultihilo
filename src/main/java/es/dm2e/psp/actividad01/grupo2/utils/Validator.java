package es.dm2e.psp.actividad01.grupo2.utils;

// ======================================================
// =                UTILS / CONSTANTES                  =
// ======================================================

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Validator {

    // Constructor privado para evitar instanciación (igual que en RandomGenerator)
    private Validator() {
        throw new AssertionError("No se debe instanciar esta clase!");
    }

    // Validar una ruta
    public static boolean validateRute(String rute){
        return Files.exists(Paths.get(rute));
    }

    // Validar si existe la ruta y ademas es un directorio
    public static boolean validateDirectory(String dir) {
        Path dirPath = Paths.get(dir);
        return Files.exists(dirPath) && Files.isDirectory(dirPath);
    }

    // Validar si existe la ruta y es un fichero
    public static boolean validateFile(String file) {
        Path filePath = Paths.get(file);
        return Files.exists(filePath) && Files.isRegularFile(filePath);
    }

    // Validar un número entero y que sea mayor a 0
    public static boolean validateNumber(String number) {
        try {
            // Intentamos parsear a Integer
            int result = Integer.parseInt(number);

            // Comprobamos que es mayor a 0
            if (result <= 0) {
                throw new IllegalArgumentException();
            }

        // Si no hemos podido parsearlo
        } catch (NumberFormatException e) {
            System.out.printf("<%s> no es un número\n", number);
            return false;

        // Si no es mayor a 0
        } catch (IllegalArgumentException e) {
            System.out.printf("<%s> no es un número mayor a 0\n", number);
            return false;
        }
        return true;
    }
}
