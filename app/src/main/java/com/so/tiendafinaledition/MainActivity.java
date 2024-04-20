package com.so.tiendafinaledition;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.json.JSONObject;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    Button btn;
    FloatingActionButton fab;
    TextView tempVal;
    String accion = "nuevo";
    String id="", rev="", idProducto="";
    String urlCompletaFoto;
    Intent tomarFotoIntent;
    ImageView img;
    utilidades utls;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        utls = new utilidades();
        fab = findViewById(R.id.fabListarProductos);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirActividad();
            }
        });
        btn = findViewById(R.id.btnGuardarProducto);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    tempVal = findViewById(R.id.txtMarca);
                    String marca = tempVal.getText().toString();

                    tempVal = findViewById(R.id.txtDescripcion);
                    String descripcion = tempVal.getText().toString();

                    tempVal = findViewById(R.id.txtPresentacion);
                    String presentacion = tempVal.getText().toString();

                    tempVal = findViewById(R.id.txtPrecio);
                    String precio = tempVal.getText().toString();

                    //guardar datos en el servidor
                    JSONObject datosProductos = new JSONObject();
                    if(accion.equals("modificar")){
                        datosProductos.put("_id", id);
                        datosProductos.put("_rev", rev);
                    }
                    datosProductos.put("idProducto", idProducto);
                    datosProductos.put("marca", marca);
                    datosProductos.put("descripcion", descripcion);
                    datosProductos.put("presentacion", presentacion);
                    datosProductos.put("precio", precio);
                    datosProductos.put("urlCompletaFoto", urlCompletaFoto);

                    String respuesta = "";
                    enviarDatosServidor objGuardarDatosServidor = new enviarDatosServidor(getApplicationContext());
                    respuesta = objGuardarDatosServidor.execute(datosProductos.toString()).get();

                    JSONObject respuestaJSONObject = new JSONObject(respuesta);
                    if( respuestaJSONObject.getBoolean("ok") ){
                        id = respuestaJSONObject.getString("id");
                        rev = respuestaJSONObject.getString("rev");
                    }else{
                        mostrarMsg("Error al guardar datos en el servidor");
                    }
                    DB db = new DB(getApplicationContext(), "",null, 1);
                    String[] datos = new String[]{id, rev,idProducto,marca,presentacion,descripcion,precio, urlCompletaFoto};
                    respuesta = db.administrar_productos(accion, datos);
                    if(respuesta.equals("ok")){
                        Toast.makeText(getApplicationContext(), "Producto guardado con exito", Toast.LENGTH_LONG).show();
                        abrirActividad();
                    }else{
                        Toast.makeText(getApplicationContext(), "Error al intentar guardar el producto: "+ respuesta, Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Error: "+ e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        img = findViewById(R.id.btnImgProducto);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tomarFotoProducto();
            }
        });
        mostrarDatosProducto();
    }
    private void tomarFotoProducto() {
        tomarFotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File fotoProducto = null;
        try{
            fotoProducto = crearImagenProducto();
            if( fotoProducto!=null ){
                Uri uriFotoproducto = FileProvider.getUriForFile(MainActivity.this,
                        "com.so.tiendafinaledition.fileprovider", fotoProducto);
                tomarFotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriFotoproducto);
                startActivityForResult(tomarFotoIntent, 1);
            }else{
                mostrarMsg("No se pudo creaar la foto");
            }
        }catch (Exception e){
            mostrarMsg("Error al abrir la camara: "+ e.getMessage());
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if(requestCode==1 && resultCode==RESULT_OK){
                Bitmap imageBitmap = BitmapFactory.decodeFile(urlCompletaFoto);
                img.setImageBitmap(imageBitmap);
            }else{
                mostrarMsg("El usuario cancelo la toma de la foto");
            }
        }catch (Exception e){
            mostrarMsg("Error al obtener la foto de la camara");
        }
    }
    private File crearImagenProducto() throws Exception{
        String fechaHoraMs = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()),
                fileName = "imagen_"+ fechaHoraMs +"_";
        File dirAlmacenamiento = getExternalFilesDir(Environment.DIRECTORY_DCIM);
        if( dirAlmacenamiento.exists()==false ){
            dirAlmacenamiento.mkdirs();
        }
        File imagen = File.createTempFile(fileName, ".jpg", dirAlmacenamiento);
        urlCompletaFoto = imagen.getAbsolutePath();
        return imagen;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void mostrarDatosProducto() {
        try {
            Bundle parametros = getIntent().getExtras();//Recibir los parametros...
            accion = parametros.getString("accion");

            if (accion.equals("modificar")){
                JSONObject jsonObject = new JSONObject(parametros.getString("productos")).getJSONObject("value");
                id = jsonObject.getString("_id");
                rev = jsonObject.getString("_rev");
                idProducto=jsonObject.getString("idProducto");

                tempVal = findViewById(R.id.txtMarca);
                tempVal.setText(jsonObject.getString("marca"));

                tempVal = findViewById(R.id.txtDescripcion);
                tempVal.setText(jsonObject.getString("descripcion"));

                tempVal = findViewById(R.id.txtPresentacion);
                tempVal.setText(jsonObject.getString("presentacion"));

                tempVal = findViewById(R.id.txtPrecio);
                tempVal.setText(jsonObject.getString("precio"));

                urlCompletaFoto = jsonObject.getString("urlCompletaFoto");
                Bitmap imageBitmap = BitmapFactory.decodeFile(urlCompletaFoto);
                img.setImageBitmap(imageBitmap);

            }else{//nuevo registro
                idProducto = utls.generarIdUnico();
            }
        }catch (Exception e){
            mostrarMsg("Error al mostrar datos: "+ e.getMessage());
        }
    }
    private void mostrarMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    private void abrirActividad() {
        Intent abrirActividad = new Intent(getApplicationContext(), agenda_productos.class);
        startActivity(abrirActividad);
    }
}
