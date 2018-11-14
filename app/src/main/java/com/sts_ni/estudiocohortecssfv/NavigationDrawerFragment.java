package com.sts_ni.estudiocohortecssfv;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.sts_ni.estudiocohortecssfv.dto.ErrorDTO;
import com.sts_ni.estudiocohortecssfv.dto.NodoItemDTO;
import com.sts_ni.estudiocohortecssfv.ws.SeguridadWS;
import com.sts_ni.estudiocohortecssfv.wsclass.DataNodoItemArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks CALLBACKS;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle DRAWER_TOGGLE;

    private DrawerLayout DRAWER_LAYOUT;
    private ListView DRAWER_LIST_VIEW;
    private View FRAGMENT_CONTAINER_VIEW;

    private int CURRENT_SELECTED_POSTION = 0;
    private boolean FROM_SAVED_INSTANCE_STATE;
    private boolean USER_LEARNED_DRAWER;

    private SeguridadWS SEGURIDAD_WS;
    private List<NodoItemDTO> LST_NODO_ITEM_DTO;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        USER_LEARNED_DRAWER = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            CURRENT_SELECTED_POSTION = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            FROM_SAVED_INSTANCE_STATE = true;
        }

        // Select either the default item (0) or the last selected item.
        selectItem(CURRENT_SELECTED_POSTION, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            SEGURIDAD_WS = new SeguridadWS(getActivity());

            LST_NODO_ITEM_DTO = new ArrayList<NodoItemDTO>();
            for (int i = 0; i < ((CssfvApp) getActivity().getApplication()).getMenuArray().getPropertyCount(); i++) {
                LST_NODO_ITEM_DTO.add((NodoItemDTO) ((CssfvApp) getActivity().getApplication()).getMenuArray().getProperty(i));
            }
            NodoItemDTO nodoSalida = new NodoItemDTO();
            nodoSalida.setEtiqueta("Salir del sistema");
            nodoSalida.setUrl("0");
            LST_NODO_ITEM_DTO.add(nodoSalida);

            DRAWER_LIST_VIEW = (ListView) inflater.inflate(
                    R.layout.navigation_menu_sistema, container, false);
            DRAWER_LIST_VIEW.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    selectItem(position, true);
                }
            });
            DRAWER_LIST_VIEW.setAdapter(new ArrayAdapter<NodoItemDTO>(
                    getActionBar().getThemedContext(),
                    R.layout.menu_list_item_layout,
                    android.R.id.text1,
                    LST_NODO_ITEM_DTO));
            DRAWER_LIST_VIEW.setItemChecked(CURRENT_SELECTED_POSTION, true);
            return DRAWER_LIST_VIEW;
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
        return DRAWER_LIST_VIEW = (ListView) inflater.inflate(R.layout.navigation_menu_sistema, container, false);
    }

    public boolean isDrawerOpen() {
        return DRAWER_LAYOUT != null && DRAWER_LAYOUT.isDrawerOpen(FRAGMENT_CONTAINER_VIEW);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        FRAGMENT_CONTAINER_VIEW = getActivity().findViewById(fragmentId);
        DRAWER_LAYOUT = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        DRAWER_LAYOUT.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        DRAWER_TOGGLE = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                DRAWER_LAYOUT,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!USER_LEARNED_DRAWER) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    USER_LEARNED_DRAWER = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!USER_LEARNED_DRAWER && !FROM_SAVED_INSTANCE_STATE) {
            DRAWER_LAYOUT.openDrawer(FRAGMENT_CONTAINER_VIEW);
        }

        // Defer code dependent on restoration of previous instance state.
        DRAWER_LAYOUT.post(new Runnable() {
            @Override
            public void run() {
                DRAWER_TOGGLE.syncState();
            }
        });

        DRAWER_LAYOUT.setDrawerListener(DRAWER_TOGGLE);
    }

    private void selectItem(int position, boolean lstClick) {
        CURRENT_SELECTED_POSTION = position;
        if (DRAWER_LIST_VIEW != null) {
            DRAWER_LIST_VIEW.setItemChecked(position, true);
        }
        if (DRAWER_LAYOUT != null) {
            DRAWER_LAYOUT.closeDrawer(FRAGMENT_CONTAINER_VIEW);
        }
        if (CALLBACKS != null) {
            CALLBACKS.onNavigationDrawerItemSelected(position);
        }
        if(lstClick) {
            NodoItemDTO nodoSeleccionado = LST_NODO_ITEM_DTO.get(position);
            if (nodoSeleccionado.getUrl().compareTo("0") == 0) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXIT", true);
                startActivity(intent);
                //System.exit(0);
            }else{
                try {
                    Intent intent = new Intent();
                    intent.setAction(nodoSeleccionado.getUrl());
                    startActivity(intent);
                    getActivity().finish();
                }catch (ActivityNotFoundException e){
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            CALLBACKS = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        CALLBACKS = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, CURRENT_SELECTED_POSTION);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        DRAWER_TOGGLE.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (DRAWER_LAYOUT != null && isDrawerOpen()) {
            inflater.inflate(R.menu.pantalla_inicio, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(android.view.Menu menu){
        super.onPrepareOptionsMenu(menu);
        if (DRAWER_LAYOUT != null && isDrawerOpen()) {
            MenuItem itemLupa = (MenuItem) menu.findItem(R.id.action_search);
            itemLupa.setVisible(false);
            MenuItem itemRecargar = (MenuItem) menu.findItem(R.id.action_reload);
            itemRecargar.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (DRAWER_TOGGLE.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.getItemId() == R.id.action_search) {
            Toast.makeText(getActivity(), "Example action.", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.label_menu);
    }

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }

}
