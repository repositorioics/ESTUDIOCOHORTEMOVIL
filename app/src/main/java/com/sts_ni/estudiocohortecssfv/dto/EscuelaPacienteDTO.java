package com.sts_ni.estudiocohortecssfv.dto;

/**
 * Created by alvaro on 27/04/2015.
 */
public class EscuelaPacienteDTO {

    private int codEscuela;
    private int secPaciente;
    private String descripcion;

    public int getCodEscuela() {
        return codEscuela;
    }

    public void setCodEscuela(int codEscuela) {
        this.codEscuela = codEscuela;
    }

    public int getSecPaciente() {
        return secPaciente;
    }

    public void setSecPaciente(int secPaciente) {
        this.secPaciente = secPaciente;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
