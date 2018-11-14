package com.sts_ni.estudiocohortecssfv.dto;

import java.io.Serializable;

/**
 * Created by flopezcarballo on 25/03/2015.
 */
public class PerifericoResultadoDTO implements Serializable {
    private int secPerifericoResultado;
    private int secOrdenLaboratorio;
    private String anisocitosis;
    private String anisocromia;
    private String poiquilocitosis;
    private String linfocitosAtipicos;
    private String observacionSblanca;
    private String observacionPlaqueta;
    private String codigoMuestra;
    private Short usuarioBioanalista;
    private String horaReporte;
    private Character estado;

    public int getSecPerifericoResultado() {
        return secPerifericoResultado;
    }

    public void setSecPerifericoResultado(int secPerifericoResultado) {
        this.secPerifericoResultado = secPerifericoResultado;
    }

    public int getSecOrdenLaboratorio() {
        return secOrdenLaboratorio;
    }

    public void setSecOrdenLaboratorio(int secOrdenLaboratorio) {
        this.secOrdenLaboratorio = secOrdenLaboratorio;
    }

    public String getAnisocitosis() {
        return anisocitosis;
    }

    public void setAnisocitosis(String anisocitosis) {
        this.anisocitosis = anisocitosis;
    }

    public String getAnisocromia() {
        return anisocromia;
    }

    public void setAnisocromia(String anisocromia) {
        this.anisocromia = anisocromia;
    }

    public String getPoiquilocitosis() {
        return poiquilocitosis;
    }

    public void setPoiquilocitosis(String poiquilocitosis) {
        this.poiquilocitosis = poiquilocitosis;
    }

    public String getLinfocitosAtipicos() {
        return linfocitosAtipicos;
    }

    public void setLinfocitosAtipicos(String linfocitosAtipicos) {
        this.linfocitosAtipicos = linfocitosAtipicos;
    }

    public String getObservacionSblanca() {
        return observacionSblanca;
    }

    public void setObservacionSblanca(String observacionSblanca) {
        this.observacionSblanca = observacionSblanca;
    }

    public String getObservacionPlaqueta() {
        return observacionPlaqueta;
    }

    public void setObservacionPlaqueta(String observacionPlaqueta) {
        this.observacionPlaqueta = observacionPlaqueta;
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
