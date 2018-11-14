package com.sts_ni.estudiocohortecssfv.dto;

/**
 * Created by user on 26/04/2015.
 */
public class EstadoNutricionalSintomasDTO {

    private Double imc;
    private String lugarAtencion;
    private Character obeso;
    private Character sobrepeso;
    private Character sospechaProblema;
    private Character normal;
    private Character bajoPeso;
    private Character bajoPesoSevero;

    public Double getImc() {
        return imc;
    }

    public void setImc(Double imc) {
        this.imc = imc;
    }

    public String getLugarAtencion() {
        return lugarAtencion;
    }

    public void setLugarAtencion(String lugarAtencion) {
        this.lugarAtencion = lugarAtencion;
    }

    public Character getObeso() {
        return obeso;
    }

    public void setObeso(Character obeso) {
        this.obeso = obeso;
    }

    public Character getSobrepeso() {
        return sobrepeso;
    }

    public void setSobrepeso(Character sobrepeso) {
        this.sobrepeso = sobrepeso;
    }

    public Character getSospechaProblema() {
        return sospechaProblema;
    }

    public void setSospechaProblema(Character sospechaProblema) {
        this.sospechaProblema = sospechaProblema;
    }

    public Character getNormal() {
        return normal;
    }

    public void setNormal(Character normal) {
        this.normal = normal;
    }

    public Character getBajoPeso() {
        return bajoPeso;
    }

    public void setBajoPeso(Character bajoPeso) {
        this.bajoPeso = bajoPeso;
    }

    public Character getBajoPesoSevero() {
        return bajoPesoSevero;
    }

    public void setBajoPesoSevero(Character bajoPesoSevero) {
        this.bajoPesoSevero = bajoPesoSevero;
    }
}
