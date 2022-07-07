package com.example.proyectois

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.*
import com.example.proyectois.clases.AlarmReceiver
import com.example.proyectois.clases.Medicamento
import com.example.proyectois.clases.Paciente
import com.example.proyectois.clases.Tratamiento
import com.example.proyectois.utils.AdapterMedicinas
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_medicinas.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.min

class AgregarMedicina : AppCompatActivity() {
    lateinit var btnHora : Button
    lateinit var cancelar : Button
    lateinit var guardar : Button

    lateinit var nombreMedicina : EditText
    lateinit var tipoMedicina : EditText
    lateinit var cantidadMedicina : EditText

    lateinit var horaSeleccionada : TextView
    lateinit var arregloRepeticion : AutoCompleteTextView
    var hora : Int =69
    var minuto : Int=69
    private lateinit var picker : MaterialTimePicker
    private lateinit var calendar : Calendar
    private lateinit var alarmManager: AlarmManager
    private lateinit var  pendingIntent: PendingIntent

    private lateinit var nombreTrat : String
    private lateinit var fechaTrat : String
    private var index : Int=0
    private val random = (0..2147483647).random()


    lateinit var tratamientoActual : Tratamiento
    lateinit var arrayMedicinasActual : ArrayList<Medicamento>
    private lateinit var prefs : SharedPreferences
    private val llave = "LaLlave"

    private val Pacientes = ArrayList<Paciente>()
    private val gson = Gson()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregarmedicina)

        createNotificationChannel()

        nombreTrat = intent.getStringExtra("nombreTrat").toString()
        fechaTrat = intent.getStringExtra("fechaTrat").toString()
        index = intent.getIntExtra("index",0)

        arrayMedicinasActual = ArrayList<Medicamento>()

        nombreMedicina = findViewById(R.id.nombreAddMed)
        tipoMedicina = findViewById(R.id.tipoAddMed)
        cantidadMedicina = findViewById(R.id.dosis)

        val repet = resources.getStringArray(R.array.repeticionArray)
        val arrayAdapterRepeticion = ArrayAdapter(this, R.layout.dropdown_medible, repet)
        arregloRepeticion = findViewById(R.id.tipoRepeticion)
        arregloRepeticion.setAdapter(arrayAdapterRepeticion)

//Se busca en preferencias al usuario---------------------------------------------------------------
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        if(prefs.getInt(llave, -1)==-1){
            startActivity(Intent(this, LoginActivity::class.java))
        }else{


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

                var arrayTratamientos = Pacientes[prefs.getInt(llave,-1)].tratamientos

                if (arrayTratamientos != null) {
                    for(n: Tratamiento in arrayTratamientos){
                        if(n.nombre==nombreTrat && n.fecha==fechaTrat){
                            arrayMedicinasActual=n.medicinas!!
                        }

                    }
                }else{
                    Toast.makeText(applicationContext, "No hay medicinas", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this,TratamientoActivity::class.java))
                }


//--------------------------------------------------------------------------------------------------
            }catch (e:Exception){
                Log.d("Error", e.toString())
            }




        }


        cancelar= findViewById(R.id.btnCancelarMed)
        cancelar.setOnClickListener {
            finish()
        }


        horaSeleccionada = findViewById(R.id.lblAddMedTimeSelected)



        btnHora = findViewById(R.id.btnSeleccionarHora)
        btnHora.setOnClickListener {
            showTimePicker()

        }

//BOTÓN GUARDAR, AHORA SÍ VIENE LO CHIDO------------------------------------------------------------
        guardar= findViewById(R.id.btnGuardarMed)
        guardar.setOnClickListener {
        if(nombreMedicina.text!=null && tipoMedicina.text!=null && arregloRepeticion.getText() !=null && cantidadMedicina.text!=null && hora!=69 && minuto!=69) {

            val medicinaAgregar = Medicamento(
               nombreMedicina.text.toString(),
               tipoMedicina.text.toString(),
               arregloRepeticion.getText().toString(),
               String.format("%02d", hora) + ":" + String.format("%02d", minuto),
               cantidadMedicina.text.toString(),
                random
               )

            arrayMedicinasActual.add(medicinaAgregar)
            setAlarm(medicinaAgregar)

            var arrayTratamientos = Pacientes[prefs.getInt(llave,-1)].tratamientos

            if (arrayTratamientos != null) {
                for(n: Tratamiento in arrayTratamientos){
                    if(n.nombre==nombreTrat && n.fecha==fechaTrat){
                        n.medicinas = arrayMedicinasActual
                    }

                }
            }else{
                Toast.makeText(applicationContext, "Error", Toast.LENGTH_LONG).show()
                startActivity(Intent(this,TratamientoActivity::class.java))
            }

            try {
                val json = gson.toJson(Pacientes)
                val archivo = OutputStreamWriter(openFileOutput("pacientes.txt", MODE_PRIVATE))
                archivo.write(json)
                archivo.close()
                //Falta hacer el ajuste de refresh.
                startActivity(Intent(this, TratamientoActivity::class.java))
            }
            catch (e: java.lang.Exception){
                Toast.makeText(applicationContext, "Error al Guardar", Toast.LENGTH_LONG).show()
                Log.d("Error", e.toString())
            }

        }else{Toast.makeText(this,"Faltan datos", Toast.LENGTH_SHORT).show()}

        }

    }





    //Funciones para la alarma--------------------------------------------------------------------------
    private fun cancelAlarm() {
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(this,0,intent,0)
        alarmManager.cancel(pendingIntent)
        Toast.makeText(this,"Alarma cancelada", Toast.LENGTH_SHORT).show()
    }

    private fun setAlarm(medicinaAgregar : Medicamento) {
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        intent.putExtra("nombreMed",nombreMedicina.text.toString())
        intent.putExtra("nombreTrat",nombreTrat)
        pendingIntent = PendingIntent.getBroadcast(this,random,intent,PendingIntent.FLAG_UPDATE_CURRENT)

        when(medicinaAgregar.repeticion){
            "1 dosis por día" -> alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.timeInMillis,86400000,pendingIntent)
            "1 dosis cada 12 horas" -> alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.timeInMillis,43200000,pendingIntent)
            "1 dosis cada 8 horas" -> alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.timeInMillis,28800000,pendingIntent)
            "1 a la hora marcada" -> alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.timeInMillis,pendingIntent)
            else -> Toast.makeText(this,"No se reconoce el intervalo", Toast.LENGTH_SHORT).show()
        }
        Toast.makeText(this,"Alarma agregada", Toast.LENGTH_SHORT).show()


    }

    private fun showTimePicker() {
        picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Selecciona la hora")
            .build()

        picker.show(supportFragmentManager,"e-salud")
        picker.addOnPositiveButtonClickListener {

            hora=picker.hour
            minuto=picker.minute



            calendar = Calendar.getInstance()
            calendar[Calendar.HOUR_OF_DAY]= hora
            calendar[Calendar.MINUTE]=minuto
            calendar[Calendar.SECOND]=0
            calendar[Calendar.MILLISECOND]=0
            horaSeleccionada.text= "Hora seleccionada: "+String.format("%02d",hora)+":"+String.format("%02d",minuto)

        }

    }

    private fun createNotificationChannel() {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name : CharSequence = "e-saludReminderChannel"
            val descripcion = "Canal para alarma de e-Salud"
            val importancia = NotificationManager.IMPORTANCE_HIGH
            val canal = NotificationChannel("e-salud",name,importancia)
            canal.description = descripcion
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(canal)
        }

    }

//--------------------------------------------------------------------------------------------------
}