package es.dm2e.psp.actividad01.grupo2.monitores;

import java.util.Random;

public class CuentaBanco {

    private float saldo;

    public CuentaBanco() {
        this.saldo = 1500 + (new Random().nextInt(120_001) / 100.0f);
    }

    public synchronized float getSaldo() {
        return saldo;
    }

    public synchronized boolean transferenciaDisponible(float saldo) {
        if (this.saldo - saldo < 0) {
            return false;
        }
        this.saldo -= saldo;
        return true;
    }
}
