package com.example.concesionario_sabado;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class VehiculoActivity extends AppCompatActivity {

    EditText jetplaca,jetmodelo,jetmarca;
    CheckBox jcbactivo;
    String placa,modelo,marca;
    ClsOpenHelper admin=new ClsOpenHelper(this,"Concesionario.db",null,1);
    long respuesta;
    byte sw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehiculo);

        //Ocultar la barra de titulo por defecto y voy a asociar
        //objetos Java con Objetos Xml
        getSupportActionBar().hide();
        jetplaca = findViewById(R.id.etplaca);
        jetmodelo=findViewById(R.id.etmodelo);
        jetmarca=findViewById(R.id.etmarca);
        jcbactivo=findViewById(R.id.cbactivo2);

        Button btcancelar = findViewById(R.id.button5);

        sw=0;

        btcancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jetplaca.setText("");
                jetmodelo.setText("");
                jetmarca.setText("");
                jcbactivo.setChecked(false);
                jetplaca.requestFocus();

                jetplaca.setEnabled(true);
                jetmodelo.setEnabled(true);
                jetmarca.setEnabled(true);

                sw=0;


            }
        });
    }

    public void Guardar(View view){
        placa=jetplaca.getText().toString();
        modelo=jetmodelo.getText().toString();
        marca=jetmarca.getText().toString();
        if (placa.isEmpty() || modelo.isEmpty() || marca.isEmpty()){
            Toast.makeText(this, "Todos los datos son requeridos", Toast.LENGTH_SHORT).show();
            jetplaca.requestFocus();
        }else{

            //Siempre debe validar que nos e cambie placa o el identificación
            SQLiteDatabase db=admin.getWritableDatabase();
            ContentValues registro=new ContentValues();
            registro.put("placa",placa);
            registro.put("modelo",modelo);
            registro.put("marca",marca);
            if (sw==0)
                respuesta=db.insert("TblVehiculo",null,registro);
            else{
                respuesta=db.update("TblVehiculo",registro,"placa='"+placa+"'",null);
                sw=0;
            }

            if (respuesta == 0){
                Toast.makeText(this, "Error guardando registro", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Registro guardado", Toast.LENGTH_SHORT).show();
                limpiar_campos();
            }
            db.close();
        }
    }//fin Metodo de guardar

    public void Consultar(View view){
        //Validando que haya una identificacion
        placa=jetplaca.getText().toString();
        if (!placa.isEmpty()){
            SQLiteDatabase db=admin.getReadableDatabase();
            Cursor fila=db.rawQuery("select * from TblVehiculo where placa='"+placa+"'",null);
            if (fila.moveToNext()) {
                sw = 1;
                jetmodelo.setText(fila.getString(1));
                jetmarca.setText(fila.getString(2));
                if (fila.getString(3).equals("Si")) {
                    jcbactivo.setChecked(true);

                } else {
                    jcbactivo.setChecked(false);
                }

                jetplaca.setEnabled(false);
                jetmodelo.setEnabled(false);
                jetmarca.setEnabled(false);
            }
            else{
                Toast.makeText(this, "Registro no hallado", Toast.LENGTH_SHORT).show();
            }
            db.close();
        }else{
            Toast.makeText(this, "Identificacion es requerida para consultar", Toast.LENGTH_SHORT).show();
            jetplaca.requestFocus();
        }
    }//Fin del consultar

    public void Anular(View view){
        SQLiteDatabase db=admin.getWritableDatabase();
        Cursor fila = db.rawQuery("select * from TblVehiculo where placa='" + placa + "'", null);
        if (fila.moveToNext()) {
            sw = 1;
            if (fila.getString(3).equals("Si") ) {
                ContentValues registro = new ContentValues();
                registro.put("activo", "No");
                respuesta = db.update("TblVehiculo", registro, "placa='" + placa + "'", null);
                jcbactivo.setChecked(false);
                Toast.makeText(this, "Registro anulado", Toast.LENGTH_SHORT).show();
            }
            else if(fila.getString(3).equals("No") ) {
                ContentValues registro = new ContentValues();
                registro.put("activo", "Si");
                respuesta = db.update("TblVehiculo", registro, "placa='" + placa + "'", null);
                jcbactivo.setChecked(true);
                Toast.makeText(this, "Registro activado", Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(this, "Vehiculo no hallado", Toast.LENGTH_SHORT).show();
        }
        db.close();
        }
//fin anular





    private void limpiar_campos(){
        jetplaca.setText("");
        jetmodelo.setText("");
        jetmarca.setText("");
        jcbactivo.setChecked(false);
        jetplaca.requestFocus();
        sw=0;
    }

    public void main(View view){
        Intent intmain=new Intent(this,MainActivity.class);
        startActivity(intmain);
    }
}