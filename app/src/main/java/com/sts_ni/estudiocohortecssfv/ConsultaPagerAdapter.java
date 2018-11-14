package com.sts_ni.estudiocohortecssfv;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ResourceBundle;

/***
 * Clase para controlar el Activity de los TABs.
 */
public class ConsultaPagerAdapter extends FragmentPagerAdapter {

    private Resources res;

    public ConsultaPagerAdapter(FragmentManager fm, Resources res) {
        super(fm);
        this.res = res;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                // The first section of the app is the most interesting -- it offers
                // a launchpad into the other demonstrations in this example application.
                return new CSintomasTabFragment();
            case 1:
                return  new CExamenesTabFragment();
            case 2:
                return  new CDiagnosticoTabFragment();
            case 3:
                return  new CCierreTabFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                // The first section of the app is the most interesting -- it offers
                // a launchpad into the other demonstrations in this example application.
                return this.res.getString(R.string.title_tab_sintomas);

            case 1:
                return this.res.getString(R.string.title_tab_examenes);
            case 2:
                return this.res.getString(R.string.title_tab_diagnostico);
            case 3:
                return this.res.getString(R.string.title_tab_cierre);
        }
        return "Section " + (position + 1);
    }
}

