package com.sts_ni.estudiocohortecssfv.tools;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
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
import com.sts_ni.estudiocohortecssfv.dto.InicioDTO;
import com.sts_ni.estudiocohortecssfv.utils.StringUtils;

import java.util.ArrayList;

/**
 * Autor: Ing. Miguel Alejandro López Detrinidad
 * Fecha: 08 Feb 2015
 * Descripción: Controlador del List View Customizado.
 */
public class LstViewGenericaInicio extends ArrayAdapter<InicioDTO> implements View.OnClickListener, AbsListView.OnScrollListener {

    private Context CONTEXT;
    private Object ACTIVITY_INST;
    private ArrayList<InicioDTO> VALUES;
    private Resources RES;
    private int CURRENT_FIRST_VISIBLE_ITEM;
    private int CURRENT_VISIBLE_ITEM_COUNT;
    private int CURRENT_SCROLLSTATE;

    /*************  CustomAdapter Constructor *****************/
    public LstViewGenericaInicio(Context context, Object activityInst, ArrayList<InicioDTO> values, Resources res) {

        super(context, R.layout.lista_generica_incio_layout, values);

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
        ListaInicioHolder holder = null;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) ((Activity) CONTEXT)
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.lista_generica_incio_layout,
                    parent, false);

            /************ Set Model values in Holder elements ***********/

            holder = new ListaInicioHolder();

            holder.txtvNumHojaConsulta = (TextView) rowView
                    .findViewById(R.id.txtvNumHojaConsulta);
            holder.txtvNomPaciente = (TextView) rowView
                    .findViewById(R.id.txtvNomPaciente);
            holder.txtvNomMedico = (TextView) rowView
                    .findViewById(R.id.txtvNomMedico);
            holder.txtvEstado = (TextView) rowView
                    .findViewById(R.id.txtvEstado);
            rowView.setTag(holder);
        } else {
            // Get holder
            holder = (ListaInicioHolder) rowView.getTag();
        }

        holder.txtvNumHojaConsulta.setText("" + ((InicioDTO) this.VALUES.get(position)).getCodExpediente());
        holder.txtvNomPaciente.setText(((InicioDTO) this.VALUES.get(position)).getNomPaciente());
        holder.txtvEstado.setText(((InicioDTO) this.VALUES.get(position)).getEstado());
        if(!StringUtils.isNullOrEmpty(((InicioDTO) this.VALUES.get(position)).getNombreMedico())) {
            holder.txtvNomMedico.setText(new StringBuffer().append(
                    ((InicioDTO) this.VALUES.get(position)).getNombreMedico()).toString());
            /*holder.txtvNomMedico.setText(new StringBuffer().append("Médico: ").append(
                    ((InicioDTO) this.VALUES.get(position)).getNombreMedico()).toString());*/
        } else {
            holder.txtvNomMedico.setText("");
        }


        /******** Set Item Click Listner for LayoutInflater for each row *******/

        rowView.setOnClickListener(new OnItemClickListener(position));
        return rowView;
    }

    public InicioDTO getItem(int posicion){
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

    static class ListaInicioHolder
    {
        TextView txtvNumHojaConsulta;
        TextView txtvNomPaciente;
        TextView txtvEstado;
        TextView txtvNomMedico;
    }

    /********* Called when Item click in ListView ************/
    private class OnItemClickListener  implements View.OnClickListener {
        private int mPosition;

        OnItemClickListener(int position){
            mPosition = position;
        }

        @Override
        public void onClick(View arg0) {

            if (ACTIVITY_INST instanceof PantallaInicioActivity){
                PantallaInicioActivity pInicioActivity = (PantallaInicioActivity) CONTEXT;

                pInicioActivity.onItemClick(mPosition);
            } else if (ACTIVITY_INST instanceof InicioEnfermeriaActivity){
                InicioEnfermeriaActivity inicioEnfermActivity = (InicioEnfermeriaActivity) CONTEXT;

                inicioEnfermActivity.onItemClick(mPosition);
            }else if (ACTIVITY_INST instanceof InicioConsultaActivity){
                InicioConsultaActivity inicioConsultaActivity = (InicioConsultaActivity) CONTEXT;

                inicioConsultaActivity.onItemClick(mPosition);
            }
        }
    }
}
