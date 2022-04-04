package fr.iutlyon1.theo.accidentcirculationprojetopendata

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import fr.iutlyon1.theo.accidentcirculationprojetopendata.databinding.ActivityAccidentDetailsBinding


private lateinit var binding: ActivityAccidentDetailsBinding

class AccidentDetails : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAccidentDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = binding.toolbar
        toolbar.title="Accident"

        // Sets the Toolbar to act as the ActionBar for this Activity window.
        setSupportActionBar(toolbar)


    }

    // Menu icons are inflated just as they were with actionbar
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.toolbar_menu , menu)

        val returnButton = menu.findItem(0)
        
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.returnArrow -> {
                super.onBackPressed()
                finish()
            }

        }
        return super.onOptionsItemSelected(item)
    }
}