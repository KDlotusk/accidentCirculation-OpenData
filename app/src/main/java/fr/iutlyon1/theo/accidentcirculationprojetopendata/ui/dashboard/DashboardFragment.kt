package fr.iutlyon1.theo.accidentcirculationprojetopendata.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import fr.iutlyon1.theo.accidentcirculationprojetopendata.R
import fr.iutlyon1.theo.accidentcirculationprojetopendata.api.ApiConnectAsyncTask
import fr.iutlyon1.theo.accidentcirculationprojetopendata.databinding.FragmentDashboardBinding
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

        //Initialize
        val dashboardViewModel =
            ViewModelProvider(this)[DashboardViewModel::class.java]

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val adapter = AccidentListAdapter(requireContext(), dashboardViewModel.accidents)

        val gridView: GridView = binding.DashBoardGridView

        val noInternetImageView : ImageView = binding.DashBoardNoInternetImageView

        //set the gridView
        gridView.adapter = adapter
        gridView.numColumns = 2
        gridView.stretchMode = GridView.STRETCH_COLUMN_WIDTH
        gridView.visibility = View.VISIBLE

        //set the image view
        noInternetImageView.visibility = View.GONE

        //deals with data
        val swipeRefreshLayout= binding.swipeRefreshDashboard


        swipeRefreshLayout.setColorSchemeResources(
            R.color.holo_blue_bright,
            R.color.holo_green_light,
            R.color.holo_orange_light,
            R.color.holo_red_light
        )
        swipeRefreshLayout.setOnRefreshListener {
            gridView.bringToFront()
            noInternetImageView.visibility = View.GONE

            adapter.print()

            loadData(adapter, dashboardViewModel, swipeRefreshLayout)
        }


        if(dashboardViewModel.accidents.isEmpty()) {
            loadData(adapter, dashboardViewModel, swipeRefreshLayout)
        }


        return root
    }

    private fun loadData(adapter : AccidentListAdapter, dashboardViewModel : DashboardViewModel, swipeRefreshLayout : SwipeRefreshLayout) {
        dashboardViewModel.nbRws+=10

        val asyncTask = ApiConnectAsyncTask(activity as FragmentActivity, swipeRefreshLayout)
        asyncTask.execute(
            dashboardViewModel.url,
            adapter,
            dashboardViewModel.accidents
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}