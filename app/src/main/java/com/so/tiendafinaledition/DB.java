package com.so.tiendafinaledition;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DB extends SQLiteOpenHelper {
    private static final String dbname = "productos";
    private static final int v = 1;
    private static final String SQLdb = "CREATE TABLE productos(idProducto integer primary key autoincrement, " +
            "codigo text, descripcion text, marca text, presentacion text, precio text, foto text)";

    public DB(@Nullable Context context, String s, Object o, int i) {
        super(context, dbname, null, v);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQLdb);
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Cambiar estructura de la BD si es necesario
    }

    public String administrar_productos(String accion, String[] datos) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            String sql = "";
            if (accion.equals("nuevo")) {
                sql = "INSERT INTO productos(marca, descripcion, presentacion, precio, foto) VALUES('" + datos[1] + "', '" +
                        datos[2] + "', '" + datos[3] + "', '" + datos[4] + "', '" + datos[5] + "' )";
            } else if (accion.equals("modificar")) {
                sql = "UPDATE productos SET marca='" + datos[1] + "', descripcion='" + datos[2] + "', presentacion='" + datos[3] + "', " +
                        "precio='" + datos[4] + "', foto='" + datos[5] + "' WHERE idProducto='" + datos[0] + "'";
            } else if (accion.equals("eliminar")) {
                sql = "DELETE FROM productos WHERE idProducto='" + datos[0] + "'";
            }
            db.execSQL(sql);
            return "ok";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public Cursor obtener_productos() {
        Cursor cursor;
        SQLiteDatabase db = getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM productos ORDER BY marca", null);
        return cursor;
    }
}