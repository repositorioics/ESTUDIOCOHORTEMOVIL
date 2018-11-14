package com.sts_ni.estudiocohortecssfv;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.sts_ni.estudiocohortecssfv.dto.CabeceraSintomaDTO;
import com.sts_ni.estudiocohortecssfv.tools.TabListener;

/**
 * Controlador de la Pantalla Consulta.
 */
public class ConsultaActivity extends ActionBarActivity {

    public ConsultaPagerAdapter mAppSectionsPagerAdapter;
    public CabeceraSintomaDTO CABECERA;

    /**
     * The {@link ViewPager} that will display the three primary sections of the app, one at a
     * time.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta);

         Integer indice = this.getIntent().getIntExtra("indice", -1);

        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.

        mViewPager = (ViewPager) findViewById(R.id.vpgConsulta);
        final ActionBar actionBar = getSupportActionBar();

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mAppSectionsPagerAdapter = new ConsultaPagerAdapter(getSupportFragmentManager(), getResources());
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_action_bar_title_center);
        ((TextView)actionBar.getCustomView().findViewById(R.id.myTitleBar)).setText("");
        ((TextView)actionBar.getCustomView().findViewById(R.id.myUserBar)).setText(((CssfvApp)getApplication()).getInfoSessionWSDTO().getUser());
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        /*ActionBar.Tab tabSintomas = actionBar.newTab()
                .setText(R.string.title_tab_sintomas)
                .setTabListener(new TabListener<CSintomasTabFragment>(
                        this, getResources().getString(R.string.title_tab_sintomas), CSintomasTabFragment.class));
        actionBar.addTab(tabSintomas);

        ActionBar.Tab tabExamenes = actionBar.newTab()
                .setText(R.string.title_tab_examenes)
                .setTabListener(new TabListener<CExamenesTabFragment>(
                        this, getResources().getString(R.string.title_tab_examenes), CExamenesTabFragment.class));
        actionBar.addTab(tabExamenes);

        ActionBar.Tab tabDiagnostico = actionBar.newTab()
                .setText(R.string.title_tab_diagnostico)
                .setTabListener(new TabListener<CDiagnosticoTabFragment>(
                        this, getResources().getString(R.string.title_tab_diagnostico), CDiagnosticoTabFragment.class));
        actionBar.addTab(tabDiagnostico);

        ActionBar.Tab tabCierre = actionBar.newTab()
                .setText(R.string.title_tab_cierre)
                .setTabListener(new TabListener<CCierreTabFragment>(
                        this, getResources().getString(R.string.title_tab_cierre), CCierreTabFragment.class));
        actionBar.addTab(tabCierre);*/

        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // hide the given tab
            }

            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // probably ignore this event
            }
        };

        for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mAppSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(tabListener));
        }

        if(indice != -1) {
            mViewPager.setCurrentItem(indice);
            /*switch (indice) {
                case 0:
                    actionBar.selectTab(tabSintomas);
                    break;
                case 1:
                    actionBar.selectTab(tabExamenes);
                    break;
                case 2:
                    actionBar.selectTab(tabDiagnostico);
                    break;
                case 3:
                    actionBar.selectTab(tabCierre);
                    break;
            }*/
        }
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    protected void onPostCreate (Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);

    }



    public void onSectionAttached(int number) {
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.pantalla_inicio, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    /*  Esta funcion es utilizada por el adpatador del listview **/
    public void onItemClick(int mPosition)
    {

    }

}
