package com.sts_ni.estudiocohortecssfv.dto;

import java.io.Serializable;

/**
 * Created by flopezcarballo on 25/03/2015.
 */
public class MalariaResultadoDTO implements Serializable {

    private int secMalariaResultado;
    private int secOrdenLaboratorio;
    private String PFalciparum;
    private String PVivax;
    private String negativo;
    private String codigoMuestra;
    private Short usuarioBioanalista;
    private String horaReporte;
    private Character estado;

    public int getSecMalariaResultado() {
        return secMalariaResultado;
    }

    public void setSecMalariaResultado(int secMalariaResultado) {
        this.secMalariaResultado = secMalariaResultado;
    }

    public int getSecOrdenLaboratorio() {
        return secOrdenLaboratorio;
    }

    public void setSecOrdenLaboratorio(int secOrdenLaboratorio) {
        this.secOrdenLaboratorio = secOrdenLaboratorio;
    }

    public String getPFalciparum() {
        return PFalciparum;
    }

    public void setPFalciparum(String PFalciparum) {
        this.PFalciparum = PFalciparum;
    }

    public String getPVivax() {
        return PVivax;
    }

    public void setPVivax(String PVivax) {
        this.PVivax = PVivax;
    }

    public String getNegativo() {
        return negativo;
    }

    public void setNegativo(String negativo) {
        this.negativo = negativo;
    }

    public String getCodigoMuestra() {
        return codigoMuestra;
    }

    public void setCodigoMuestra(String codigoMuestra) {
        this.codigoMuestra = codigoMuestra;
    }

    public Short getUsuarioBioanalista() {
        return usuarioBioanalista;
    }

    public void setUsuarioBioanalista(Short usuarioBioanalista) {
        this.usuarioBioanalista = usuarioBioanalista;
    }

    public Character getEstado() {
        return estado;
    }

    public void setEstado(Character estado) {
        this.estado = estado;
    }

    public String getHoraReporte() {
        return horaReporte;
    }

    public void setHoraReporte(String horaReporte) {
        this.horaReporte = horaReporte;
    }
}
