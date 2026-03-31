package com.example.tareamovil.clases;

import android.icu.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;

public class Config {
    //public static String local="http://192.168.1.104/movil1/";
    //public static String local="http://192.168.1.10/movil1/";
    //public static String local="http://192.168.10.103/movil1/";//UTH CLASE
    //public static String local="http://test.bonaquian.com/movil1/";//UTH CLASE
    //public static String usuario="";

    public static String local="http://kisna.bonaquian.com/movil1/";//UTH CLASE
    public static String usuario="";
    public static int iduser = 0;

    public static String UBI_SAVE = local + "ubicacion_save.php";

    public static String hoy()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}
