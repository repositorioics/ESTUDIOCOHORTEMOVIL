package com.sts_ni.estudiocohortecssfv.dto;

/**
 * Created by mmoreno on 09/10/2015.
 */
public class MedicosDTO {

    Short idMedico;
    String nombreMedico;
    String codigoPersonal;

    boolean presentaCodigoPersonal;

    public String getNombreMedico() {
        return nombreMedico;
    }

    public void setNombreMedico(String nombreMedico) {
        this.nombreMedico = nombreMedico;
    }

    public Short getIdMedico() {
        return idMedico;
    }

    public void setIdMedico(Short idMedico) {
        this.idMedico = idMedico;
    }

    public String getCodigoPersonal() {
        return codigoPersonal;
    }

    public void setCodigoPersonal(String codigoPersonal) {
        this.codigoPersonal = codigoPersonal;
    }

    public boolean isPresentaCodigoPersonal() {
        return presentaCodigoPersonal;
    }

    public void setPresentaCodigoPersonal(boolean presentaCodigoPersonal) {
        this.presentaCodigoPersonal = presentaCodigoPersonal;
    }

    @Override
    public String toString() {
        if(presentaCodigoPersonal) {
            return this.codigoPersonal;
        }else{
            return this.nombreMedico;
        }
    }
}
