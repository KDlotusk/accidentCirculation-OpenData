package fr.iutlyon1.theo.accidentcirculationprojetopendata.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import fr.iutlyon1.theo.accidentcirculationprojetopendata.modele.Accident

class DashboardViewModel : ViewModel() {

    val accidents = ArrayList<Accident>()

    private val _text = MutableLiveData("This is dashboard Fragment")
    val text: LiveData<String> = _text
}