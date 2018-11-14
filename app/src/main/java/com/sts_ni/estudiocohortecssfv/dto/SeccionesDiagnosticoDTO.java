package com.sts_ni.estudiocohortecssfv.dto;

import java.io.Serializable;

/**
 * Created by MiguelLopez
 * Created at 18/10/2015.
 */
public class SeccionesDiagnosticoDTO implements Serializable {

    private boolean esHistorialExamenesCompletada;
    private boolean esTratamientoPlanesCompletada;
    private boolean esDiagnosticoCompletada;
    private boolean esProximaCitaCompletada;


    public boolean isEsHistorialExamenesCompletada() {
        return esHistorialExamenesCompletada;
    }

    public void setEsHistorialExamenesCompletada(boolean esHistorialExamenesCompletada) {
        this.esHistorialExamenesCompletada = esHistorialExamenesCompletada;
    }

    public boolean isEsTratamientoPlanesCompletada() {
        return esTratamientoPlanesCompletada;
    }

    public void setEsTratamientoPlanesCompletada(boolean esTratamientoPlanesCompletada) {
        this.esTratamientoPlanesCompletada = esTratamientoPlanesCompletada;
    }

    public boolean isEsDiagnosticoCompletada() {
        return esDiagnosticoCompletada;
    }

    public void setEsDiagnosticoCompletada(boolean esDiagnosticoCompletada) {
        this.esDiagnosticoCompletada = esDiagnosticoCompletada;
    }


    public boolean isEsProximaCitaCompletada() {
        return esProximaCitaCompletada;
    }

    public void setEsProximaCitaCompletada(boolean esProximaCitaCompletada) {
        this.esProximaCitaCompletada = esProximaCitaCompletada;
    }
}
