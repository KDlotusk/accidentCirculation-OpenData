package fr.iutlyon1.theo.accidentcirculationprojetopendata.ui.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import fr.iutlyon1.theo.accidentcirculationprojetopendata.R
import fr.iutlyon1.theo.accidentcirculationprojetopendata.modele.Accident
import kotlin.collections.ArrayList

class AccidentListAdapter(
    private val context: Context,
    val listAccident : ArrayList<Accident>
) : BaseAdapter() {


    override fun getCount(): Int =
        listAccident.size


    override fun getItem(index : Int): Any =
        listAccident[index]


    override fun getItemId(index: Int): Long =
        index.toLong()




    fun print() {
        for(accident in listAccident) {
            println("accident => ${accident.pedestrians[0].grav}")
        }
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val layoutItem : View
        val mInflater = LayoutInflater.from(context)

        //(1) : Réutilisation du layout
        layoutItem = convertView ?: mInflater.inflate(R.layout.accident_card_layout, parent, false)

        var viewHolder = ViewHolder (
            layoutItem.findViewById(R.id.lum),
            layoutItem.findViewById(R.id.status_logo),
            layoutItem.findViewById(R.id.dateOfWreck),
            layoutItem.findViewById(R.id.isNew),
            layoutItem.findViewById(R.id.nbOfCar)
        )

        if(layoutItem.tag != null) {
            viewHolder = layoutItem.tag as ViewHolder
        }
        layoutItem.tag = viewHolder

        viewHolder.dateOfWreck.text = listAccident[position].date
        viewHolder.nbOfCars.text = listAccident[position].vehicules.size.toString()
        viewHolder.isNew.text = ""


        if(listAccident[position].location.lum != "unknown") {
            val nameImage = "im_" +
            when(listAccident[position].location.lum) {
                "Plein jour"                            -> {
                                                                viewHolder.dateOfWreck.setTextColor(
                                                                    Color.BLACK)
                                                                "jour"
                                                            }
                "Crépuscule ou aube"                    -> {
                                                                viewHolder.dateOfWreck.setTextColor(
                                                                    Color.WHITE)
                                                                "crepuscule"
                                                            }
                "Nuit sans éclairage public"            -> {
                                                                viewHolder.dateOfWreck.setTextColor(
                                                                    Color.WHITE)
                                                                "nuit"
                                                            }
                "Nuit avec éclairage public non allumé" -> {
                                                                viewHolder.dateOfWreck.setTextColor(
                                                                    Color.WHITE)
                                                                "nuit"
                                                            }
                "Nuit avec éclairage public allumé"     -> {
                                                                viewHolder.dateOfWreck.setTextColor(
                                                                    Color.WHITE)
                                                                "lampadaire"
                                                            }

                else -> "default"
            }

            val resID = context.resources.getIdentifier(nameImage, "mipmap", context.packageName)

            viewHolder.lum.setImageResource(resID)
        }

        if(listAccident[position].pedestrians.size != 0) {
            if(listAccident[position].pedestrians[0].grav != "unknown"){
                val nameImage = "lg_" +
                        when(listAccident[position].pedestrians[0].grav) {
                            "Indemne" -> "alive"
                            "Tué"     -> "dead"
                            "Blessé"  -> "hospital"

                            else -> "no_damage"
                        } + "_foreground"

                val resID = context.resources.getIdentifier(nameImage, "mipmap", context.packageName)

                viewHolder.status_logo.setImageResource(resID)
            }
        }



        return layoutItem
    }
}

class ViewHolder (
    var lum: ImageView,
    var status_logo : ImageView,
    var dateOfWreck: TextView,
    var isNew: TextView,
    var nbOfCars: TextView
    )

