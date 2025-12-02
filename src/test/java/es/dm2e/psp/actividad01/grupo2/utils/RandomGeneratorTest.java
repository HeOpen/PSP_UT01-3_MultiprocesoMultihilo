package es.dm2e.psp.actividad01.grupo2.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RandomGeneratorTest {

    @Test
    void randomInt0() {
        int intGenerated = RandomGenerator.randomInt(0, 0);
        assertEquals(0, intGenerated);
    }

    @Test
    void randomInt1() {
        int intGenerated = RandomGenerator.randomInt(1, 1);
        assertEquals(1, intGenerated);
    }

    @Test
    void randomIntBetween0And10() {
        int intGenerated = RandomGenerator.randomInt(0, 10);
        assertTrue(intGenerated >= 0 && intGenerated <= 10);
        System.out.println(intGenerated);
    }

    @Test
    void randomFloat() {
        float floatGenerated = RandomGenerator.randomFloat(0, 1);
        assertTrue(floatGenerated >= 0 && floatGenerated <= 10);
        System.out.println(floatGenerated);
    }
}