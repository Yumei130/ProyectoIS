package com.example.proyectois.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.proyectois.R
import com.example.proyectois.clases.Tratamiento

class AdapterTratamiento(val context: Context, val res:Int, val list:ArrayList<Tratamiento>):BaseAdapter() {
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
        val textNombre = view.findViewById<TextView>(R.id.textRowNombreTrat)
        val textFecha = view.findViewById<TextView>(R.id.textRowFechaTrat)
        val textMedico = view.findViewById<TextView>(R.id.textMedicoTrat)

        textNombre.text = list[index].nombre
        textFecha.text = list[index].fecha
        textMedico.text ="Médico: "+list[index].medico

        return view

    }
}
