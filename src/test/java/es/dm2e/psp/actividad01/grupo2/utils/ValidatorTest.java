package es.dm2e.psp.actividad01.grupo2.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidatorTest {

    @Test
    void validateRute() {
        String falseRute = "falseRutekkkk1234";
        assertTrue(Validator.validateRute("src"));
        assertFalse(Validator.validateRute(falseRute));
    }

    @Test
    void validateDirectory() {
        String falseDir = "kkkk1234";
        assertTrue(Validator.validateDirectory("src"));
        assertFalse(Validator.validateDirectory(falseDir));
    }

    @Test
    void validateFile(){
        String falseFile = "filekkkk1234";
        assertTrue(Validator.validateFile("src/main/java/es/dm2e/psp/actividad01/grupo2/Main.java"));
        assertFalse(Validator.validateFile(falseFile));
    }

    @Test
    void validateNumber(){
        assertTrue(Validator.validateNumber("1"));
        assertTrue(Validator.validateNumber("10"));
        assertFalse(Validator.validateNumber("text"));
        assertFalse(Validator.validateNumber("-2"));
        assertFalse(Validator.validateNumber("0"));
    }
}