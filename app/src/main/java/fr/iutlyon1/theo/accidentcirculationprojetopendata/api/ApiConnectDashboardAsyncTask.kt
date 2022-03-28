package fr.iutlyon1.theo.accidentcirculationprojetopendata.api

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.AsyncTask
import android.os.Build
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.GridView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import fr.iutlyon1.theo.accidentcirculationprojetopendata.R
import fr.iutlyon1.theo.accidentcirculationprojetopendata.modele.*
import fr.iutlyon1.theo.accidentcirculationprojetopendata.ui.adapters.AccidentListAdapter
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL


class ApiConnectDashboardAsyncTask(private val context : FragmentActivity, val swipeRefreshLayout : SwipeRefreshLayout, private val adapter: AccidentListAdapter) : AsyncTask<Any, Int, String>() {
    private lateinit var accidents : ArrayList<Accident>
    private lateinit var prefs: SharedPreferences

    private var canUseCellular = 0

    private fun testOrUnknownString(fields : JSONObject, value : String) : String
            = if(fields.has(value))
        fields.getString(value)
    else
        "unknown"
    private fun testOrUnknownLong(fields : JSONObject, value : String) : Long
            = if(fields.has(value))
        fields.getLong(value)
    else
        -1

    private fun parsing(jsonLine: JSONObject) : Int {
        val records = jsonLine.getJSONArray("records")
        val nbRecords = jsonLine.getJSONObject("parameters").getInt("rows")

        for(recordIndex in 0 until records.length()) {

            publishProgress(nbRecords, recordIndex)


            val record = records[recordIndex] as JSONObject
            val fields = record.getJSONObject("fields")

            // address
            val adr : String =  testOrUnknownString(fields,"adr")

            val dep : String =  testOrUnknownString(fields,"dep")
            val com: String  =  testOrUnknownString(fields,"com")

            val lat: Long =     testOrUnknownLong(fields,"lat")
            val long: Long =    testOrUnknownLong(fields,"long")

            // location
            val address : Address = Address(adr, dep, com, lat, long)

            val lum: String =   testOrUnknownString(fields,"lum")
            val prof: String =  testOrUnknownString(fields,"prof")


            val surf : String = testOrUnknownString(fields,"surf")

            val infra : String =testOrUnknownString(fields,"infra")


            val situ : String = testOrUnknownString(fields,"situ")


            val location = Location(lum, address, prof, surf, infra,situ)

            //pedestrian
            val gravs: List<String> = testOrUnknownString(fields,"grav").split(',')

            val pedestrians = ArrayList<Pedestrian>()
            for(grav in gravs)
                pedestrians.add(Pedestrian(grav))

            //vehicule
            val vehicules = ArrayList<Vehicule>()
            val catvs: List<String> = testOrUnknownString(fields,"catv").split(',')
            val manvs: List<String> = testOrUnknownString(fields,"manv").split(',') // careful, can sometimes be only one
            for((index, catv) in catvs.withIndex()) {
                if(index < manvs.size)
                    vehicules.add(Vehicule(catv, manvs[index]))
                else
                    vehicules.add(Vehicule(catv, manvs[manvs.size-1]))
            }


            //accident :
            val id : String =    testOrUnknownString(fields,"recordid")

            val date : String =   testOrUnknownString(fields,"datetime").split('T')[0]

            accidents.add(Accident(id, date, pedestrians, vehicules, location))
        }
        publishProgress(nbRecords, accidents.size)

        return (accidents.size.toFloat() / nbRecords.toFloat() * 100).toInt()
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
        accidents = params[1] as ArrayList<Accident>

        var jsonLine = ""


        //reading the shared preference of the app
        prefs = context.getSharedPreferences(
            "fr.iutlyon1.theo.accidentcirculationprojetopendata", Context.MODE_PRIVATE
        )

        canUseCellular= prefs.getInt("fr.iutlyon1.theo.accidentcirculationprojetopendata.canUseCellular", 0)
        Log.i("canUseCelular", canUseCellular.toString())

        if (canUseCellular == 0) {
            if (checkCellularInternet(context)) {
                //askCellular()
                publishProgress(-1)

                while(canUseCellular == 0 && checkCellularInternet(context)) {
                    Log.i("AsyncTask", "waiting for user answer")
                }
                if(canUseCellular == -1)
                    cancel(true)
            }
        }

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
            }
            jsonLine = sb.toString()

            //Log.d("AsyncTask", "flux =$jsonLine")
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

        if (totalRow == -1) {

            // case where user is using cellular data
            prefs = context.getSharedPreferences(
                "fr.iutlyon1.theo.accidentcirculationprojetopendata", Context.MODE_PRIVATE
            )

            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setTitle("Title").setMessage("Your using your cellular connection, do you want to continue ?")

            builder.setPositiveButton("Yes") { dialog, id ->
                canUseCellular = 1
                Log.i("saving preference",
                    prefs.edit().putInt("fr.iutlyon1.theo.accidentcirculationprojetopendata.canUseCellular", canUseCellular).commit().toString())
            }
            builder.setNegativeButton("No") { dialog, id ->
                canUseCellular = -1
                Log.i("saving preference",
                    prefs.edit().putInt("fr.iutlyon1.theo.accidentcirculationprojetopendata.canUseCellular", canUseCellular).commit().toString())
            }
            val alert = builder.create()
            alert.show()



            Log.e("AsyncTask", "builder")
        }
        else {
            val currentRow = progress[1] as Int

            println("currently at ${(currentRow.toFloat()/totalRow.toFloat()*100).toInt()}%")
        }
    }

    override fun onPostExecute(result: String?) {
        swipeRefreshLayout.isRefreshing = false

        accidents.reverse()
        
        adapter.notifyDataSetChanged()

        //saving data

        val fos: FileOutputStream?
        val out: ObjectOutputStream?
        try {
            fos = context.openFileOutput("saveFile", Context.MODE_PRIVATE)
            out = ObjectOutputStream(fos)

            for(accident in accidents) {
                out.writeObject(accident)
            }
            out.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        for(accident in accidents) {
            println("accident =>" + accident.pedestrians[0].grav)
        }

        try {
            Snackbar.make(
                context.findViewById(R.id.DashBoardGridView),
                result!!,
                Snackbar.LENGTH_SHORT
            ).show()
        }
        catch(e : Exception) {
            Toast.makeText(context, result!!, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCancelled() {
        swipeRefreshLayout.isRefreshing = false

        //val gridView : GridView = context.findViewById(R.id.DashBoardGridView)
        //val noInternetImageView : ImageView = context.findViewById(R.id.DashBoardNoInternetImageView)

        Snackbar.make(context.findViewById(R.id.DashBoardGridView),
            "sorry but you don't have internet access", Snackbar.LENGTH_SHORT).setAction("X") {
            // Call action functions here
        }.show()

        // getting the content of the saved data
        val directory = context.getFilesDir()
        val file = File(directory, "saveFile")
        if(file.exists()) {
            var fis: FileInputStream? = null
            var _in: ObjectInputStream? = null
            try {
                fis = context.openFileInput("saveFile")
                _in = ObjectInputStream(fis)
                while (fis.available() > 0) {
                    val accident = _in.readObject() as Accident
                    adapter.listAccident.add(accident)
                    print("add accident $accident")
                }
            } catch (e: Exception) {
                e.printStackTrace();
            } finally {
                _in?.close()
                fis?.close()
            }
        }

        adapter.notifyDataSetChanged()


        //gridView.visibility = View.GONE
        //noInternetImageView.visibility = View.VISIBLE
    }

}