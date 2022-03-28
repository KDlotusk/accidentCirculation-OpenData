package fr.iutlyon1.theo.accidentcirculationprojetopendata.modele

import java.io.Serializable

class Location(
    val lum: String,

    val address : Address,
    val prof: String,
    val surf : String,
    val infra: String,
    val situ : String,
) : Serializable