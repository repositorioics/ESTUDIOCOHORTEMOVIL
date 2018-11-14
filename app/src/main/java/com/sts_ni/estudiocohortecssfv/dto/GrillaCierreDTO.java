package com.sts_ni.estudiocohortecssfv.dto;

import java.io.Serializable;

/**
 * Created by user on 14/04/2015.
 */
public class GrillaCierreDTO implements Serializable {

    private String cargoUsuarioLog;
    private String numeroPersonalLog;
    private String nombreUsuarioLog;
    private String cargoUsuarioMedico;

    public String getNumeroPersonalLog() {
        return numeroPersonalLog;
    }

    public void setNumeroPersonalLog(String numeroPersonalLog) {
        this.numeroPersonalLog = numeroPersonalLog;
    }

    public String getNumeroPersonalMedico() {
        return numeroPersonalMedico;
    }

    public void setNumeroPersonalMedico(String numeroPersonalMedico) {
        this.numeroPersonalMedico = numeroPersonalMedico;
    }

    public String getNumeroPersonalEnfermeria() {
        return numeroPersonalEnfermeria;
    }

    public void setNumeroPersonalEnfermeria(String numeroPersonalEnfermeria) {
        this.numeroPersonalEnfermeria = numeroPersonalEnfermeria;
    }

    private String numeroPersonalMedico;
    private String nombreUsuarioMedico;
    private String cargoEnfermeria;
    private String numeroPersonalEnfermeria;
    private String nombreEnfermeria;

    public String getCargoUsuarioLog() {
        return cargoUsuarioLog;
    }

    public void setCargoUsuarioLog(String cargoUsuarioLog) {
        this.cargoUsuarioLog = cargoUsuarioLog;
    }

    public String getNombreUsuarioLog() {
        return nombreUsuarioLog;
    }

    public void setNombreUsuarioLog(String nombreUsuarioLog) {
        this.nombreUsuarioLog = nombreUsuarioLog;
    }

    public String getCargoUsuarioMedico() {
        return cargoUsuarioMedico;
    }

    public void setCargoUsuarioMedico(String cargoUsuarioMedico) {
        this.cargoUsuarioMedico = cargoUsuarioMedico;
    }

    public String getNombreUsuarioMedico() {
        return nombreUsuarioMedico;
    }

    public void setNombreUsuarioMedico(String nombreUsuarioMedico) {
        this.nombreUsuarioMedico = nombreUsuarioMedico;
    }

    public String getCargoEnfermeria() {
        return cargoEnfermeria;
    }

    public void setCargoEnfermeria(String cargoEnfermeria) {
        this.cargoEnfermeria = cargoEnfermeria;
    }

    public String getNombreEnfermeria() {
        return nombreEnfermeria;
    }

    public void setNombreEnfermeria(String nombreEnfermeria) {
        this.nombreEnfermeria = nombreEnfermeria;
    }
}
