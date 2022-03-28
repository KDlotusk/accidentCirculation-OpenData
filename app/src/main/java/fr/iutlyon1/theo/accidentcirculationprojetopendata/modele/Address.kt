package fr.iutlyon1.theo.accidentcirculationprojetopendata.modele

import java.io.Serializable

class Address(
    val adr : String,
    val dep : String,
    val com: String,

    val lat: Long,
    val long: Long,

): Serializable