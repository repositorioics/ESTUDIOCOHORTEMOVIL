package com.sts_ni.estudiocohortecssfv.dto;

import java.io.Serializable;

/**
 * Created by prgleandro on 2015/03/26.
 */
public class ExpedienteDTO implements Serializable {
    public String getNomMedico() {
        return nomMedico;
    }

    public void setNomMedico(String nomMedico) {
        this.nomMedico = nomMedico;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getNumHojaConsulta() {
        return numHojaConsulta;
    }

    public void setNumHojaConsulta(int numHojaConsulta) {
        this.numHojaConsulta = numHojaConsulta;
    }

    public String getFechaConsulta() {
        return fechaConsulta;
    }

    public void setFechaConsulta(String fechaConsulta) {
        this.fechaConsulta = fechaConsulta;
    }

    public String getHoraConsulta() {
        return horaConsulta;
    }

    public void setHoraConsulta(String horaConsulta) {
        this.horaConsulta = horaConsulta;
    }

    private String nomMedico;
    private String estado;
    private int numHojaConsulta;
    private String fechaConsulta;
    private String horaConsulta;
    private int secHojaConsulta;

    public int getSecHojaConsulta() {
        return secHojaConsulta;
    }

    public void setSecHojaConsulta(int secHojaConsulta) {
        this.secHojaConsulta = secHojaConsulta;
    }
}
