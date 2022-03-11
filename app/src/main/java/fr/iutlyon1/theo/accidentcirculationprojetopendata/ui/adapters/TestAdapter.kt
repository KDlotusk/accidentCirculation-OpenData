package fr.iutlyon1.theo.accidentcirculationprojetopendata.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import fr.iutlyon1.theo.accidentcirculationprojetopendata.R
import java.util.*
import kotlin.collections.ArrayList

class TestAdapter(
    val context: Context,
    val listEquipe : ArrayList<String>
) : BaseAdapter() {


    override fun getCount(): Int {
        return listEquipe.size
    }

    override fun getItem(index : Int): Any {
        return listEquipe[index]
    }

    override fun getItemId(index: Int): Long {
        return index.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val layoutItem : View
        val mInflater = LayoutInflater.from(context)
        //(1) : Réutilisation du layout
        if (convertView == null) {
            layoutItem = mInflater.inflate(R.layout.equipeliste_layout, parent, false)
        } else {
            layoutItem = convertView
        }


        var viewHolder = ViewHolder (layoutItem.findViewById(R.id.textView))
        if(layoutItem.tag != null) {
            viewHolder = layoutItem.tag as ViewHolder
        }

        layoutItem.tag = viewHolder


        viewHolder.textView.setText(listEquipe.get(position))


        return layoutItem;
    }


}
class ViewHolder (var textView: TextView)