package fr.iutlyon1.theo.accidentcirculationprojetopendata.modele

import java.io.Serializable
import kotlin.collections.ArrayList

class Accident (
    val id : String,
    val date : String,

    val pedestrians : ArrayList<Pedestrian>,
    val vehicules : ArrayList<Vehicule>,
    val location: Location,

    val description : String = ""
    ): Serializable