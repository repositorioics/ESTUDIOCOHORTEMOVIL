package com.sts_ni.estudiocohortecssfv.tools;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sts_ni.estudiocohortecssfv.InicioConsultaActivity;
import com.sts_ni.estudiocohortecssfv.InicioEnfermeriaActivity;
import com.sts_ni.estudiocohortecssfv.PantallaInicioActivity;
import com.sts_ni.estudiocohortecssfv.R;
import com.sts_ni.estudiocohortecssfv.dto.ExpedienteDTO;
import com.sts_ni.estudiocohortecssfv.expedientesactivities.ExpedienteActivity;

import java.util.ArrayList;

/**
 * Autor: Ing. Leandro Vanegas
 * Fecha: 26  Marzo 2015
 * Descripción: Controlador del List View Customizado.
 */
public class LstViewGenericaExp extends ArrayAdapter<ExpedienteDTO> implements View.OnClickListener, AbsListView.OnScrollListener {

    private Context CONTEXT;
    private Object ACTIVITY_INST;
    private ArrayList<ExpedienteDTO> VALUES;
    private Resources RES;
    private int CURRENT_FIRST_VISIBLE_ITEM;
    private int CURRENT_VISIBLE_ITEM_COUNT;
    private int CURRENT_SCROLLSTATE;

    /*************  CustomAdapter Constructor *****************/
    public LstViewGenericaExp(Context context, Object activityInst, ArrayList<ExpedienteDTO> values, Resources res) {

        super(context, R.layout.lista_generica_exp_layout, values);

        /********** Take passed values **********/
        this.CONTEXT = context;
        this.ACTIVITY_INST = activityInst;
        this.VALUES = values;
        this.RES = res;

        /*********** Layout inflator to call external xml layout () ***********/
        //inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }


    /****** Depends upon data size called for each row , Create each ListView row *****/
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        ListaExpedienteHolder holder = null;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) ((Activity) CONTEXT)
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.lista_generica_exp_layout,
                    parent, false);

            /************ Set Model values in Holder elements ***********/

            holder = new ListaExpedienteHolder();

            holder.txtvNumHojaConsulta = (TextView) rowView
                    .findViewById(R.id.txtvNumHojaConsulta);
            holder.txtvFecha = (TextView) rowView
                    .findViewById(R.id.txtvFecha);
            holder.txtvHora = (TextView) rowView
                    .findViewById(R.id.txtvHora);
            holder.txtvNomMedico = (TextView) rowView
                    .findViewById(R.id.txtvNomMedico);
            holder.txtvEstado = (TextView) rowView
                    .findViewById(R.id.txtvEstado);
            rowView.setTag(holder);
        } else {
            // Get holder
            holder = (ListaExpedienteHolder) rowView.getTag();
        }

        holder.txtvNumHojaConsulta.setText("" + ((ExpedienteDTO) this.VALUES.get(position)).getNumHojaConsulta());
        holder.txtvFecha.setText(((ExpedienteDTO) this.VALUES.get(position)).getFechaConsulta());
        holder.txtvHora.setText(((ExpedienteDTO) this.VALUES.get(position)).getHoraConsulta());
        holder.txtvNomMedico.setText(((ExpedienteDTO) this.VALUES.get(position)).getNomMedico());
        holder.txtvEstado.setText(((ExpedienteDTO) this.VALUES.get(position)).getEstado());
        /*String hojaImpresa = this.VALUES.get(position).getHojaImpresa();
        if (hojaImpresa != "") {
            if (hojaImpresa.trim().equals("N")) {
                holder.txtvNumHojaConsulta.setTextColor(Color.parseColor("#B82601"));
                holder.txtvFecha.setTextColor(Color.parseColor("#B82601"));
                holder.txtvHora.setTextColor(Color.parseColor("#B82601"));
                holder.txtvNomMedico.setTextColor(Color.parseColor("#B82601"));
                holder.txtvEstado.setTextColor(Color.parseColor("#B82601"));
            }
        }*/


        /******** Set Item Click Listner for LayoutInflater for each row *******/

        rowView.setOnClickListener(new OnItemClickListener(position));

        if (position % 2 == 1) {
            rowView.setBackgroundColor(android.graphics.Color.rgb(222,231,209));
        } else {
            rowView.setBackgroundColor(android.graphics.Color.rgb(239,243,234));
        }

        return rowView;
    }

    public ExpedienteDTO getItem(int posicion){
        return this.VALUES.get(posicion);
    }

    @Override
    public void onClick(View v) {
        //Log.v("CustomAdapter", "=====Row button clicked=====");
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        this.CURRENT_SCROLLSTATE = scrollState;
        this.isScrollCompleted();
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.CURRENT_FIRST_VISIBLE_ITEM = firstVisibleItem;
        this.CURRENT_VISIBLE_ITEM_COUNT = visibleItemCount;
    }

    private void isScrollCompleted() {
        if (this.CURRENT_VISIBLE_ITEM_COUNT > 0 && this.CURRENT_SCROLLSTATE == SCROLL_STATE_IDLE) {
            if (ACTIVITY_INST instanceof PantallaInicioActivity){
                PantallaInicioActivity pInicioActivity = (PantallaInicioActivity) CONTEXT;

                pInicioActivity.onLastScroll();
            }
        }
    }

    static class ListaExpedienteHolder
    {
        TextView txtvNumHojaConsulta;
        TextView txtvFecha;
        TextView txtvHora;
        TextView txtvNomMedico;
        TextView txtvEstado;
    }

    /********* Called when Item click in ListView ************/
    private class OnItemClickListener  implements View.OnClickListener {
        private int mPosition;

        OnItemClickListener(int position){
            mPosition = position;
        }

        @Override
        public void onClick(View arg0) {
            if (ACTIVITY_INST instanceof ExpedienteActivity) {
                ExpedienteActivity pInicioActivity = (ExpedienteActivity) CONTEXT;
                pInicioActivity.onItemClick(mPosition);
            }
        }
    }

    public ArrayList<ExpedienteDTO> getResultado()
    {
        return VALUES;
    }
}
