package com.sts_ni.estudiocohortecssfv.dto;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Autor: Ing. Miguel Alejandro López Detrinidad
 * Fecha: 09 Feb 2015
 * Descripción: Objeto para establecer los valores de los items de las listas de inicio.
 */
public class InicioDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private int idObjeto;
    private String numHojaConsulta;
    private String nomPaciente;
    private String estado;
    private int CodExpediente;
    private String ExpedienteFisico;
    private String sexo;
    private Calendar fechaNac;
    private Calendar fechaConsulta;
    private String numOrdenLlegada;
    private char codigoEstado;
    private int usuarioMedico;
    private int medicoCambioTurno;
    private String nombreMedico;
    private String usuarioEnfermeria;
    private String horasv;
    private String estudiosParticipantes;
    /*Nuevo campo agregado*/
    private String categoria;
    private String fif;
    private String consulta;
    private String temMedc;

    public int getIdObjeto() {
        return idObjeto;
    }

    public void setIdObjeto(int idObjeto) {
        this.idObjeto = idObjeto;
    }

    public String getNumHojaConsulta() {
        return numHojaConsulta;
    }

    public void setNumHojaConsulta(String numHojaConsulta) {
        this.numHojaConsulta = numHojaConsulta;
    }

    public String getNomPaciente() {
        return nomPaciente;
    }

    public void setNomPaciente(String nomPaciente) {
        this.nomPaciente = nomPaciente;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getCodExpediente() {
        return CodExpediente;
    }
    public void setCodExpediente(int codExpediente) {
        CodExpediente = codExpediente;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getExpedienteFisico() {
        return ExpedienteFisico;
    }

    public void setExpedienteFisico(String expedienteFisico) {
        ExpedienteFisico = expedienteFisico;
    }

    public Calendar getFechaNac() {
        return fechaNac;
    }

    public void setFechaNac(Calendar fechaNac) {
        this.fechaNac = fechaNac;
    }

    public Calendar getFechaConsulta() {
        return fechaConsulta;
    }

    public void setFechaConsulta(Calendar fechaConsulta) {
        this.fechaConsulta = fechaConsulta;
    }

    public String getNumOrdenLlegada() {
        return numOrdenLlegada;
    }

    public void setNumOrdenLlegada(String numOrdenLlegada) {
        this.numOrdenLlegada = numOrdenLlegada;
    }

    public char getCodigoEstado() {
        return codigoEstado;
    }

    public void setCodigoEstado(char codigoEstado) {
        this.codigoEstado = codigoEstado;
    }

    public int getUsuarioMedico() {
        return usuarioMedico;
    }

    public void setUsuarioMedico(int usuarioMedico) {
        this.usuarioMedico = usuarioMedico;
    }

    public int getMedicoCambioTurno() {
        return medicoCambioTurno;
    }

    public void setMedicoCambioTurno(int medicoCambioTurno) {
        this.medicoCambioTurno = medicoCambioTurno;
    }

    public String getNombreMedico() {
        return nombreMedico;
    }

    public void setNombreMedico(String nombreMedico) {
        this.nombreMedico = nombreMedico;
    }

    public String getUsuarioEnfermeria() {
        return usuarioEnfermeria;
    }

    public void setUsuarioEnfermeria(String usuarioEnfermeria) {
        this.usuarioEnfermeria = usuarioEnfermeria;
    }

    public String getHorasv() {
        return horasv;
    }

    public void setHorasv(String horasv) {
        this.horasv = horasv;
    }

    public String getEstudiosParticipantes() {
        return estudiosParticipantes;
    }

    public void setEstudiosParticipantes(String estudiosParticipantes) {
        this.estudiosParticipantes = estudiosParticipantes;
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
}
