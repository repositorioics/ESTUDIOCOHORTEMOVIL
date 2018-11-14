package com.sts_ni.estudiocohortecssfv.dto;

import java.sql.Date;

/**
 * Created by mmoreno on 08/10/2015.
 */
public class ControlCambiosDTO {

    public static final String modificacion = "Modificacion";
    public static final String discrepancia = "Discrepancia";

    private String tipoControl;
    private String nombreCampo;
    private String valorCampo;

    public String getTipoControl() {
        return tipoControl;
    }

    public void setTipoControl(String tipoControl) {
        this.tipoControl = tipoControl;
    }

    public String getNombreCampo() {
        return nombreCampo;
    }

    public void setNombreCampo(String nombreCampo) {
        this.nombreCampo = nombreCampo;
    }

    public String getValorCampo() {
        return valorCampo;
    }

    public void setValorCampo(String valorCampo) {
        this.valorCampo = valorCampo;
    }
}
