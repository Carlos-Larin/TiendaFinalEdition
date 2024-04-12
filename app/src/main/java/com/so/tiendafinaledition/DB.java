package com.so.tiendafinaledition;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DB extends SQLiteOpenHelper {
    private static final String dbname = "productos";
    private static final int v = 1;
    private static final String SQLdb = "CREATE TABLE productos(id text, rev text, idProducto integer primary key autoincrement, " +
            "codigo text, descripcion text, marca text, presentacion text, precio text, foto text)";

    public DB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, dbname, factory, v);
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
                sql = "INSERT INTO productos(id,rev,idProducto,marca, descripcion, presentacion, precio, foto) VALUES('" + datos[0] + "', '" +
                        datos[1] + "', '" + datos[2] + "', '" + datos[3] + "', '" + datos[4] + "' , '" + datos[5] + "', '" + datos[6] + "', '" + datos[7] + "')";
            } else if (accion.equals("modificar")) {
                sql = "UPDATE productos SET id='"+ datos[0] +"',rev='"+ datos[1] +"',marca='" + datos[3] + "', descripcion='" + datos[4] + "', presentacion='" + datos[5] + "', " +
                        "precio='" + datos[6] + "', foto='" + datos[7] + "' WHERE idProducto='" + datos[2] + "'";
            } else if (accion.equals("eliminar")) {
                sql = "DELETE FROM productos WHERE idProducto='" + datos[2] + "'";
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
