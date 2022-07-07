package com.example.proyectois.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.proyectois.R
import com.example.proyectois.clases.SignoVital

class AdapterVitales(val context: Context, val res:Int, val list:ArrayList<SignoVital>): BaseAdapter() {
    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(p0: Int): Any {
        return list[p0]
    }

    override fun getItemId(p0: Int): Long {
        return -1
    }

    override fun getView(index: Int, p1: View?, p2: ViewGroup?): View {
        val view= LayoutInflater.from(context).inflate(res, null)
        val textTipo = view.findViewById<TextView>(R.id.textRowTipoSigno)
        val textMayor = view.findViewById<TextView>(R.id.textRowMayorMed)
        val textMenor = view.findViewById<TextView>(R.id.textRowMenorMed)
        val textPromedio = view.findViewById<TextView>(R.id.textRowPromedioMed)
        var mayor =0F
        var menor =9999F
        var conta =0
        var promedio = 0F
        textTipo.text=list[index].Tipo
        for (n: Float in list[index].medidas!!){
            promedio += n
            if(n > mayor){
                mayor = n
            }
            if (n < menor){
                menor = n
            }
            conta++
        }
        promedio = promedio/conta
        textMayor.text = "Mayor Medida: "+mayor
        textMenor.text = "Menor Medida: "+menor
        textPromedio.text = "Promedio: "+ promedio

        return view
    }
}