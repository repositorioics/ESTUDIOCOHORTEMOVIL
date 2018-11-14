package com.sts_ni.estudiocohortecssfv.dto;

/**
 * Created by user on 26/04/2015.
 */
public class RenalSintomasDTO {

    private Character sintomasUrinarios;
    private Character leucocituria;
    private Character nitritos;
    private Character eritrocitos;
    private Character bilirrubinuria;

    public Character getEritrocitos() {
        return eritrocitos;
    }

    public void setEritrocitos(Character eritrocitos) {
        this.eritrocitos = eritrocitos;
    }

    public Character getSintomasUrinarios() {
        return sintomasUrinarios;
    }

    public void setSintomasUrinarios(Character sintomasUrinarios) {
        this.sintomasUrinarios = sintomasUrinarios;
    }

    public Character getLeucocituria() {
        return leucocituria;
    }

    public void setLeucocituria(Character leucocituria) {
        this.leucocituria = leucocituria;
    }

    public Character getNitritos() {
        return nitritos;
    }

    public void setNitritos(Character nitritos) {
        this.nitritos = nitritos;
    }

    public Character getBilirrubinuria() {
        return bilirrubinuria;
    }

    public void setBilirrubinuria(Character bilirrubinuria) {
        this.bilirrubinuria = bilirrubinuria;
    }
}
