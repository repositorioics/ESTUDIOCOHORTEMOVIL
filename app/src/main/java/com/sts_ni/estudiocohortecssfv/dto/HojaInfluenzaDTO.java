package com.sts_ni.estudiocohortecssfv.dto;

import java.util.Calendar;
import java.util.List;

/**
 * Created by leandro on 15/04/2015.
 */
public class HojaInfluenzaDTO {
    private int secHojaInfluenza;
    private int numHojaSeguimiento;
    private String fif;
    private String fis;
    private Calendar fechaInicio;
    private Calendar fechaCierre;
    private String nomPaciente;
    private int codExpediente;
    private char cerrado;
    private String estudioPaciente;
    private List<SeguimientoInfluenzaDTO> lstSeguimientoInfluenza;

    public int getNumHojaSeguimiento() {
        return numHojaSeguimiento;
    }

    public void setNumHojaSeguimiento(int numHojaSeguimiento) {
        this.numHojaSeguimiento = numHojaSeguimiento;
    }

    public String getFif() {
        return fif;
    }

    public void setFif(String fif) {
        this.fif = fif;
    }

    public String getFis() {
        return fis;
    }

    public void setFis(String fis) {
        this.fis = fis;
    }

    public Calendar getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Calendar fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Calendar getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(Calendar fechaCierre) {
        this.fechaCierre = fechaCierre;
    }


    public String getNomPaciente() {
        return nomPaciente;
    }

    public void setNomPaciente(String nomPaciente) {
        this.nomPaciente = nomPaciente;
    }

    public int getCodExpediente() {
        return codExpediente;
    }

    public void setCodExpediente(int codExpediente) {
        this.codExpediente = codExpediente;
    }


    public int getSecHojaInfluenza() {
        return secHojaInfluenza;
    }

    public void setSecHojaInfluenza(int secHojaInfluenza) {
        this.secHojaInfluenza = secHojaInfluenza;
    }

    public char getCerrado() {
        return cerrado;
    }

    public void setCerrado(char cerrado) {
        this.cerrado = cerrado;
    }

    public String getEstudioPaciente() {
        return estudioPaciente;
    }

    public void setEstudioPaciente(String estudioPaciente) {
        this.estudioPaciente = estudioPaciente;
    }

    public List<SeguimientoInfluenzaDTO> getLstSeguimientoInfluenza() {
        return lstSeguimientoInfluenza;
    }

    public void setLstSeguimientoInfluenza(List<SeguimientoInfluenzaDTO> lstSeguimientoInfluenza) {
        this.lstSeguimientoInfluenza = lstSeguimientoInfluenza;
    }
}
