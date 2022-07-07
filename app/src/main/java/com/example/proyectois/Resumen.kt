package com.example.proyectois

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import com.example.proyectois.clases.Paciente
import com.example.proyectois.utils.AdapterMedicinas
import com.example.proyectois.utils.AdapterVitales
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_resumen.*
import kotlinx.android.synthetic.main.activity_signos_vitales.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import kotlin.math.log


class Resumen : AppCompatActivity() {

    private val llave = "LaLlave"
    private lateinit var prefs : SharedPreferences
    private lateinit var nombre : TextView
    private lateinit var apellidoP : TextView
    private lateinit var apellidoM : TextView
    private lateinit var tipoSanguineo : TextView
    private lateinit var cronicos : TextView
    private lateinit var alergiasC : TextView
    private lateinit var alergiasM : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resumen)

        nombre = findViewById(R.id.vieNombreResumen)
        apellidoP = findViewById(R.id.apellidoP_Resumen)
        apellidoM = findViewById(R.id.apellidoM_Resumen)
        tipoSanguineo = findViewById(R.id.tipoSanguineo_Resumen)
        cronicos = findViewById(R.id.cronicos_Resumen)
        alergiasC = findViewById(R.id.alergias_comunes_resumen)
        alergiasM = findViewById(R.id.alergias_medicamentos_Resumen)

        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        if(prefs.getInt(llave, -1)==-1){
            startActivity(Intent(this, LoginActivity::class.java))
        }else{
            val gson = Gson()
            val Pacientes = ArrayList<Paciente>()

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

                nombre.text = nombre.text.toString() + Pacientes[prefs.getInt(llave, -1)].nombre
                apellidoP.text = apellidoP.text.toString() + Pacientes[prefs.getInt(llave, -1)].apellidoP
                apellidoM.text = apellidoM.text.toString() + Pacientes[prefs.getInt(llave, -1)].apellidoM
                tipoSanguineo.text = tipoSanguineo.text.toString() + Pacientes[prefs.getInt(llave, -1)].tipoSanguineo
                if(Pacientes[prefs.getInt(llave, -1)].tipoCronico == ""){
                    cronicos.text = cronicos.text.toString() + "Sin padecimientos cronicos registrados"
                }else{
                    cronicos.text = cronicos.text.toString() + Pacientes[prefs.getInt(llave, -1)].tipoCronico
                }

                if(Pacientes[prefs.getInt(llave, -1)].alergiasComunes == ""){
                    alergiasC.text = alergiasC.text.toString() + "Sin alergias registradas"
                }else{
                    alergiasC.text = alergiasC.text.toString() + Pacientes[prefs.getInt(llave, -1)].alergiasComunes
                }

                if(Pacientes[prefs.getInt(llave, -1)].alergiasMedicamentos == ""){
                    alergiasM.text = alergiasM.text.toString() + "Sin alergias registradas"
                }else{
                    alergiasM.text = alergiasM.text.toString() + Pacientes[prefs.getInt(llave, -1)].alergiasMedicamentos
                }

                if(Pacientes[prefs.getInt(llave,-1)].vitales != null){
                    List_Vitales_Resumen.adapter= Pacientes[prefs.getInt(llave,-1)].vitales?.let {
                        AdapterVitales(
                            this, R.layout.row_vitales, it
                        )
                    }
                }
                if(Pacientes[prefs.getInt(llave,-1)].tratamientos != null){
                    val ultimo  = Pacientes[prefs.getInt(llave,-1)].tratamientos!!.size
                    Log.d("Ultimo", ""+ Pacientes[prefs.getInt(llave,-1)].tratamientos!![ultimo-1].nombre)
                    val medicinaz = Pacientes[prefs.getInt(llave,-1)].tratamientos!![ultimo-1].medicinas
                    if (medicinaz != null){
                        Log.d("medizinas", medicinaz[0].nombre)
                        List_medicinas_Resumen.adapter= medicinaz.let {
                            AdapterMedicinas(
                                this, R.layout.row_medicinas, it
                            )
                        }
                    }

                }
            }catch (e: Exception){
                Log.d("Error", e.toString())
                Toast.makeText(applicationContext, "Error al cargar", Toast.LENGTH_LONG).show()
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