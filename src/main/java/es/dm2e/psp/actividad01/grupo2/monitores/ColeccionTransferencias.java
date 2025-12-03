package es.dm2e.psp.actividad01.grupo2.monitores;

import java.util.LinkedList;
import java.util.Queue;

public class ColeccionTransferencias {

    private final Queue<String> transferencias = new LinkedList<>();

    public synchronized void offerTransferencia(String transferencia) {
        this.transferencias.offer(transferencia);
    }

    public synchronized String pollTransferencia() {
        return this.transferencias.poll();
    }

    public synchronized String peekTransferencia() {
        return this.transferencias.peek();
    }

}
