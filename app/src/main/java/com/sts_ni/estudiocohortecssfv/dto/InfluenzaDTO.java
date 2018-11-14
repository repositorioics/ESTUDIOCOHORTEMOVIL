package com.sts_ni.estudiocohortecssfv.dto;

import java.io.Serializable;

/**
 * Created by flopezcarballo on 25/03/2015.
 */
public class InfluenzaDTO implements Serializable {
    private int secInfluenzaMuestra;
    private int secOrdenLaboratorio;
    private String codigoMuestra;
    private Short usuarioBioanalista;
    private String horaReporte;
    private Character estado;

    public int getSecInfluenzaMuestra() {
        return secInfluenzaMuestra;
    }

    public void setSecInfluenzaMuestra(int secInfluenzaMuestra) {
        this.secInfluenzaMuestra = secInfluenzaMuestra;
    }

    public int getSecOrdenLaboratorio() {
        return secOrdenLaboratorio;
    }

    public void setSecOrdenLaboratorio(int secOrdenLaboratorio) {
        this.secOrdenLaboratorio = secOrdenLaboratorio;
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
