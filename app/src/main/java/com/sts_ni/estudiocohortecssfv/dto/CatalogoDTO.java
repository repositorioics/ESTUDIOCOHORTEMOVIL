package com.sts_ni.estudiocohortecssfv.dto;

/**
 * Created by MiguelLopez on 23/12/2015.
 */
public class CatalogoDTO {
    private int id;
    private String name;

    public CatalogoDTO(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
