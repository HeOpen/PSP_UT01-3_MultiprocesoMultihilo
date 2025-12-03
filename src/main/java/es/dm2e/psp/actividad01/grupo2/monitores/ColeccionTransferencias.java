package es.dm2e.psp.actividad01.grupo2.monitores;

import java.util.LinkedList;
import java.util.Queue;

public class ColeccionTransferencias {

    private Queue<String> transferencias = new LinkedList<>();

    public synchronized void addTransferencia(String transferencia) {
        this.transferencias.offer(transferencia);
    }

    public synchronized void removeTransferencia() {
        this.transferencias.poll();
    }

}
