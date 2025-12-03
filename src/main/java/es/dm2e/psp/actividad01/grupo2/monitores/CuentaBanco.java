package es.dm2e.psp.actividad01.grupo2.monitores;

import java.util.Random;

public class CuentaBanco {

    private float saldo;
    private float saldoInicial = new Random().nextFloat() * (2_700 - 1_500) + 1_500;

    public CuentaBanco() {
        this.saldo = saldoInicial;
    }

    public synchronized float getSaldo() {
        return saldo;
    }

    public synchronized void realizarTransferencia(float saldo) {
        this.saldo -= saldo;
    }
}
