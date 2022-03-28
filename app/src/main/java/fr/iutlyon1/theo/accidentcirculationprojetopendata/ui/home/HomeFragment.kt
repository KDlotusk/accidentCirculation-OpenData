package fr.iutlyon1.theo.accidentcirculationprojetopendata.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import fr.iutlyon1.theo.accidentcirculationprojetopendata.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root



        val webView: WebView = binding.webView

        webView.webViewClient = WebViewClient()

        webView.loadUrl("https://www.20minutes.fr/dossier/accident_de_la_route")
        return root

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}