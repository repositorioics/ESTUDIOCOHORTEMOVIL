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

    /*Nuevos campos agregados 24/09/2019*/
    private String fiebreLeve;
    private String fiebreModerada;
    private String fiebreSevera;
    private String tosLeve;
    private String tosModerada;
    private String tosSevera;
    private String secrecionNasalLeve;
    private String secrecionNasalModerada;
    private String secrecionNasalSevera;
    private String dolorGargantaLeve;
    private String dolorGargantaModerada;
    private String dolorGargantaSevera;
    private String dolorCabezaLeve;
    private String dolorCabezaModerada;
    private String dolorCabezaSevera;
    private String dolorMuscularLeve;
    private String dolorMuscularModerada;
    private String dolorMuscularSevera;
    private String dolorArticularLeve;
    private String dolorArticularModerada;
    private String dolorArticularSevera;

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

    public String getFiebreLeve() {
        return fiebreLeve;
    }

    public void setFiebreLeve(String fiebreLeve) {
        this.fiebreLeve = fiebreLeve;
    }

    public String getFiebreModerada() {
        return fiebreModerada;
    }

    public void setFiebreModerada(String fiebreModerada) {
        this.fiebreModerada = fiebreModerada;
    }

    public String getFiebreSevera() {
        return fiebreSevera;
    }

    public void setFiebreSevera(String fiebreSevera) {
        this.fiebreSevera = fiebreSevera;
    }

    public String getTosLeve() {
        return tosLeve;
    }

    public void setTosLeve(String tosLeve) {
        this.tosLeve = tosLeve;
    }

    public String getTosModerada() {
        return tosModerada;
    }

    public void setTosModerada(String tosModerada) {
        this.tosModerada = tosModerada;
    }

    public String getTosSevera() {
        return tosSevera;
    }

    public void setTosSevera(String tosSevera) {
        this.tosSevera = tosSevera;
    }

    public String getSecrecionNasalLeve() {
        return secrecionNasalLeve;
    }

    public void setSecrecionNasalLeve(String secrecionNasalLeve) {
        this.secrecionNasalLeve = secrecionNasalLeve;
    }

    public String getSecrecionNasalModerada() {
        return secrecionNasalModerada;
    }

    public void setSecrecionNasalModerada(String secrecionNasalModerada) {
        this.secrecionNasalModerada = secrecionNasalModerada;
    }

    public String getSecrecionNasalSevera() {
        return secrecionNasalSevera;
    }

    public void setSecrecionNasalSevera(String secrecionNasalSevera) {
        this.secrecionNasalSevera = secrecionNasalSevera;
    }

    public String getDolorGargantaLeve() {
        return dolorGargantaLeve;
    }

    public void setDolorGargantaLeve(String dolorGargantaLeve) {
        this.dolorGargantaLeve = dolorGargantaLeve;
    }

    public String getDolorGargantaModerada() {
        return dolorGargantaModerada;
    }

    public void setDolorGargantaModerada(String dolorGargantaModerada) {
        this.dolorGargantaModerada = dolorGargantaModerada;
    }

    public String getDolorGargantaSevera() {
        return dolorGargantaSevera;
    }

    public void setDolorGargantaSevera(String dolorGargantaSevera) {
        this.dolorGargantaSevera = dolorGargantaSevera;
    }

    public String getDolorCabezaLeve() {
        return dolorCabezaLeve;
    }

    public void setDolorCabezaLeve(String dolorCabezaLeve) {
        this.dolorCabezaLeve = dolorCabezaLeve;
    }

    public String getDolorCabezaModerada() {
        return dolorCabezaModerada;
    }

    public void setDolorCabezaModerada(String dolorCabezaModerada) {
        this.dolorCabezaModerada = dolorCabezaModerada;
    }

    public String getDolorCabezaSevera() {
        return dolorCabezaSevera;
    }

    public void setDolorCabezaSevera(String dolorCabezaSevera) {
        this.dolorCabezaSevera = dolorCabezaSevera;
    }

    public String getDolorMuscularLeve() {
        return dolorMuscularLeve;
    }

    public void setDolorMuscularLeve(String dolorMuscularLeve) {
        this.dolorMuscularLeve = dolorMuscularLeve;
    }

    public String getDolorMuscularModerada() {
        return dolorMuscularModerada;
    }

    public void setDolorMuscularModerada(String dolorMuscularModerada) {
        this.dolorMuscularModerada = dolorMuscularModerada;
    }

    public String getDolorMuscularSevera() {
        return dolorMuscularSevera;
    }

    public void setDolorMuscularSevera(String dolorMuscularSevera) {
        this.dolorMuscularSevera = dolorMuscularSevera;
    }

    public String getDolorArticularLeve() {
        return dolorArticularLeve;
    }

    public void setDolorArticularLeve(String dolorArticularLeve) {
        this.dolorArticularLeve = dolorArticularLeve;
    }

    public String getDolorArticularModerada() {
        return dolorArticularModerada;
    }

    public void setDolorArticularModerada(String dolorArticularModerada) {
        this.dolorArticularModerada = dolorArticularModerada;
    }

    public String getDolorArticularSevera() {
        return dolorArticularSevera;
    }

    public void setDolorArticularSevera(String dolorArticularSevera) {
        this.dolorArticularSevera = dolorArticularSevera;
    }
}
