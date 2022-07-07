package com.example.proyectois


import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectois.clases.Paciente
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception


class LoginActivity : AppCompatActivity(){

    private lateinit var btnSiguiente : Button
    private lateinit var btnEntrar : Button

    private lateinit var correo : EditText
    private lateinit var clave : EditText

    private lateinit var incorrecto : TextView

    private val llave = "LaLlave"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnEntrar =findViewById(R.id.button3)
        correo = findViewById(R.id.correo_Login)
        clave = findViewById(R.id.clave_Login)
        incorrecto = findViewById(R.id.errorLogin)

//Configuración del botón de Crear Perfil----------------------------------------------------------
        btnSiguiente = findViewById(R.id.creaCuenta)
        btnSiguiente.setOnClickListener{
            startActivity(Intent(this, CrearPerfil::class.java))
        }
//-------------------------------------------------------------------------------------------------


//Se declara un objeto preferencias para configurar las preferencias.------------------------------
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        //Obtienene el contenido del preferences en la llave específicada
        //Siempre debe ir anotado el valor por defecto
        prefs.getInt(llave, -1)

        //Si el valor de preferencias es el pordefecto, inicia el proceso de Login.
        if(prefs.getInt(llave, -1)==-1){
            btnEntrar.setOnClickListener{
                val gson = Gson()
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
                    var nPaciente = 0
                    for (n: Paciente in PacientesJson){
                        //Si la clave es correcta, reemplaza el valor de las preferencias y lanza el activity.
                        if (n.Correo == correo.text.toString() && n.contraseña == clave.text.toString()){
                            val editor = prefs.edit()
                            editor.putInt(llave, nPaciente)
                            editor.apply()
                            val intent = Intent(this,bienvenida::class.java)
                            finish()
                            startActivity(intent)
                        }
                        else{
                            incorrecto.setVisibility(View.VISIBLE)
                        }

                    }

                }catch (e: Exception){
                    incorrecto.setVisibility(View.VISIBLE)
                    print( e.message)
                    //Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
                }
            }
            //-------------------------------------------------------------------------------------------------

        }else{
            //Si tiene un valor que no es el default, accede directamente y se salta el LogIn -----------------
            val intent = Intent(this,bienvenida::class.java)
            finish()
            startActivity(intent)
        }

    }
}