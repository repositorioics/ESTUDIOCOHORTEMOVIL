package com.sts_ni.estudiocohortecssfv.dto;

public class MunicipiosDTO {
    private int divisionpoliticaId;
    private String nombre;
    private int codigoNacional;
    private int dependencia;

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

    public int getDependencia() {
        return dependencia;
    }

    public void setDependencia(int dependencia) {
        this.dependencia = dependencia;
    }
    @Override
    public String toString(){
        return this.nombre;

    }
}
