package com.sts_ni.estudiocohortecssfv.dto;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Autor: Ing. Miguel Alejandro Lopez Detrinidad
 * Fecha: 01 Marz 2015
 * Descripción: Clase que tiene los atributos de la cabecera
 * de sintoma
 */
public class CabeceraSintomaDTO implements Serializable {

    private String expedienteFisico;
    private String codConsentimeinto;
    private Calendar fechaNacimiento;
    private String sexo;
    private Double pesoKg;
    private Double tallaCm;
    private Double temperaturac;
    private Calendar fechaConsulta;
    private String horaConsulta;
    private Short usuarioMedico;
    /*Nuevo campo agregado*/
    private String categoria;
    private String fif;
    private String consulta;
    private String temMedc;
    private String eritrocitos;


    public String getExpedienteFisico() {
        return expedienteFisico;
    }

    public void setExpedienteFisico(String expedienteFisico) {
        this.expedienteFisico = expedienteFisico;
    }

    public Calendar getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Calendar fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public Double getPesoKg() {
        return pesoKg;
    }

    public void setPesoKg(Double pesoKg) {
        this.pesoKg = pesoKg;
    }

    public Double getTallaCm() {
        return tallaCm;
    }

    public void setTallaCm(Double tallaCm) {
        this.tallaCm = tallaCm;
    }

    public Double getTemperaturac() {
        return temperaturac;
    }

    public void setTemperaturac(Double temperaturac) {
        this.temperaturac = temperaturac;
    }

    public Calendar getFechaConsulta() {
        return fechaConsulta;
    }

    public void setFechaConsulta(Calendar fechaConsulta) {
        this.fechaConsulta = fechaConsulta;
    }

    public String getCodConsentimeinto() {
        return codConsentimeinto;
    }

    public void setCodConsentimeinto(String codConsentimeinto) {
        this.codConsentimeinto = codConsentimeinto;
    }

    public String getHoraConsulta() {
        return horaConsulta;
    }

    public void setHoraConsulta(String horaConsulta) {
        this.horaConsulta = horaConsulta;
    }

    public Short getUsuarioMedico() {
        return usuarioMedico;
    }

    public void setUsuarioMedico(Short usuarioMedico) {
        this.usuarioMedico = usuarioMedico;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getFif() {
        return fif;
    }

    public void setFif(String fif) {
        this.fif = fif;
    }

    public String getConsulta() {
        return consulta;
    }

    public void setConsulta(String consulta) {
        this.consulta = consulta;
    }

    public String getTemMedc() {
        return temMedc;
    }

    public void setTemMedc(String temMedc) {
        this.temMedc = temMedc;
    }

    public String getEritrocitos() {
        return eritrocitos;
    }

    public void setEritrocitos(String eritrocitos) {
        this.eritrocitos = eritrocitos;
    }
}
