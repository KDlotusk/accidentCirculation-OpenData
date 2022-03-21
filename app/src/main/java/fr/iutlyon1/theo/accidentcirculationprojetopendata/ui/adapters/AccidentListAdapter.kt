package fr.iutlyon1.theo.accidentcirculationprojetopendata.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import fr.iutlyon1.theo.accidentcirculationprojetopendata.R
import fr.iutlyon1.theo.accidentcirculationprojetopendata.modele.Accident
import java.util.*
import kotlin.collections.ArrayList

class AccidentListAdapter(
    private val context: Context,
    private val listAccident : ArrayList<Accident>
) : BaseAdapter() {


    override fun getCount(): Int =
        listAccident.size


    override fun getItem(index : Int): Any =
        listAccident[index]


    override fun getItemId(index: Int): Long =
        index.toLong()




    fun print() {
        for(accident in listAccident) {
            println("accident => ${accident.date}")
        }
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val layoutItem : View
        val mInflater = LayoutInflater.from(context)

        //(1) : Réutilisation du layout
        layoutItem = convertView ?: mInflater.inflate(R.layout.accident_card_layout, parent, false)

        var viewHolder = ViewHolder (layoutItem.findViewById(R.id.dateOfWreck))
        if(layoutItem.tag != null) {
            viewHolder = layoutItem.tag as ViewHolder
        }

        layoutItem.tag = viewHolder

        viewHolder.textView.text = listAccident[position].date


        return layoutItem
    }
}
class ViewHolder (var textView: TextView)

