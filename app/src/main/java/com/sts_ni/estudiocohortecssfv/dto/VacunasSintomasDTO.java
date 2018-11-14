package com.sts_ni.estudiocohortecssfv.dto;

/**
 * Created by user on 26/04/2015.
 */
public class VacunasSintomasDTO {

    private Character lactanciaMaterna;
    private Character vacunasCompletas;
    private Character vacunaInfluenza;
    private String fechaVacuna;

    public Character getLactanciaMaterna() {
        return lactanciaMaterna;
    }

    public void setLactanciaMaterna(Character lactanciaMaterna) {
        this.lactanciaMaterna = lactanciaMaterna;
    }

    public Character getVacunasCompletas() {
        return vacunasCompletas;
    }

    public void setVacunasCompletas(Character vacunasCompletas) {
        this.vacunasCompletas = vacunasCompletas;
    }

    public Character getVacunaInfluenza() {
        return vacunaInfluenza;
    }

    public void setVacunaInfluenza(Character vacunaInfluenza) {
        this.vacunaInfluenza = vacunaInfluenza;
    }

    public String getFechaVacuna() {
        return fechaVacuna;
    }

    public void setFechaVacuna(String fechaVacuna) {
        this.fechaVacuna = fechaVacuna;
    }
}
