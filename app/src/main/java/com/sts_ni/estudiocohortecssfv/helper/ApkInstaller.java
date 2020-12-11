package com.sts_ni.estudiocohortecssfv.helper;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.BuildConfig;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;

public class ApkInstaller {
    public static void installApplication(Context context) {
        if (Build.VERSION.SDK_INT >= 24) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uriFromFile(context, new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + "estudioCohorte_CSSFV.apk"
            )), "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                Log.e("TAG", "Error al abrir el archivo!");
            }
        } else {
            String PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
            String fileName = "estudioCohorte_CSSFV.apk";
            File file = new File(PATH + fileName);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                Log.e("TAG", "Error al abrir el archivo!");
            }
        }

    }

    private static Uri uriFromFile(Context context, File file) {
        //if (Build.VERSION.SDK_INT >= 24) {
            return FileProvider.getUriForFile(context, "com.sts_ni.estudiocohortecssfv" + ".provider", file);
        //} else {
            //return Uri.fromFile(file);
        //}
    }
}
