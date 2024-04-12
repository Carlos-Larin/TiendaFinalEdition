package com.so.tiendafinaledition;

import android.os.Build;
import androidx.annotation.RequiresApi;
import java.util.Base64;
import java.util.UUID;
@RequiresApi(api = Build.VERSION_CODES.O)
public class utilidades {
    static String urlConsulta="http://192.168.1.20:5984/tiendaonline/_design/TiendaOnline/_view/TiendaOnline";
    static String urlMto="http://http://192.168.1.20:5984/tiendaonline";
    static String user="CarlosAristides";
    static String passwd="CARTOS12A?";
    static String credencialesCodificadas = Base64.getEncoder().encodeToString((user +":"+ passwd).getBytes());
    public String generarIdUnico(){
        return java.util.UUID.randomUUID().toString();
    }
}
