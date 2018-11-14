package com.sts_ni.estudiocohortecssfv.dto;

/**
 * Created by user on 26/04/2015.
 */
public class GeneralesSintomasDTO {

    private Integer presion;
    private Short pas;
    private Short pad;
    private Short fciaResp;
    private Short fciaCard;
    private String lugarAtencion;
    private String consulta;
    private Character segChick;
    private Character turno;
    private Double temMedc;
    private String fis;
    private String fif;
    private String ultDiaFiebre;
    private int codExpediente;
    private int numHojaConsulta;

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

    private String ultDosisAntipiretico;
    private String amPmUltDiaFiebre;
    private String horaUltDosisAntipiretico;
    private String amPmUltDosisAntipiretico;

    public Integer getPresion() {
        return presion;
    }

    public void setPresion(Integer presion) {
        this.presion = presion;
    }

    public Short getPas() {
        return pas;
    }

    public void setPas(Short pas) {
        this.pas = pas;
    }

    public Short getPad() {
        return pad;
    }

    public void setPad(Short pad) {
        this.pad = pad;
    }

    public Short getFciaResp() {
        return fciaResp;
    }

    public void setFciaResp(Short fciaResp) {
        this.fciaResp = fciaResp;
    }

    public Short getFciaCard() {
        return fciaCard;
    }

    public void setFciaCard(Short fciaCard) {
        this.fciaCard = fciaCard;
    }

    public String getLugarAtencion() {
        return lugarAtencion;
    }

    public void setLugarAtencion(String lugarAtencion) {
        this.lugarAtencion = lugarAtencion;
    }

    public String getConsulta() {
        return consulta;
    }

    public void setConsulta(String consulta) {
        this.consulta = consulta;
    }

    public Character getSegChick() {
        return segChick;
    }

    public void setSegChick(Character segChick) {
        this.segChick = segChick;
    }

    public Character getTurno() {
        return turno;
    }

    public void setTurno(Character turno) {
        this.turno = turno;
    }

    public Double getTemMedc() {
        return temMedc;
    }

    public void setTemMedc(Double temMedc) {
        this.temMedc = temMedc;
    }

    public String getUltDiaFiebre() {
        return ultDiaFiebre;
    }

    public void setUltDiaFiebre(String ultDiaFiebre) {
        this.ultDiaFiebre = ultDiaFiebre;
    }

    public String getUltDosisAntipiretico() {
        return ultDosisAntipiretico;
    }

    public void setUltDosisAntipiretico(String ultDosisAntipiretico) {
        this.ultDosisAntipiretico = ultDosisAntipiretico;
    }

    public String getAmPmUltDiaFiebre() {
        return amPmUltDiaFiebre;
    }

    public void setAmPmUltDiaFiebre(String amPmUltDiaFiebre) {
        this.amPmUltDiaFiebre = amPmUltDiaFiebre;
    }

    public String getHoraUltDosisAntipiretico() {
        return horaUltDosisAntipiretico;
    }

    public void setHoraUltDosisAntipiretico(String horaUltDosisAntipiretico) {
        this.horaUltDosisAntipiretico = horaUltDosisAntipiretico;
    }

    public String getAmPmUltDosisAntipiretico() {
        return amPmUltDosisAntipiretico;
    }

    public void setAmPmUltDosisAntipiretico(String amPmUltDosisAntipiretico) {
        this.amPmUltDosisAntipiretico = amPmUltDosisAntipiretico;
    }

    public int getNumHojaConsulta() {
        return numHojaConsulta;
    }

    public void setNumHojaConsulta(int numHojaConsulta) {
        this.numHojaConsulta = numHojaConsulta;
    }

    public int getCodExpediente() {
        return codExpediente;
    }

    public void setCodExpediente(int codExpediente) {
        this.codExpediente = codExpediente;
    }
}
