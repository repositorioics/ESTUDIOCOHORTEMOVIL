package com.sts_ni.estudiocohortecssfv.dto;

/**
 * Created by flopezcarballo on 26/02/2015.
 */
public class PacienteDTO {
    private int codExpediente;
    private int secPaciente;
    private String nomPaciente;

    public int getCodExpediente() {
        return codExpediente;
    }

    public void setCodExpediente(int codExpediente) {
        this.codExpediente = codExpediente;
    }

    public int getSecPaciente() {
        return secPaciente;
    }

    public void setSecPaciente(int secPaciente) {
        this.secPaciente = secPaciente;
    }

    public String getNomPaciente() {
        return nomPaciente;
    }

    public void setNomPaciente(String nomPaciente) {
        this.nomPaciente = nomPaciente;
    }
}
