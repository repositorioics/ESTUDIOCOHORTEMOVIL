package com.sts_ni.estudiocohortecssfv.dto;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by leandro on 25/03/2015.
 */
public class SeguimientoInfluenzaDTO implements Serializable {
    private int secHojaInfluenza;
    private int controlDia;
    private String fechaSeguimiento;
    private int usuarioMedico;
    private String consultaInicial;
    private String fiebre;
    private String tos;
    private String secrecionNasal;
    private String dolorGarganta;
    private String congestionNasa;
    private String dolorCabeza;
    private String faltaApetito;
    private String dolorMuscular;
    private String dolorArticular;
    private String dolorOido;
    private String respiracionRapida;
    private String dificultadRespirar;
    private String faltaEscuela;
    private String quedoEnCama;
    private String nombreMedico;

    public int getSecHojaInfluenza() {
        return secHojaInfluenza;
    }

    public void setSecHojaInfluenza(int secHojaInfluenza) {
        this.secHojaInfluenza = secHojaInfluenza;
    }

    public int getControlDia() {
        return controlDia;
    }

    public void setControlDia(int controlDia) {
        this.controlDia = controlDia;
    }

    public String getFechaSeguimiento() {
        return fechaSeguimiento;
    }

    public void setFechaSeguimiento(String fechaSeguimiento) {
        this.fechaSeguimiento = fechaSeguimiento;
    }

    public int getUsuarioMedico() {
        return usuarioMedico;
    }

    public void setUsuarioMedico(int usuarioMedico) {
        this.usuarioMedico = usuarioMedico;
    }

    public String getConsultaInicial() {
        return consultaInicial;
    }

    public void setConsultaInicial(String consultaInicial) {
        this.consultaInicial = consultaInicial;
    }

    public String getFiebre() {
        return fiebre;
    }

    public void setFiebre(String fiebre) {
        this.fiebre = fiebre;
    }

    public String getTos() {
        return tos;
    }

    public void setTos(String tos) {
        this.tos = tos;
    }

    public String getSecrecionNasal() {
        return secrecionNasal;
    }

    public void setSecrecionNasal(String secrecionNasal) {
        this.secrecionNasal = secrecionNasal;
    }

    public String getDolorGarganta() {
        return dolorGarganta;
    }

    public void setDolorGarganta(String dolorGarganta) {
        this.dolorGarganta = dolorGarganta;
    }

    public String getCongestionNasa() {
        return congestionNasa;
    }

    public void setCongestionNasa(String congestionNasa) {
        this.congestionNasa = congestionNasa;
    }

    public String getDolorCabeza() {
        return dolorCabeza;
    }

    public void setDolorCabeza(String dolorCabeza) {
        this.dolorCabeza = dolorCabeza;
    }

    public String getFaltaApetito() {
        return faltaApetito;
    }

    public void setFaltaApetito(String faltaApetito) {
        this.faltaApetito = faltaApetito;
    }

    public String getDolorMuscular() {
        return dolorMuscular;
    }

    public void setDolorMuscular(String dolorMuscular) {
        this.dolorMuscular = dolorMuscular;
    }

    public String getDolorArticular() {
        return dolorArticular;
    }

    public void setDolorArticular(String dolorArticular) {
        this.dolorArticular = dolorArticular;
    }

    public String getDolorOido() {
        return dolorOido;
    }

    public void setDolorOido(String dolorOido) {
        this.dolorOido = dolorOido;
    }

    public String getRespiracionRapida() {
        return respiracionRapida;
    }

    public void setRespiracionRapida(String respiracionRapida) {
        this.respiracionRapida = respiracionRapida;
    }

    public String getDificultadRespirar() {
        return dificultadRespirar;
    }

    public void setDificultadRespirar(String dificultadRespirar) {
        this.dificultadRespirar = dificultadRespirar;
    }

    public String getFaltaEscuela() {
        return faltaEscuela;
    }

    public void setFaltaEscuela(String faltaEscuela) {
        this.faltaEscuela = faltaEscuela;
    }

    public String getQuedoEnCama() {
        return quedoEnCama;
    }

    public void setQuedoEnCama(String quedoEnCama) {
        this.quedoEnCama = quedoEnCama;
    }

    public String getNombreMedico() {
        return nombreMedico;
    }

    public void setNombreMedico(String nombreMedico) {
        this.nombreMedico = nombreMedico;
    }
}
