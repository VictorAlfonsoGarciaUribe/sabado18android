package com.example.concesionario_sabado;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class VentaActivity extends AppCompatActivity {

    EditText jetidentificacion, jetnombre, jetplaca, jetmodelo, jetmarca, jetfactura, jetfecha;
    CheckBox jcbactivo;
    String identificacion, placa, factura, fecha, nombre, modelo, marca;
    ClsOpenHelper admin = new ClsOpenHelper(this, "Concesionario.db", null, 1);
    long respuesta, respuestab;
    byte sw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venta);
        //Ocultar la barra de titulo por defecto y voy a asociar
        //objetos Java con Objetos Xml
        getSupportActionBar().hide();
        jetidentificacion = findViewById(R.id.etidentificacion);
        jetnombre = findViewById(R.id.etnombre);

        Button btcancelar = findViewById(R.id.button5);

        btcancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jetidentificacion.setText("");
                jetnombre.setText("");
                jetplaca.setText("");
                jetmodelo.setText("");
                jetmarca.setText("");
                jetfecha.setText("");
                jetfactura.setText("");
                jcbactivo.setChecked(false);

                jetplaca.setEnabled(true);
                jetmodelo.setEnabled(true);
                jetmarca.setEnabled(true);
                jetidentificacion.setEnabled(true);
                jetnombre.setEnabled(true);

                sw = 0;
            }
        });


        //Vehiculo
        jetplaca = findViewById(R.id.etplaca);
        jetmodelo = findViewById(R.id.etmodelo);
        jetmarca = findViewById(R.id.etmarca);

        //Factura
        jetfactura = findViewById(R.id.etfactura);
        jetfecha = findViewById(R.id.etfecha);


        sw = 0;

   /*     //Spiner

//Spiner
        Spinner spinner = (Spinner) findViewById(R.id.spinner_identificaciones);

// Definir una lista para almacenar los datos
        List<String> identificaciones = new ArrayList<String>();

        try {
            // Consultar los datos de la columna "identificacion" de la tabla "TblCliente"
            String selectQuery = "SELECT identificacion FROM TblCliente";
            SQLiteDatabase db = admin.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            // Recorrer el cursor y agregar los datos a la lista
            if (cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(cursor.getColumnIndex("identificacion"));
                    identificaciones.add(id);
                } while (cursor.moveToNext());
            }

            // Cerrar el cursor y la base de datos
            cursor.close();
            db.close();

        } catch (Exception e) {
            Log.e("VentaActivity", "Error al obtener los datos de la base de datos", e);
            Toast.makeText(this, "Error al obtener los datos de la base de datos", Toast.LENGTH_SHORT).show();
        }
 */



    }

    public void Guardar(View view) {
        identificacion = jetidentificacion.getText().toString();
        placa = jetplaca.getText().toString();
        factura = jetfactura.getText().toString();
        fecha = jetfecha.getText().toString();

        if (identificacion.isEmpty() || placa.isEmpty() || factura.isEmpty() || fecha.isEmpty()) {
            Toast.makeText(this, "Todos los datos son requeridos", Toast.LENGTH_SHORT).show();
            jetidentificacion.requestFocus();
        } else {
            SQLiteDatabase db = admin.getWritableDatabase();
            ContentValues registro = new ContentValues();
            registro.put("identificacion", identificacion);
            registro.put("placa", placa);
            registro.put("codigo", factura);
            registro.put("fecha", fecha);
            sw = 1;
            respuesta = db.insert("TblVenta", null, registro);
            ContentValues registroa = new ContentValues();
            registroa.put("activo", "No");
            respuestab = db.update("TblVehiculo", registroa, "placa='" + placa + "'", null);

            Toast.makeText(this, "Vehiculo vendido", Toast.LENGTH_SHORT).show();
            db.close();
            Toast.makeText(this, "Facturado", Toast.LENGTH_SHORT).show();
        }
    }

    public void Consultar(View view) {
        //Validando que haya una identificacion
        identificacion = jetidentificacion.getText().toString();
        placa = jetplaca.getText().toString();
        factura = jetfactura.getText().toString();
        //Consulta factura  INNER JOIN

        if (!factura.isEmpty()) {

            factura = jetfactura.getText().toString();

            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT TblCliente.nombre, TblVehiculo.modelo, TblVehiculo.marca, TblVenta.fecha, TblCliente.identificacion, TblVehiculo.placa, TblVenta.activo  FROM TblVenta INNER JOIN TblCliente ON TblCliente.identificacion = TblVenta.identificacion INNER JOIN TblVehiculo ON TblVehiculo.placa = TblVenta.placa WHERE TblVenta.codigo='" + factura + "'", null);

            if (fila.moveToNext()) {
                jetnombre.setText(fila.getString(0));
                jetmodelo.setText(fila.getString(1));
                jetmarca.setText(fila.getString(2));
                jetfecha.setText(fila.getString(3));
                jetidentificacion.setText(fila.getString(4));
                jetplaca.setText(fila.getString(5));

                if (fila.getString(6).equals("Si")) {
                    jcbactivo.setChecked(true);

                } else {
                    jcbactivo.setChecked(false);
                }

            } else {
                Toast.makeText(this, "Factura no existe", Toast.LENGTH_SHORT).show();
            }
            db.close();

        } else {
            Toast.makeText(this, "Digite factura  ", Toast.LENGTH_SHORT).show();
        }


        //Consultar cliente
        SQLiteDatabase db = admin.getWritableDatabase();
        if (!identificacion.isEmpty()) {


            //Consulta cliente
            Cursor fila = db.rawQuery("select * from TblCliente where identificacion='" + identificacion + "'", null);

            if (fila.moveToNext()) {
                sw = 1;
                if (fila.getString(3).equals("Si")) {
                    //Consulta Cliente
                    jetnombre.setText(fila.getString(1));

                    jetidentificacion.setEnabled(false);
                    jetnombre.setEnabled(false);


                } else {
                    Toast.makeText(this, "Cliente inactivo", Toast.LENGTH_SHORT).show();
                    jetidentificacion.setText("");
                }
            } else {
                Toast.makeText(this, "Cliente no eciste ", Toast.LENGTH_SHORT).show();

                //Cierra base de datos
                db.close();
            }
        } else {
            Toast.makeText(this, "Digite CC  ", Toast.LENGTH_SHORT).show();

        }


        //Consultar vehiculo
        if (!placa.isEmpty()) {

            Cursor filab = db.rawQuery("select * from TblVehiculo where placa='" + placa + "'", null);

            if (filab.moveToNext()) {
                sw = 1;


                if (filab.getString(3).equals("Si")) {

                    //Consulta carro
                    jetmodelo.setText(filab.getString(1));
                    jetmarca.setText(filab.getString(2));

                    jetplaca.setEnabled(false);
                    jetmodelo.setEnabled(false);
                    jetmarca.setEnabled(false);
                } else {
                    Toast.makeText(this, "Vehiculo inactivo", Toast.LENGTH_SHORT).show();
                    jetidentificacion.setText("");
                }
            } else {
                Toast.makeText(this, "Vehiculo invalida ", Toast.LENGTH_SHORT).show();

                //Cierra base de datos

            }
        } else {
            Toast.makeText(this, "Digite la Placa ", Toast.LENGTH_SHORT).show();

        }
        db.close();
        sw = 0;
    }


    //Fin consultar



    public void limpiarCampos(View view) {
        jetidentificacion.setText("");
        jetnombre.setText("");
        jetplaca.setText("");
        jetmodelo.setText("");
        jetmarca.setText("");
        jetfecha.setText("");
        jetfactura.setText("");
        jcbactivo.setChecked(false);

        sw = 0;
    }

    public void main(View view){
        Intent intmain=new Intent(this,MainActivity.class);
        startActivity(intmain);
    }

}

