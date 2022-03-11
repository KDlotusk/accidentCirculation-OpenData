package fr.iutlyon1.theo.accidentcirculationprojetopendata.ui.dashboard

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.GridView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import fr.iutlyon1.theo.accidentcirculationprojetopendata.api.apiConnectAsyncTask
import fr.iutlyon1.theo.accidentcirculationprojetopendata.databinding.FragmentDashboardBinding
import fr.iutlyon1.theo.accidentcirculationprojetopendata.modele.Accident
import fr.iutlyon1.theo.accidentcirculationprojetopendata.ui.adapters.AccidentListAdapter

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this)[DashboardViewModel::class.java]

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val adapter = AccidentListAdapter(activity as Context, dashboardViewModel.accidents)

        val gridView: GridView = binding.gridView

        gridView.adapter = adapter
        gridView.numColumns = 2
        gridView.stretchMode = GridView.STRETCH_COLUMN_WIDTH

        val asyncTask = apiConnectAsyncTask()
        asyncTask.execute("https://public.opendatasoft.com/api/records/1.0/search/?dataset=accidents-corporels-de-la-circulation-millesime&q=&facet=datetime&facet=nom_com&facet=lum&facet=agg&facet=int&facet=atm&facet=col&facet=surf&facet=circ&facet=catr&facet=plan&facet=prof&facet=infra&facet=situ&facet=an_nais&facet=sexe&facet=actp&facet=grav&facet=secu&facet=secu_utl&facet=locp&facet=place&facet=catu&facet=etatp&facet=trajet&facet=choc&facet=manv&facet=obsm&facet=obs&facet=catv&facet=occutc&facet=gps&facet=com_name&facet=dep_code&facet=dep_name&facet=epci_code&facet=epci_name&facet=reg_code&facet=reg_name&facet=com_code", adapter, dashboardViewModel.accidents)


        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}