package com.example.proyectois.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.proyectois.R
import com.example.proyectois.clases.Medicamento

class AdapterMedicinas(val context: Context, val res:Int, val list:ArrayList<Medicamento>):
    BaseAdapter() {
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
        val view = LayoutInflater.from(context).inflate(res,null)
        val nombre = view.findViewById<TextView>(R.id.textRowNombreMed)
        val tipo = view.findViewById<TextView>(R.id.textRowTipoMed)
        val repeticion = view.findViewById<TextView>(R.id.textRowRepetMed)
        val hora = view.findViewById<TextView>(R.id.textRowHoraMed)


        nombre.text = list[index].nombre
        tipo.text = list[index].tipo
        repeticion.text = list[index].repeticion+" - "+list[index].cantidad+" dosis"
        hora.text = list[index].primerHora

        return view
    }
}