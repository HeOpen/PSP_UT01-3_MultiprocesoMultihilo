package es.dm2e.psp.actividad01.grupo2;

// ======================================================
// =        REPRESENTACIÓN DE LA CUENTA DEL BANCO       =
// ======================================================

import static es.dm2e.psp.actividad01.grupo2.utils.RandomGenerator.randomFloat;

public class MonitorCuentaBanco {

    // ======================================================
    // =                UTILS / CONSTANTES                  =
    // ======================================================

    private static final float MIN_STARTING_BALANCE = 1_500f;
    private static final float MAX_STARTING_BALANCE = 2700f;

    // Recurso compartido
    private final float startingBalance = randomFloat(MIN_STARTING_BALANCE, MAX_STARTING_BALANCE);
    private float balance = startingBalance;

}
