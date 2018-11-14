package com.sts_ni.estudiocohortecssfv.dto;

/**
 * Created by flopezcarballo on 18/03/2015.
 */
public class LaboratorioDTO {


    private int secOrdenLaboratorio;
    private int secHojaConsulta;
    private int numOrdenLaboratorio;
    private String examen;
    private char estado;

    public int getSecOrdenLaboratorio() {
        return secOrdenLaboratorio;
    }

    public void setSecOrdenLaboratorio(int secOrdenLaboratorio) {
        this.secOrdenLaboratorio = secOrdenLaboratorio;
    }

    public int getNumOrdenLaboratorio() {
        return numOrdenLaboratorio;
    }

    public void setNumOrdenLaboratorio(int numOrdenLaboratorio) {
        this.numOrdenLaboratorio = numOrdenLaboratorio;
    }

    public String getExamen() {
        return examen;
    }

    public void setExamen(String examen) {
        this.examen = examen;
    }

    public char getEstado() {
        return estado;
    }

    public void setEstado(char estado) {
        this.estado = estado;
    }

    public int getSecHojaConsulta() {
        return secHojaConsulta;
    }

    public void setSecHojaConsulta(int secHojaConsulta) {
        this.secHojaConsulta = secHojaConsulta;
    }
}
