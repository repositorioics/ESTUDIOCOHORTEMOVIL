package com.sts_ni.estudiocohortecssfv.tools;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

import com.sts_ni.estudiocohortecssfv.R;

/**
 * Created by mmoreno on 30/12/2015.
 */
public class ImpresionPdfDialog extends DialogFragment {

    public interface DialogImpresionPdfListener {
        public void onClickImpresion();
        public void onClickPDF();
    }

    DialogImpresionPdfListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
   /* @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (DialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }*/

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View myView = inflater.inflate(R.layout.activity_dialog_impresion_pdf, null);

        myView.findViewById(R.id.imgBtnImprimir).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DialogImpresionPdfListener)getActivity()).onClickImpresion();
                getDialog().dismiss();
            }
        });

        myView.findViewById(R.id.imgBtnPdf).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DialogImpresionPdfListener)getActivity()).onClickPDF();
                getDialog().dismiss();
            }
        });

        builder.setView(myView)
                .setNegativeButton(R.string.boton_Cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle("Seleccione una opción");
        /*alertDialog.findViewById(R.id.imgBtnImprimir).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DialogImpresionPdfListener)getTargetFragment()).onClickImpresion();
            }
        });*/

        /*alertDialog.findViewById(R.id.imgBtnPdf).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DialogImpresionPdfListener)getTargetFragment()).onClickPDF();
            }
        });*/

        return alertDialog;
    }
}
