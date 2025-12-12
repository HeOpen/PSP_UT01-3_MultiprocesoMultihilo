package es.dm2e.psp.actividad01.grupo2.monitores;

import java.util.Random;

public class CuentaBanco {

    private float saldo;

    public CuentaBanco(int numeroTransferencias) {
        Random random = new Random();
        this.saldo = numeroTransferencias * (1500 + random.nextInt(1200) + random.nextFloat());
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
