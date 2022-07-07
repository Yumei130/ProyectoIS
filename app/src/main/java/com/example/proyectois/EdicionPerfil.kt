package com.example.proyectois

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import com.example.proyectois.clases.Paciente
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_edicion_perfil.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.lang.Exception


class EdicionPerfil : AppCompatActivity() {

    private lateinit var arregloSangres : AutoCompleteTextView
    private lateinit var arregloCronos : AutoCompleteTextView

    private lateinit var nombre: EditText
    private lateinit var apellidoP : EditText
    private lateinit var apellidoM : EditText
    private lateinit var clave : EditText
    private lateinit var confirmar : EditText
    private lateinit var alergiasC : EditText
    private lateinit var alergiasM : EditText

    private lateinit var cancelar : Button
    private lateinit var guardar: Button
    private lateinit var prefs : SharedPreferences

    private val llave = "LaLlave"

    override fun onResume() {
        super.onResume()

// Configuracion del array adapter de tipos sanguineos - Esto es para que haga funcion de cajita----
        val sangres = resources.getStringArray(R.array.tiposSanguineos)
        val arrayAdapterSangre = ArrayAdapter(this, R.layout.dropdown_tipos_sangre, sangres)
        arregloSangres.setAdapter(arrayAdapterSangre)
//--------------------------------------------------------------------------------------------------

//Array adapter de enfermedades crónicas.-----------------------------------------------------------
        val cronicos = resources.getStringArray(R.array.padecimientosCronicos)
        val arrayAdapterCronicos = ArrayAdapter(this, R.layout.dropdown_tipos_cronicos, cronicos)
        arregloCronos.setAdapter(arrayAdapterCronicos)
//--------------------------------------------------------------------------------------------------
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edicion_perfil)

        nombre = findViewById(R.id.nombre_editaP)
        apellidoP = findViewById(R.id.apellidoP_editaP)
        apellidoM = findViewById(R.id.apellidoM_editaP)
        clave = findViewById(R.id.Clave_editaP)
        confirmar = findViewById(R.id.ConfirmaContrasena_editaP)
        alergiasC = findViewById(R.id.alergias_comunes_editaP)
        alergiasM = findViewById(R.id.alergias_medicamentoseditaP)
        arregloSangres = findViewById(R.id.seleccionSanguineoeditaP)
        arregloCronos = findViewById(R.id.seleccionCronicoeditaP)
        cancelar = findViewById(R.id.cancelarCambios)
        guardar = findViewById(R.id.guardarCambios)

        val gson = Gson()
        val Pacientes = ArrayList<Paciente>()

        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        if(prefs.getInt(llave, -1)==-1){
            startActivity(Intent(this, LoginActivity::class.java))
        }else{

 //Carga los datos del Usuario para que sean visibles en el editor de perfil------------------------
        //Obtiene los datos almacenados ---------------------------------------------------------------
            try {
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
            }catch (e: Exception){
                Log.d("Error", e.toString())
                Toast.makeText(applicationContext, "Error al cargar", Toast.LENGTH_LONG).show()
            }
        //------------------------------------------------------------------------------------------
        //Carga los datos a la pantalla ------------------------------------------------------------
            nombre.setText(Pacientes[prefs.getInt(llave, -1)].nombre)
            apellidoP.setText(Pacientes[prefs.getInt(llave, -1)].apellidoP)
            apellidoM.setText(Pacientes[prefs.getInt(llave, -1)].apellidoM)
            clave.setText(Pacientes[prefs.getInt(llave, -1)].contraseña)
            confirmar.setText(Pacientes[prefs.getInt(llave, -1)].contraseña)
            arregloSangres.setText(Pacientes[prefs.getInt(llave, -1)].tipoSanguineo)
            arregloCronos.setText(Pacientes[prefs.getInt(llave, -1)].tipoCronico)
            alergiasC.setText(Pacientes[prefs.getInt(llave, -1)].alergiasComunes)
            alergiasM.setText(Pacientes[prefs.getInt(llave, -1)].alergiasMedicamentos)
        //------------------------------------------------------------------------------------------
        }
 //-------------------------------------------------------------------------------------------------

    //Cancela los cambioes de datos y reestablece los datos a su version anterion ------------------
        cancelar.setOnClickListener(){
            nombre.setText(Pacientes[prefs.getInt(llave, -1)].nombre)
            apellidoP.setText(Pacientes[prefs.getInt(llave, -1)].apellidoP)
            apellidoM.setText(Pacientes[prefs.getInt(llave, -1)].apellidoM)
            clave.setText(Pacientes[prefs.getInt(llave, -1)].contraseña)
            confirmar.setText(Pacientes[prefs.getInt(llave, -1)].contraseña)
            arregloSangres.setText(Pacientes[prefs.getInt(llave, -1)].tipoSanguineo)
            arregloCronos.setText(Pacientes[prefs.getInt(llave, -1)].tipoCronico)
            alergiasC.setText(Pacientes[prefs.getInt(llave, -1)].alergiasComunes)
            alergiasM.setText(Pacientes[prefs.getInt(llave, -1)].alergiasMedicamentos)
            Toast.makeText(applicationContext, "Se restablecieron los datos anteriores", Toast.LENGTH_LONG).show()
        }
    //----------------------------------------------------------------------------------------------

        guardar.setOnClickListener(){
            if (confirmar.getText().toString() == clave.getText().toString()){
                Pacientes[prefs.getInt(llave, -1)].nombre = nombre.getText().toString()
                Pacientes[prefs.getInt(llave, -1)].apellidoP = apellidoP.getText().toString()
                Pacientes[prefs.getInt(llave, -1)].apellidoM = apellidoM.getText().toString()
                Pacientes[prefs.getInt(llave, -1)].contraseña = clave.getText().toString()
                Pacientes[prefs.getInt(llave, -1)].tipoSanguineo = arregloSangres.getText().toString()
                Pacientes[prefs.getInt(llave, -1)].tipoCronico = arregloCronos.getText().toString()
                Pacientes[prefs.getInt(llave, -1)].alergiasComunes = alergiasC.getText().toString()
                Pacientes[prefs.getInt(llave, -1)].alergiasMedicamentos = alergiasM.getText().toString()

                try {
                    val json = gson.toJson(Pacientes)
                    val archivo = OutputStreamWriter(openFileOutput("pacientes.txt", MODE_PRIVATE))
                    archivo.write(json)
                    archivo.close()
                }
                catch (e: Exception){
                    Toast.makeText(applicationContext, "Error al Guardar", Toast.LENGTH_LONG).show()
                    Log.d("Error", e.toString())
                }
                Toast.makeText(applicationContext, "Se guardo con éxito", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(applicationContext, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show()
            }

        }
    }

//Configuración del Menu ---------------------------------------------------------------------------
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
        //finish()
        startActivity(Intent(this, LoginActivity::class.java))
    }
    fun otroActivity( clase : Intent){
        //finish()
        startActivity(clase)
    }
//--------------------------------------------------------------------------------------------------
}