package fr.iutlyon1.theo.accidentcirculationprojetopendata.api

import android.os.AsyncTask
import android.util.Log
import fr.iutlyon1.theo.accidentcirculationprojetopendata.modele.*
import fr.iutlyon1.theo.accidentcirculationprojetopendata.ui.adapters.AccidentListAdapter
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class apiConnectAsyncTask : AsyncTask<Any, Void, String>() {
    private var adapter : AccidentListAdapter? = null
    private lateinit var accidents : ArrayList<Accident>

    fun parsing(jsonLine: JSONObject) {
        val records = jsonLine.getJSONArray("records")

        for(recordIndex in 0 until records.length()) {
            val record = records[recordIndex] as JSONObject
            val fields = record.getJSONObject("fields")

            // address
            val adr : String =  fields.getString("adr")
            val dep : String =  fields.getString("dep")
            val com: String  =  fields.getString("com")

            val lat: Long =     fields.getLong("lat")
            val long: Long =    fields.getLong("long")

            // location
            val address : Address = Address(adr, dep, com, lat, long)

            val lum: String =   fields.getString("lum")
            val prof: String =  fields.getString("prof")
            val surf : String = fields.getString("surf")
            val infra: String = fields.getString("infra")
            val situ : String = fields.getString("situ")

            val location = Location(lum, address, prof, surf, infra, situ)

            //pedestrian
            val gravs: List<String> = fields.getString("grav").split(',')
            val pedestrians = ArrayList<Pedestrian>()
            for(grav in gravs)
                pedestrians.add(Pedestrian(grav))

            //vehicule
            val vehicules = ArrayList<Vehicule>()
            val catvs: List<String> = fields.getString("catv").split(',')
            val manvs: List<String> = fields.getString("manv").split(',') // careful, can sometimes be only one
            for((index, catv) in catvs.withIndex()) {
                if(index < manvs.size)
                    vehicules.add(Vehicule(catv, manvs[index]))
                else
                    vehicules.add(Vehicule(catv, manvs[manvs.size-1]))
            }


            //accident :
            val id : Long =     fields.getLong("id")

            val dateFormat = SimpleDateFormat ("yyyy-MM-dd", Locale.FRANCE)
            val date : String =   fields.getString("date")

            accidents.add(Accident(id, date, pedestrians, vehicules, location))
        }

    }


    override fun doInBackground(vararg params: Any?): String {
        val host = params[0] as String
        adapter = params[1] as AccidentListAdapter
        accidents = params[2] as ArrayList<Accident>

        var jsonLine = ""

        val url = URL(host)
        val urlConnection = url.openConnection() as HttpURLConnection
        if (urlConnection.responseCode == HttpURLConnection.HTTP_OK) {
            val input = BufferedReader(InputStreamReader(urlConnection.inputStream))
            jsonLine = input.readLine()
            Log.d("AsyncTask", "flux =$jsonLine")

            input.close()
        }
        urlConnection.disconnect()

        if(jsonLine.isNotEmpty()) {
            try {
                parsing(JSONObject(jsonLine))
                return "updated"
            }
            catch(e : Exception) {
                e.printStackTrace()
            }
        }

        return "Error"
    }

    override fun onPostExecute(result: String?) {
        adapter!!.notifyDataSetChanged()
    }
}