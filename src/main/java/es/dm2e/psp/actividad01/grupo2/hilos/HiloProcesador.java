package es.dm2e.psp.actividad01.grupo2.hilos;

import es.dm2e.psp.actividad01.grupo2.monitores.ColeccionTransferencias;
import es.dm2e.psp.actividad01.grupo2.monitores.CuentaBanco;

import java.io.PrintWriter;

public class HiloProcesador extends Thread {

    // Variable compartida para todos los hilos
    private static float totalImportes = 0;
    // Objeto candado para sincronizar la suma del static
    private static final Object LOCK_TOTAL = new Object();

    private final String nombre;
    private final ColeccionTransferencias coleccionTransferencias;
    private final CuentaBanco cuentaBanco;
    private final PrintWriter outSinSaldo;
    private final PrintWriter outInterna;
    private final PrintWriter outExterna;

    public HiloProcesador(String nombre, ColeccionTransferencias col, CuentaBanco cuenta,
                          PrintWriter outSin, PrintWriter outInt, PrintWriter outExt) {
        this.nombre = nombre;
        this.coleccionTransferencias = col;
        this.cuentaBanco = cuenta;
        this.outSinSaldo = outSin;
        this.outInterna = outInt;
        this.outExterna = outExt;
    }

    public static float getTotalImportes() {
        return totalImportes;
    }

    private void sumarImporte(float cantidad) {
        synchronized (LOCK_TOTAL) {
            totalImportes += cantidad;
        }
    }

    @Override
    public void run() {
        try {
            String transferencia;
            while ((transferencia = coleccionTransferencias.pollTransferencia()) != null && !this.isInterrupted()) {

                float valorTransferencia = Float.parseFloat(transferencia.split(";")[1]);
                char tipoCuenta = transferencia.charAt(0);

                System.out.printf("%s procesando: %s\n", nombre, transferencia);

                boolean exito = cuentaBanco.realizarTransferencia(valorTransferencia);

                if (!exito) {
                    synchronized (outSinSaldo) {
                        System.out.println("Guardando operación en fichero sin saldo");
                        outSinSaldo.println(transferencia);
                        outSinSaldo.flush();
                    }
                } else {
                    if (tipoCuenta == '1') {
                        synchronized (outInterna) {
                            System.out.println("Guardando operación en fichero interno");
                            outInterna.println(transferencia);
                            outInterna.flush();
                        }
                    } else if (tipoCuenta == '2') {
                        synchronized (outExterna) {
                            System.out.println("Guardando operación en fichero externo");
                            outExterna.println(transferencia);
                            outExterna.flush();
                        }
                    }
                }

                sumarImporte(valorTransferencia);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}