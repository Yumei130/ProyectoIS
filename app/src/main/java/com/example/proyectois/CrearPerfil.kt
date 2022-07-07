package com.example.proyectois

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.example.proyectois.clases.Paciente
import com.google.gson.Gson
import java.io.*


class CrearPerfil : AppCompatActivity() {

        private lateinit var salir : Button
        private lateinit var guardar : Button

        private lateinit var arregloSangres : AutoCompleteTextView
        private lateinit var arregloCronos : AutoCompleteTextView

        private lateinit var nombreLayout: EditText
        private lateinit var paternoLayout: EditText
        private lateinit var maternoLayout: EditText
        private lateinit var telefonolayout: EditText
        private lateinit var claveLayout: EditText
        private lateinit var confirmaLayout: EditText
        private lateinit var alergiasC: EditText
        private lateinit var alergiasM: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_perfil)
        val Pacientes = ArrayList<Paciente>()

        // Configuracion del array adapter de tipos sanguineos - Esto es para que haga funcion de cajita
        val sangres = resources.getStringArray(R.array.tiposSanguineos)
        val arrayAdapterSangre = ArrayAdapter(this, R.layout.dropdown_tipos_sangre, sangres)
        arregloSangres= findViewById(R.id.seleccionSanguineoCreaP)
        arregloSangres.setAdapter(arrayAdapterSangre)

        //Array adapter de enfermedades crónicas.
        val cronicos = resources.getStringArray(R.array.padecimientosCronicos)
        val arrayAdapterCronicos = ArrayAdapter(this, R.layout.dropdown_tipos_cronicos, cronicos)
        arregloCronos = findViewById(R.id.seleccionCronicoCreaP)
        arregloCronos.setAdapter(arrayAdapterCronicos)

        //Configuracion de boton de salir de la pantalla de crear perfil
        salir = findViewById(R.id.salirCrear)
        salir.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
        }

 //Configuración del Botón de Guardar de la pantalla de Crear Perfil -------------------------------
 //-------------------------------------------------------------------------------------------------
        guardar = findViewById(R.id.guardarCrear)
        guardar.setOnClickListener{
//Este primer Try verifica que las casillas indispensables no esten vacias y que las claves coincidan
            try {
                nombreLayout = findViewById(R.id.nombre_CreaP)
                paternoLayout = findViewById(R.id.apellidoP_CreaP)
                maternoLayout = findViewById(R.id.apellidoM_CreaP)
                telefonolayout = findViewById(R.id.Correo_CreaP)
                claveLayout = findViewById(R.id.Clave_CreaP)
                confirmaLayout = findViewById(R.id.ConfirmaContrasena_CreaP)
                alergiasC = findViewById(R.id.alergias_comunes_CreaP)
                alergiasM= findViewById(R.id.alergias_medicamentosCreaP)

                val paciente = Paciente(
                nombreLayout.getText().toString(),
                paternoLayout.getText().toString(),
                maternoLayout.getText().toString(),
                telefonolayout.getText().toString(),
                claveLayout.getText().toString(),
                arregloSangres.getText().toString(),
                arregloCronos.getText().toString(),
                    alergiasC.getText().toString(),
                    alergiasM.getText().toString(),
                    null,
                    null
                )
                if (paciente.nombre == "" || paciente.apellidoP == "" || paciente.apellidoM =="" ||
                    paciente.Correo == "" || paciente.contraseña == "" || paciente.tipoSanguineo ==""){
                    throw Exception("Hacen falta datos Escenciales")
                }
                val confirma = confirmaLayout.getText().toString()
                if (confirma != paciente.contraseña){
                    throw Exception("Las contraseñas no coinciden")
                }

                val gson = Gson()
                var json:String = gson.toJson(paciente)


                Log.d("Paciente", json)

 //Se verifica que el usuario no exista ------------------------------------------------------------
 //-------------------------------------------------------------------------------------------------
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
                    for (n:Paciente in PacientesJson){
                        Pacientes.add(n)
                    }
                        for (n:Paciente in Pacientes){
                            if (paciente.Correo == n.Correo ){
                                throw Exception("Ya existe")
                            }
                        }
                        Pacientes.add(paciente)
                }
                catch (e: Exception){
                    Log.d("Error:", e.toString())
                    if (e.message == "Ya existe"){
                        throw Exception("Usuario ya registrado")
                    }else if(Pacientes.size==0){
                        Pacientes.add(paciente)
                    }
                }

  //Este try verifica que Se almaceno correctamente ------------------------------------------------
  //------------------------------------------------------------------------------------------------
                try {

                    json = gson.toJson(Pacientes)
                    Log.d("NombrePaciente" , Pacientes[0].nombre)
                    Log.d("Pacientes JSON> ", json)
                    val archivo = OutputStreamWriter(openFileOutput("pacientes.txt", MODE_PRIVATE))
                    archivo.write(json)
                    archivo.close()

                }catch (e: Exception){
                    Log.d("error:", e.toString())
                    throw Exception("Ocurrio un error al guardar")
                }

                Toast.makeText(applicationContext, "Transacción Exitosa", Toast.LENGTH_LONG).show()
                //finish()
                startActivity(Intent(this, LoginActivity::class.java))
 //-------------------------------------------------------------------------------------------------

            }catch (e: Exception){
                Log.d("Error", e.toString())
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

}