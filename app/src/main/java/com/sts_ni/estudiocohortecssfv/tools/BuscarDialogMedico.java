package com.sts_ni.estudiocohortecssfv.tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;

import com.sts_ni.estudiocohortecssfv.R;

/**
* Activity para Dialog Genericos
*/
public class BuscarDialogMedico extends DialogFragment {

    public interface DialogListener {
        public void onDialogAceptClick(DialogFragment dialog);
        public void onDialogCancelClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    DialogListener mListener;


    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    /*@Override
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
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.activity_dialog_buscar_generico, null))
                .setPositiveButton(R.string.boton_aceptar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the positive button event back to the host activity

                        if(getTargetFragment() != null) {
                            ((DialogListener)getTargetFragment()).onDialogAceptClick(BuscarDialogMedico.this);
                        } else {
                            ((DialogListener)getActivity()).onDialogAceptClick(BuscarDialogMedico.this);
                        }
                    }
                })
                .setNegativeButton(R.string.boton_Cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the negative button event back to the host activity
                        if(getTargetFragment() != null) {
                            ((DialogListener)getTargetFragment()).onDialogCancelClick(BuscarDialogMedico.this);
                        } else {
                            ((DialogListener)getActivity()).onDialogCancelClick(BuscarDialogMedico.this);
                        }
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle("Ingrese nombre del médico");

        return alertDialog;
    }
}