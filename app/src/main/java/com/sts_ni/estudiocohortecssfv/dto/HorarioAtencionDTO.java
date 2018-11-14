package com.sts_ni.estudiocohortecssfv.dto;

import java.io.Serializable;

/**
 * Autor: Ing. Miguel Alejandro Lopez Detrinidad
 * Fecha: 05 Marz 2015
 */
public class HorarioAtencionDTO implements Serializable {

    private Integer secHorarioAtencion;
    private String turno;
    private String dia;
    private String horaInicio;
    private String horaFin;

    public HorarioAtencionDTO() {
    }

    public HorarioAtencionDTO(Integer secHorarioAtencion, String turno, String dia, String horaInicio, String horaFin) {
        this.secHorarioAtencion = secHorarioAtencion;
        this.turno = turno;
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public Integer getSecHorarioAtencion() {
        return secHorarioAtencion;
    }

    public void setSecHorarioAtencion(Integer secHorarioAtencion) {
        this.secHorarioAtencion = secHorarioAtencion;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(String horaFin) {
        this.horaFin = horaFin;
    }
}
