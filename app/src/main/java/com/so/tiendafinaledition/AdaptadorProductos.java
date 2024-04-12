package com.so.tiendafinaledition;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
public class AdaptadorProductos extends BaseAdapter {
    Context context;
    ArrayList<Producto> datosProductosArrayList;
    Producto datosProducto;
    LayoutInflater layoutInflater;
    public AdaptadorProductos(Context context, ArrayList<Producto> datosProductosArrayList) {
        this.context = context;
        this.datosProductosArrayList = datosProductosArrayList;
    }
    @Override
    public int getCount() {return datosProductosArrayList.size();}
    @Override
    public Object getItem(int i) {return datosProductosArrayList.get(i);}
    @Override
    public long getItemId(int i) {
        return i;//Long.parseLong(datosProductosArrayList.get(i).getIdProducto());}
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.listview_imagenes, viewGroup, false);
        try {
            datosProducto = datosProductosArrayList.get(i);

            TextView tempVal = itemView.findViewById(R.id.lblNombreProducto);
            tempVal.setText(datosProducto.getMarca());

            tempVal = itemView.findViewById(R.id.lblPrecioProducto);
            tempVal.setText(datosProducto.getPrecio());

            tempVal = itemView.findViewById(R.id.lblDescripcion);
            tempVal.setText(datosProducto.getDescripcion());

            tempVal = itemView.findViewById(R.id.lblPresentacion);
            tempVal.setText(datosProducto.getPresentacion());

            Bitmap imageBitmap = BitmapFactory.decodeFile(datosProducto.getUrlFotoProducto());
            ImageView img = itemView.findViewById(R.id.imgFotoProducto);
            img.setImageBitmap(imageBitmap);
        } catch (Exception e) {
            Toast.makeText(context, "Error al mostrar los datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return itemView;
    }
}
