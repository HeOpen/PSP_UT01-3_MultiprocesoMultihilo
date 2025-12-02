package es.dm2e.psp.actividad01.grupo2;

// ======================================================
// =                UTILS / CONSTANTES                  =
// ======================================================

import java.util.Random;

public class RandomGenerator {

    // Random
    private static final Random RANDOM = new Random();
    // Constructor privado para evitar instanciación
    private RandomGenerator() {
        throw new AssertionError("No se debe instanciar esta clase!");
    }

    // Generar número random
    public static int randomInt(int min, int max) {
        return RANDOM.nextInt(min, max + 1);
    }

    public static float randomFloat(float min, float max) {
        float valor = min + RANDOM.nextFloat() * (max - min);
        return Math.round(valor * 100f) / 100f;
    }
}
