package com.sts_ni.estudiocohortecssfv.dto;

import java.util.ArrayList;

/**
 * Created by flopezcarballo on 23/02/2015.
 */
public class ResultadoListWSDTO<T> extends ErrorDTO{
   private ArrayList<T> lstResultado;

    public ArrayList<T> getLstResultado() {
        return lstResultado;
    }

    public void setLstResultado(ArrayList<T> lstResultado) {
        this.lstResultado = lstResultado;
    }
}

