package com.sts_ni.estudiocohortecssfv;

import android.app.Application;

import com.sts_ni.estudiocohortecssfv.dto.InfoSessionWSDTO;
import com.sts_ni.estudiocohortecssfv.dto.NodoItemDTO;
import com.sts_ni.estudiocohortecssfv.wsclass.DataNodoItemArray;

import java.util.List;

/**
 * Autor: Ing. Miguel Alejandro Lopez Detrinidad
 * Fecha: 17 Feb 2015
 * Descripción: Clase que maneja las variables globales de toda la aplicación.
 */
public class CssfvApp extends Application {

    private InfoSessionWSDTO infoSessionWSDTO;
    private DataNodoItemArray menuArray;

    public InfoSessionWSDTO getInfoSessionWSDTO() {
        return infoSessionWSDTO;
    }

    public void setInfoSessionWSDTO(InfoSessionWSDTO infoSessionWSDTO) {
        this.infoSessionWSDTO = infoSessionWSDTO;
    }

    public DataNodoItemArray getMenuArray() {
        return menuArray;
    }

    public void setMenuArray(DataNodoItemArray menuArray) {
        this.menuArray = menuArray;
    }

}
