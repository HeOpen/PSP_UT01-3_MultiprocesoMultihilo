package es.dm2e.psp.actividad01.grupo2.hilos;

import es.dm2e.psp.actividad01.grupo2.monitores.ColeccionTransferencias;
import es.dm2e.psp.actividad01.grupo2.monitores.CuentaBanco;

import java.io.PrintWriter;

public class HiloProcesador extends Thread {

    private static float totalImportes = 0;

    private final String nombre;
    private final ColeccionTransferencias coleccionTransferencias;
    private final CuentaBanco cuentaBanco;
    private final PrintWriter outSinSaldo;
    private final PrintWriter outInterna;
    private final PrintWriter outExterna;

    public HiloProcesador(
            String nombre,
            ColeccionTransferencias coleccionTransferencias, CuentaBanco cuentaBanco,
            PrintWriter outSinSaldo, PrintWriter outInterna, PrintWriter outExterna) {
        this.nombre = nombre;
        this.coleccionTransferencias = coleccionTransferencias;
        this.cuentaBanco = cuentaBanco;
        this.outSinSaldo = outSinSaldo;
        this.outInterna = outInterna;
        this.outExterna = outExterna;
    }

    public float getTotalImportes() {
        return totalImportes;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public void run() {
        try {
            while (coleccionTransferencias.peekTransferencia() != null && !this.isInterrupted()) {
                String transferencia = coleccionTransferencias.pollTransferencia();
                float valorTransferencia = Float.parseFloat(transferencia.split(";")[1]);
                System.out.printf("Se procede a realizar transferencia: %s\n", transferencia);
                System.out.printf("Saldo de la cuenta del banco: %f\n", cuentaBanco.getSaldo());
                if (!cuentaBanco.realizarTransferencia(valorTransferencia)) {
                    System.out.println("Guardando operación en fichero sin saldo");
                    outSinSaldo.println(transferencia);
                    totalImportes += valorTransferencia;
                } else {
                    cuentaBanco.realizarTransferencia(valorTransferencia);
                    switch (transferencia.charAt(0)) {
                        case '1':
                            System.out.println("Guardando operación en fichero interno");
                            outInterna.println(transferencia);
                            totalImportes += valorTransferencia;
                            break;
                        case '2':
                            System.out.println("Guardando operación en fichero externo");
                            outExterna.println(transferencia);
                            totalImportes += valorTransferencia;
                            break;
                    }
                }

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
