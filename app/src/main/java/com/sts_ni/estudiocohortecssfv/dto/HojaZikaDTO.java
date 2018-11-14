package com.sts_ni.estudiocohortecssfv.dto;

import java.util.Calendar;
import java.util.List;

/**
 * Created by ics on 17/4/2017.
 */

public class HojaZikaDTO {
    private int secHojaZika;
    private int numHojaSeguimiento;
    private String fis;
    private String fif;
    private Calendar fechaInicio;
    private Calendar fechaCierre;
    private int codExpediente;
    private String nomPaciente;
    private String categoria;
    private String estudioPaciente;
    private char cerrado;
    private String sintomaInicial1;
    private String sintomaInicial2;
    private String sintomaInicial3;
    private String sintomaInicial4;
    private List<SeguimientoZikaDTO> lstSeguimientoZika;

    public int getSecHojaZika() {
        return secHojaZika;
    }

    public void setSecHojaZika(int secHojaZika) {
        this.secHojaZika = secHojaZika;
    }

    public int getNumHojaSeguimiento() {
        return numHojaSeguimiento;
    }

    public void setNumHojaSeguimiento(int numHojaSeguimiento) {
        this.numHojaSeguimiento = numHojaSeguimiento;
    }

    public String getFis() {
        return fis;
    }

    public void setFis(String fis) {
        this.fis = fis;
    }

    public String getFif() {
        return fif;
    }

    public void setFif(String fif) {
        this.fif = fif;
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

    public int getCodExpediente() {
        return codExpediente;
    }

    public void setCodExpediente(int codExpediente) {
        this.codExpediente = codExpediente;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public char getCerrado() {
        return cerrado;
    }

    public void setCerrado(char cerrado) {
        this.cerrado = cerrado;
    }

    public String getSintomaInicial1() {
        return sintomaInicial1;
    }

    public void setSintomaInicial1(String sintomaInicial1) {
        this.sintomaInicial1 = sintomaInicial1;
    }

    public String getSintomaInicial2() {
        return sintomaInicial2;
    }

    public void setSintomaInicial2(String sintomaInicial2) {
        this.sintomaInicial2 = sintomaInicial2;
    }

    public String getSintomaInicial3() {
        return sintomaInicial3;
    }

    public void setSintomaInicial3(String sintomaInicial3) {
        this.sintomaInicial3 = sintomaInicial3;
    }

    public String getSintomaInicial4() {
        return sintomaInicial4;
    }

    public void setSintomaInicial4(String sintomaInicial4) {
        this.sintomaInicial4 = sintomaInicial4;
    }

    public List<SeguimientoZikaDTO> getLstSeguimientoZika() {
        return lstSeguimientoZika;
    }

    public void setLstSeguimientoZika(List<SeguimientoZikaDTO> lstSeguimientoZika) {
        this.lstSeguimientoZika = lstSeguimientoZika;
    }

    public String getNomPaciente() {
        return nomPaciente;
    }

    public void setNomPaciente(String nomPaciente) {
        this.nomPaciente = nomPaciente;
    }

    public String getEstudioPaciente() {
        return estudioPaciente;
    }

    public void setEstudioPaciente(String estudioPaciente) {
        this.estudioPaciente = estudioPaciente;
    }
}
