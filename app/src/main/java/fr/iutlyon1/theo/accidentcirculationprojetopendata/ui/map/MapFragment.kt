package fr.iutlyon1.theo.accidentcirculationprojetopendata.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import fr.iutlyon1.theo.accidentcirculationprojetopendata.R
import fr.iutlyon1.theo.accidentcirculationprojetopendata.api.ApiConnectMapAsyncTask
import fr.iutlyon1.theo.accidentcirculationprojetopendata.api.CallBackApi
import fr.iutlyon1.theo.accidentcirculationprojetopendata.databinding.FragmentMapBinding
import fr.iutlyon1.theo.accidentcirculationprojetopendata.modele.Accident

class MapFragment : Fragment(), CallBackApi {

    /*
    * information :
    *
    * on my phone this part of code doesn't seems to work anymore, an error "android.content.res.Resources$NotFoundException: Resource ID #0x7f070016 type #0x3 is not valid"
    * seems to occur, I don't know since when that error happens as I was testng on the emulator, where it works realy fine
    *
    * so please, do try the app in the emulator to see that functionality working
    * (I'm using Nexus 5 with api 30, but also seems to work with other emulators)
    * */



    private lateinit var googleMap : GoogleMap

    override fun onFinished() {
        try {
            for (accident in accidents) {
                val pos = LatLng(
                    accident.location.address.lat.toDouble() / 100000,
                    accident.location.address.long.toDouble() / 100000
                )
                googleMap.addMarker(
                    MarkerOptions().position(pos).title("an accident happened here")
                )
            }
        }
        catch(e : Exception) {
            e.printStackTrace()
        }
    }


    private var _binding: FragmentMapBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val accidents : ArrayList<Accident> = ArrayList<Accident>()

    private val callback = OnMapReadyCallback { _googleMap ->
        /*
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        try {
            val paris = LatLng(48.50, 2.20)
            _googleMap.addMarker(MarkerOptions().position(paris).title("Marker in paris"))
            _googleMap.moveCamera(CameraUpdateFactory.newLatLng(paris))

            googleMap = _googleMap

            loadData()
        }
        catch(e:java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        try {
            super.onViewCreated(view, savedInstanceState)

            val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
            mapFragment?.getMapAsync(callback)
        }
        catch(e:java.lang.Exception) {
            e.printStackTrace()
        }

    }


    private fun loadData() {
        val asyncTask = ApiConnectMapAsyncTask(activity as FragmentActivity, this)
        asyncTask.execute(
            "https://public.opendatasoft.com/api/records/1.0/search/?dataset=accidents-corporels-de-la-circulation-millesime&q=&rows=2000&facet=lat&facet=long"
            , accidents
        )
    }
}