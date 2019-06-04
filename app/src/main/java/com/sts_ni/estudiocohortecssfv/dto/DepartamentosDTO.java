package com.sts_ni.estudiocohortecssfv.dto;

public class DepartamentosDTO {
    private int divisionpoliticaId;
    private String nombre;
    private int codigoNacional;
    private String codigoIso;

    public int getDivisionpoliticaId() {
        return divisionpoliticaId;
    }

    public void setDivisionpoliticaId(int divisionpoliticaId) {
        this.divisionpoliticaId = divisionpoliticaId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCodigoNacional() {
        return codigoNacional;
    }

    public void setCodigoNacional(int codigoNacional) {
        this.codigoNacional = codigoNacional;
    }

    public String getCodigoIso() {
        return codigoIso;
    }

    public void setCodigoIso(String codigoIso) {
        this.codigoIso = codigoIso;
    }
    @Override
    public String toString(){
        return this.nombre;

    }
}
