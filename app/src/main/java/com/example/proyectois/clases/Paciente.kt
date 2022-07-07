package com.example.proyectois.clases

class Paciente(
    var nombre: String,
    var apellidoP: String,
    var apellidoM: String,
    var Correo: String,
    var contraseña: String,
    var tipoSanguineo: String,
    var tipoCronico: String?,
    var alergiasComunes: String?, //El ? indica que puede ser nulo y no sea necesario escribirlo en el constructor
    var alergiasMedicamentos: String?,
    var tratamientos: ArrayList<Tratamiento>?,
    var vitales : ArrayList<SignoVital>?
) {

}