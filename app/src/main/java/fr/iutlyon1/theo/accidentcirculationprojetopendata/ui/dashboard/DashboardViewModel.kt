package fr.iutlyon1.theo.accidentcirculationprojetopendata.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import fr.iutlyon1.theo.accidentcirculationprojetopendata.modele.Accident

class DashboardViewModel : ViewModel() {

    var nbRws = 0
    val url :String
        get() {
            return "https://public.opendatasoft.com/api/records/1.0/search/?dataset=accidents-corporels-de-la-circulation-millesime&q=&rows="+"" +
                    nbRws.toString() +
                    "&facet=datetime&facet=lum&facet=agg&facet=surf&facet=prof&facet=infra&facet=situ&facet=grav&facet=manv&facet=catv&facet=com_name&facet=dep_name&facet=reg_name"

        }

    val accidents = ArrayList<Accident>()

    private val _text = MutableLiveData("This is dashboard Fragment")
    val text: LiveData<String> = _text
}