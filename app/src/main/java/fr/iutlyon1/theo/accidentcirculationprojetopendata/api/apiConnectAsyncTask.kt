package fr.iutlyon1.theo.accidentcirculationprojetopendata.api

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.AsyncTask
import android.os.Build
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.GridView
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import fr.iutlyon1.theo.accidentcirculationprojetopendata.R
import fr.iutlyon1.theo.accidentcirculationprojetopendata.modele.*
import fr.iutlyon1.theo.accidentcirculationprojetopendata.ui.adapters.AccidentListAdapter
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import kotlin.collections.ArrayList

class ApiConnectAsyncTask(private val context : FragmentActivity, val swipeRefreshLayout : SwipeRefreshLayout) : AsyncTask<Any, Int, String>() {
    private var adapter : AccidentListAdapter? = null
    private lateinit var accidents : ArrayList<Accident>

    private var canUseCellular = 0

    private fun parsing(jsonLine: JSONObject) : Int {
        val records = jsonLine.getJSONArray("records")
        val nbRecords = jsonLine.getJSONObject("parameters").getInt("rows")

        for(recordIndex in 0 until records.length()) {

            publishProgress(nbRecords, recordIndex)


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
            val prof: String = if(fields.has("prof"))
                fields.getString("prof")
            else
                "unknown"

            val surf : String = if(fields.has("prof"))
                fields.getString("surf")
            else
                "unknown"

            val infra : String = if(fields.has("situ"))
                fields.getString("situ")
            else
                "unknown"

            val situ : String = if(fields.has("situ"))
                fields.getString("situ")
            else
                "unknown"

            val location = Location(lum, address, prof, surf, infra,situ)

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
            val id : String =    record.getString("recordid")

            val date : String =   fields.getString("datetime").split('T')[0]

            accidents.add(Accident(id, date, pedestrians, vehicules, location))
        }
        publishProgress(nbRecords, accidents.size)

        return (accidents.size / nbRecords * 100).toInt()
    }


    private fun checkForInternet(context: Context): Boolean {

        // register activity with the connectivity manager service
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // if the android version is equal to M
        // or greater we need to use the
        // NetworkCapabilities to check what type of
        // network has the internet connection
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // Returns a Network object corresponding to
            // the currently active default data network.
            val network = connectivityManager.activeNetwork ?: return false

            // Representation of the capabilities of an active network.
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                // Indicates this network uses a Wi-Fi transport,
                // or WiFi has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

                // Indicates this network uses a Cellular transport. or
                // Cellular has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                // else return false
                else -> false
            }
        } else {
            // if the android version is below M
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            return networkInfo.isConnected
        }
    }

    private fun checkCellularInternet(context: Context): Boolean {
        // register activity with the connectivity manager service
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // if the android version is equal to M
        // or greater we need to use the
        // NetworkCapabilities to check what type of
        // network has the internet connection
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // Returns a Network object corresponding to
            // the currently active default data network.
            val network = connectivityManager.activeNetwork ?: return false

            // Representation of the capabilities of an active network.
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                // Indicates this network uses a Cellular transport. or
                // Cellular has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                // else return false
                else -> false
            }
        } else {
            // if the android version is below M
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            return networkInfo.isConnected
        }
    }

    override fun onPreExecute() {
        swipeRefreshLayout.isRefreshing = true

        if(!checkForInternet(context)){

            cancel(true)
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
            var line : String?
            val sb = StringBuilder()

            while(run {
                    line = input.readLine()
                    line
                } != null) {
                sb.append(line)

                if (canUseCellular == 0) {
                    if (checkCellularInternet(context)) {
                        Log.e("AsyncTask", "connected using 4g")
                        jsonLine = sb.toString()
                        val parameters = JSONObject(jsonLine).getJSONObject("parameters")
                        if (parameters.has("start")) {
                            val nbRows = parameters.getInt("rows")
                            askCellular(nbRows)

                            while(canUseCellular == 0 && checkCellularInternet(context)) {
                                Log.i("AsyncTask", "waiting for user answer")
                            }
                            if(canUseCellular == -1)
                                cancel(true)
                        }
                    }
                }


            }
            jsonLine = sb.toString()

            Log.d("AsyncTask", "flux =$jsonLine")
            input.close()
        }

        urlConnection.disconnect()



        accidents.clear()

        if(jsonLine.isNotEmpty()) {
            try {
                val value = parsing(JSONObject(jsonLine))
                return when {
                    value == 100 -> "updated successfully"
                    value > 0 -> "didn't load everything"
                    else -> "Error"
                }
            }
            catch(e : Exception) {
                e.printStackTrace()
            }
        }

        return "Error"
    }

    override fun onProgressUpdate(vararg progress: Int?) {
        val totalRow = progress[0] as Int
        val currentRow = progress[1] as Int

        println("currently at ${(currentRow.toFloat()/totalRow.toFloat()*100).toInt()}%")

        Snackbar.make(context.findViewById(R.id.DashBoardGridView), "currently at ${(currentRow.toFloat()/totalRow.toFloat()*100).toInt()}%", Snackbar.LENGTH_SHORT).show()
    }

    override fun onPostExecute(result: String?) {
        swipeRefreshLayout.isRefreshing = false
        adapter!!.notifyDataSetChanged()


        for(accident in accidents) {
            println("accident =>" + accident.date)
        }

        Snackbar.make(context.findViewById(R.id.DashBoardGridView), result!!, Snackbar.LENGTH_SHORT).show()
    }

    override fun onCancelled() {
        swipeRefreshLayout.isRefreshing = false

        val gridView : GridView = context.findViewById(R.id.DashBoardGridView)
        val noInternetImageView : ImageView = context.findViewById(R.id.DashBoardNoInternetImageView)

        Snackbar.make(context.findViewById(R.id.DashBoardGridView),
            "sorry but you don't have internet access", Snackbar.LENGTH_SHORT).setAction("X") {
            // Call action functions here
        }.show()


        gridView.visibility = View.GONE
        noInternetImageView.visibility = View.VISIBLE
    }


    private fun askCellular(nbRows : Int) {
        Looper.prepare()

        Snackbar.make(context.findViewById(R.id.DashBoardGridView),
            "Your using your cellular connection, $nbRows number of records will be downloaded, do you agree?", Snackbar.LENGTH_INDEFINITE)
            .setAction("Yes") {
                    canUseCellular = 1
            }.setAction("No") {
                    canUseCellular = -1
            }
            .show()


        Log.e("AsyncTask", "builder")
    }
}