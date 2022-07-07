package com.example.proyectois

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import com.example.proyectois.clases.Paciente
import com.example.proyectois.clases.Tratamiento
import com.example.proyectois.utils.AdapterTratamiento
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_tratamiento.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class TratamientoActivity : AppCompatActivity() {


    private lateinit var nombreTratamiento : EditText
    private lateinit var fechaTratamiento : EditText
    private lateinit var nombreMedico : EditText
    private lateinit var listaTratamientos : ListView
    private lateinit var Tratamientos2 : ArrayList<Tratamiento>
    private lateinit var prefs : SharedPreferences
    private val llave = "LaLlave"
    lateinit var btnAgregar : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tratamiento)



//Se busca en preferencias al usuario---------------------------------------------------------------
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        if(prefs.getInt(llave, -1)==-1){
            startActivity(Intent(this, LoginActivity::class.java))

        }else{
            val gson = Gson()
            val Pacientes = ArrayList<Paciente>()
        try {
//Se intenta leer el archivo y se obtiene el arreglo de pacientes-----------------------------------
            val file = InputStreamReader(openFileInput("pacientes.txt"))
            val bfr = BufferedReader(file)
            var linea = bfr.readLine()
            var texto = ""
            while (linea!=null){
                texto+=linea
                linea = bfr.readLine()
                                }
            val PacientesJson = gson.fromJson(texto, Array<Paciente>::class.java)
            for (n: Paciente in PacientesJson){
                Pacientes.add(n)
            }
//Si existe algo dentro del Gson se manda al adapter para obtener el view---------------------------
            if(Pacientes[prefs.getInt(llave,-1)].tratamientos != null){
                listTratamientos.adapter= Pacientes[prefs.getInt(llave,-1)].tratamientos?.let {
                    AdapterTratamiento(
                        this, R.layout.row_tratamiento,it
                    )
                }
                Tratamientos2= Pacientes[prefs.getInt(llave,-1)].tratamientos!!
            }
//--------------------------------------------------------------------------------------------------
            }catch (e:Exception){
            Log.d("Error", e.toString())
            Toast.makeText(applicationContext, "Error al cargar", Toast.LENGTH_LONG).show()
            }


//Listener para el botón ---------------------------------------------------------------------------
            btnAgregar= findViewById(R.id.btnAddTrat)
            btnAgregar.setOnClickListener {

                nombreTratamiento= findViewById(R.id.nombreAddTrat)
                fechaTratamiento= findViewById(R.id.fechaAddTrat)
                nombreMedico= findViewById(R.id.medicoAddTrat)
                val nombre = nombreTratamiento.getText().toString()
                val fechaObtenida = fechaTratamiento.getText().toString()
                val medico = nombreMedico.getText().toString()
                val tratamientoActual = Tratamiento(nombre,null,fechaObtenida,medico)
                var Tratamientos = ArrayList<Tratamiento>()
                if(Pacientes[prefs.getInt(llave,-1)].tratamientos==null){
                    Tratamientos.add(tratamientoActual)
                }else {
                    Tratamientos = Pacientes[prefs.getInt(llave,-1)].tratamientos!!
                    Tratamientos.add(tratamientoActual)
                }


                Pacientes[prefs.getInt(llave,-1)].tratamientos = Tratamientos
                Tratamientos2=Tratamientos
                try {
                    val json = gson.toJson(Pacientes)
                    val archivo = OutputStreamWriter(openFileOutput("pacientes.txt", MODE_PRIVATE))
                    archivo.write(json)
                    archivo.close()
                    //Falta hacer el ajuste de refresh.
                    startActivity(Intent(this, TratamientoActivity::class.java))
                    finish()
                }
                catch (e: java.lang.Exception){
                    Toast.makeText(applicationContext, "Error al Guardar", Toast.LENGTH_LONG).show()
                    Log.d("Error", e.toString())
                }

            }


        }

//--------------------------------------------------------------------------------------------------

//Listener para el ListView ---------------------------------------------------------------------------

        listaTratamientos = findViewById(R.id.listTratamientos)
        listaTratamientos.setOnItemClickListener { adapterView, view, i, l ->
            var conta: Int=0;
            for(n: Tratamiento in Tratamientos2){
                conta++;
                if(i+1==conta){
                    val next = Intent(this,Medicinas::class.java)
                    next.putExtra("nombreTrat",n.nombre)
                    next.putExtra("fechaTrat",n.fecha)
                    next.putExtra("index",i)
                    startActivity(next)
                }
            }
        }







//--------------------------------------------------------------------------------------------------


    }



    //Configuración del Menu -----------------------------------------------------------------------
    // Crea al menu de forma visible
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }
    // Agrega las funcionalidades de los items del menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.signosVitales -> otroActivity(Intent(this, SignosVitales::class.java))
            R.id.resumen -> otroActivity(Intent(this, Resumen::class.java))
            R.id.perfilUsuario -> otroActivity(Intent(this, EdicionPerfil::class.java))
            R.id.medicinas -> otroActivity(Intent(this, TratamientoActivity::class.java))
            R.id.salirSesion -> salir()
        }
        return super.onOptionsItemSelected(item)
    }
    fun salir(){
        val editor = prefs.edit()
        editor.remove(llave)
        editor.apply()
        finish()
        startActivity(Intent(this, LoginActivity::class.java))
    }
    fun otroActivity( clase : Intent){
        //finish()
        startActivity(clase)
    }
//--------------------------------------------------------------------------------------------------
}