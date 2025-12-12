package es.dm2e.psp.actividad01.grupo2.hilos;

import es.dm2e.psp.actividad01.grupo2.monitores.ColeccionTransferencias;
import es.dm2e.psp.actividad01.grupo2.monitores.CuentaBanco;

import java.io.PrintWriter;

public class HiloProcesador extends Thread {

    // Importe procesado por hilo
    private float importeProcesado = 0;

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

    public String getNombre() {
        return nombre;
    }

    public float getImporteProcesado() {
        return importeProcesado;
    }

    @Override
    public void run() {
        try {
            String transferencia;
            while ((transferencia = coleccionTransferencias.pollTransferencia()) != null && !this.isInterrupted()) {

                float valorTransferencia = Float.parseFloat(transferencia.split(";")[1]);
                char tipoCuenta = transferencia.charAt(0);

                System.out.printf("%s procesando: %s\n", nombre, transferencia);

                boolean exito = cuentaBanco.transferenciaDisponible(valorTransferencia);

                if (!exito) {
                    synchronized (outSinSaldo) {
                        System.out.printf("%s: guardando transferencia %s en fichero sin saldo\n", nombre, transferencia);
                        outSinSaldo.println(transferencia);
                        outSinSaldo.flush();
                    }
                } else {
                    if (tipoCuenta == '1') {
                        synchronized (outInterna) {
                            System.out.printf("%s: guardando transferencia %s en fichero interno\n", nombre, transferencia);
                            outInterna.println(transferencia);
                            outInterna.flush();
                        }
                    } else if (tipoCuenta == '2') {
                        synchronized (outExterna) {
                            System.out.printf("%s: guardando transferencia %s en fichero externo\n", nombre, transferencia);
                            outExterna.println(transferencia);
                            outExterna.flush();
                        }
                    }
                }

                importeProcesado += valorTransferencia;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}