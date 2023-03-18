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

public class ClienteActivity extends AppCompatActivity {

    EditText jetidentificacion,jetnombre,jetcorreo;
    CheckBox jcbactivo;
    String identificacion,nombre,correo;
    ClsOpenHelper admin=new ClsOpenHelper(this,"Concesionario.db",null,1);
    long respuesta;
    byte sw;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente);
        //Ocultar la barra de titulo por defecto y voy a asociar
        //objetos Java con Objetos Xml
        getSupportActionBar().hide();
        jetidentificacion=findViewById(R.id.etidentificacion);
        jetnombre=findViewById(R.id.etnombre);
        jetcorreo=findViewById(R.id.etcorreo);
        jcbactivo=findViewById(R.id.cbactivo);

        Button btcancelar = findViewById(R.id.button5);
        sw=0;

        btcancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jetidentificacion.setText("");
                jetnombre.setText("");
                jetcorreo.setText("");
                jcbactivo.setChecked(false);

                jetidentificacion.setEnabled(true);
                jetnombre.setEnabled(true);
                jetcorreo.setEnabled(true);

                jetidentificacion.requestFocus();

                sw=0;
            }
        });

    }

    public void Guardar(View view){
        identificacion=jetidentificacion.getText().toString();
        nombre=jetnombre.getText().toString();
        correo=jetcorreo.getText().toString();
        if (identificacion.isEmpty() || nombre.isEmpty() || correo.isEmpty()){
            Toast.makeText(this, "Todos los datos son requeridos", Toast.LENGTH_SHORT).show();
            jetidentificacion.requestFocus();
        }else{
            SQLiteDatabase db=admin.getWritableDatabase();
            ContentValues registro=new ContentValues();
            registro.put("identificacion",identificacion);
            registro.put("nombre",nombre);
            registro.put("correo",correo);
            if (sw==0)
                respuesta=db.insert("TblCliente",null,registro);
            else{
                respuesta=db.update("TblCliente",registro,"identificacion='"+identificacion+"'",null);
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
        identificacion=jetidentificacion.getText().toString();
        if (!identificacion.isEmpty()){
            SQLiteDatabase db=admin.getReadableDatabase();
            Cursor fila=db.rawQuery("select * from TblCliente where identificacion='"+identificacion+"'",null);
            if (fila.moveToNext()){
                sw=1;
                jetnombre.setText(fila.getString(1));
                jetcorreo.setText(fila.getString(2));
                if (fila.getString(3).equals("Si")){
                    jcbactivo.setChecked(true);
                }
                else {
                    jcbactivo.setChecked(false);
                }
                jetidentificacion.setEnabled(false);
                jetnombre.setEnabled(false);
                jetcorreo.setEnabled(false);

            }else{
                Toast.makeText(this, "Registro no hallado", Toast.LENGTH_SHORT).show();
            }
            db.close();
        }else{
            Toast.makeText(this, "Identificacion es requerida para consultar", Toast.LENGTH_SHORT).show();
            jetidentificacion.requestFocus();
        }
    }//Fin del consultar

    public void Anular(View view){

        SQLiteDatabase db=admin.getWritableDatabase();
        Cursor fila = db.rawQuery("select * from TblCliente where identificacion='" + identificacion + "'", null);
        if (fila.moveToNext()) {
            sw = 1;
            if (fila.getString(3).equals("Si") ) {
                ContentValues registro = new ContentValues();
                registro.put("activo", "No");
                respuesta = db.update("TblCliente", registro, "identificacion='" + identificacion + "'", null);
                jcbactivo.setChecked(false);
                Toast.makeText(this, "Registro anulado", Toast.LENGTH_SHORT).show();
            }
            else if(fila.getString(3).equals("No") ) {
                ContentValues registro = new ContentValues();
                registro.put("activo", "Si");
                respuesta = db.update("TblCliente", registro, "identificacion='" + identificacion + "'", null);
                jcbactivo.setChecked(true);
                Toast.makeText(this, "Registro activado", Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(this, "Registro no hallado", Toast.LENGTH_SHORT).show();
        }
        db.close();


    }//fin anular

    public void Cancelar(View view){
        limpiar_campos();
    }

    private void limpiar_campos(){
        jetidentificacion.setText("");
        jetnombre.setText("");
        jetcorreo.setText("");
        jcbactivo.setChecked(false);
        jetidentificacion.requestFocus();
        sw=0;
    }

    public void main(View view){
        Intent intmain=new Intent(this,MainActivity.class);
        startActivity(intmain);
    }

}