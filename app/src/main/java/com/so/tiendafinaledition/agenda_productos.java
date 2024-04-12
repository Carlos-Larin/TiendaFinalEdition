package com.so.tiendafinaledition;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.so.tiendafinaledition.DB;

import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import kotlin.contracts.Returns;
public class agenda_productos extends AppCompatActivity {
    Bundle parametros = new Bundle();
    DB db;
    ListView lts;
    Cursor cProductos;
    final ArrayList<Producto> alProductos = new ArrayList<>();
    final ArrayList<Producto> alProductosCopy = new ArrayList<>();
    Producto datosProducto;
    FloatingActionButton btn;
    JSONArray datosJSON; //para los datos que vienen del servidor.
    JSONObject jsonObject;
    obtenerDatosServidor datosServidor;
    detectarInternet di;
    int posicion = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agenda_productos);
        db = new DB(getApplicationContext(),"", null, 1);
        btn = findViewById(R.id.fabAgregarProductos);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parametros.putString("accion", "nuevo");
                abrirActividad(parametros);
            }
        });
        di = new detectarInternet(getApplicationContext());
        if( di.hayConexionInternet() ){
            obtenerDatosProductosServidor();
        }else{
            mostrarMsg("No hay conexion, datos en local");
            obtenerProductos();//offline
        }
        buscarProductos();
    }
    private void obtenerDatosProductosServidor(){
        try{
            datosServidor = new obtenerDatosServidor();
            String data = datosServidor.execute().get();

            jsonObject =new JSONObject(data);
            datosJSON = jsonObject.getJSONArray("rows");
            mostrarDatosProductos();
        }catch (Exception e){
            mostrarMsg("Error al obtener datos productos del server: "+ e.getMessage());
        }
    }
    private void mostrarDatosProductos(){
        try{
            if( datosJSON.length()>0 ){
                lts = findViewById(R.id.ltsProductos);
                alProductos.clear();
                alProductosCopy.clear();

                JSONObject misDatosJSONObject;
                for (int i=0; i<datosJSON.length(); i++){
                    misDatosJSONObject = datosJSON.getJSONObject(i).getJSONObject("value");
                    datosProducto = new Producto(
                            misDatosJSONObject.getString("_id"),
                            misDatosJSONObject.getString("_rev"),
                            misDatosJSONObject.getString("idProducto"),
                            misDatosJSONObject.getString("Marca"),
                            misDatosJSONObject.getString("Descripcion"),
                            misDatosJSONObject.getString("Presentacion"),
                            misDatosJSONObject.getString("Precio"),
                            misDatosJSONObject.getString("urlCompletaFoto")
                    );
                    alProductos.add(datosProducto);
                }
                alProductosCopy.addAll(alProductos);

                AdaptadorProductos adImagenes = new AdaptadorProductos(getApplicationContext(), alProductos);
                lts.setAdapter(adImagenes);

                registerForContextMenu(lts);
            }else{
                mostrarMsg("No hay datos que mostrar");
            }
        }catch (Exception e){
            mostrarMsg("Error al mostrar datos: "+ e.getMessage());
        }
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mimenu, menu);
        try {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            cProductos.moveToPosition(info.position);
            menu.setHeaderTitle("¿Qué deseas hacer con " + datosJSON.getJSONObject(posicion).getJSONObject("value").getString("marca"));
        }catch (Exception e){mostrarMsg("Error al mostrar el menu: "+ e.getMessage());}
    }
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        try {
            int itemId = item.getItemId();
            if (itemId == R.id.mnxAgregar) {
                parametros.putString("accion", "nuevo");
                abrirActividad(parametros);
            } else if (itemId == R.id.mnxModificar) {
                String[] productos = {
                        cProductos.getString(0), // idProducto
                        cProductos.getString(1), // marca
                        cProductos.getString(2), // presentacion
                        cProductos.getString(3), // descripcion
                        cProductos.getString(4), // precio
                        cProductos.getString(5)  // foto
                };
                parametros.putString("accion", "modificar");
                parametros.putStringArray("productos", new String[]{datosJSON.getJSONObject(posicion).toString()});
                abrirActividad(parametros);
            } else if (itemId == R.id.mnxEliminar) {
                eliminarProducto();
            }
            return true;
        } catch (Exception e) {
            mostrarMsg("Error al seleccionar el ítem: " + e.getMessage());
            return super.onContextItemSelected(item);
        }
    }
    private void eliminarProducto() {
        try {
            AlertDialog.Builder confirmar = new AlertDialog.Builder(agenda_productos.this);
            confirmar.setTitle("¿Está seguro de eliminar a: ");
            confirmar.setMessage(datosJSON.getJSONObject(posicion).getJSONObject("value").getString("marca"));

            confirmar.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    try {
                        String respuesta = db.administrar_productos("eliminar", new String[]{"", "", datosJSON.getJSONObject(posicion).getJSONObject("value").getString("idProducto")});
                        if (respuesta.equals("ok")) {
                            mostrarMsg("Producto eliminado con éxito");
                            obtenerProductos();
                        } else {
                            mostrarMsg("Error al eliminar el producto: " + respuesta);
                        }
                    }catch (Exception e){
                        mostrarMsg("Error al eliminar datos: "+ e.getMessage());
                    }
                }
            });

            confirmar.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            confirmar.create().show();
        } catch (Exception e) {
            mostrarMsg("Error al eliminar: " + e.getMessage());
        }
    }
    private void buscarProductos() {
        TextView tempVal = findViewById(R.id.txtBuscarProductos);
        tempVal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    alProductos.clear();
                    String valor = tempVal.getText().toString().trim().toLowerCase();
                    if (valor.length()<= 0) {
                        alProductos.addAll(alProductosCopy);
                    } else {
                        for (Producto producto : alProductosCopy) {
                            String descripcion = producto.getDescripcion();
                            String marca = producto.getMarca();
                            String presentacion = producto.getPresentacion();
                            if (descripcion.trim().toLowerCase().contains(valor) ||
                                    marca.trim().toLowerCase().contains(valor)||
                                    presentacion.trim().toLowerCase().contains(valor)) {
                                alProductos.add(producto);
                            }
                        }
                        AdaptadorProductos adImagenes = new AdaptadorProductos(getApplicationContext(), alProductos);
                        lts.setAdapter(adImagenes);
                    }
                } catch (Exception e) {
                    mostrarMsg("Error al buscar: " + e.getMessage());
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }
    private void abrirActividad(Bundle parametros) {
        Intent abrirActividad = new Intent(getApplicationContext(), MainActivity.class);
        abrirActividad.putExtras(parametros);
        startActivity(abrirActividad);
    }
    private void obtenerProductos() {
        try {
            db = new DB(getApplicationContext(), "", null, 1);
            cProductos = db.obtener_productos();
            if (cProductos.moveToFirst()) {
                datosJSON = new JSONArray();
                do {
                    jsonObject = new JSONObject();
                    JSONObject jsonObjectValue = new JSONObject();

                    jsonObject.put("_id", cProductos.getString(0));
                    jsonObject.put("_rev",cProductos.getString(1));
                    jsonObject.put("idProducto", cProductos.getString(2));
                    jsonObject.put("Marca", cProductos.getString(3));
                    jsonObject.put("Descripcion", cProductos.getString(4));
                    jsonObject.put("Presentacion", cProductos.getString(5));
                    jsonObject.put("precio", cProductos.getString(6));
                    jsonObject.put("urlCompletaFoto", cProductos.getString(7));
                    jsonObjectValue.put("value", jsonObject);

                    datosJSON.put(jsonObjectValue);
                }while (cProductos.moveToNext());
                mostrarDatosProductos();
                AdaptadorProductos adImagenes= new AdaptadorProductos(getApplicationContext(), alProductos);
                lts.setAdapter(adImagenes);
                registerForContextMenu(lts);
            } else {
                parametros.putString("accion", "nuevo");
                abrirActividad(parametros);
                mostrarMsg("No hay datos de productos.");
            }
        } catch (Exception e) {
            mostrarMsg("Error al obtener los productos: " + e.getMessage());
        }
    }
    private void mostrarMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
