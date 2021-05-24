package pl.polsl.drogi.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import pl.polsl.drogi.R
import java.util.*
import kotlin.concurrent.schedule
import kotlin.concurrent.timerTask

class SettingsFragment : Fragment() {

    private lateinit var sup:TextView
    private var xd:Int = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_settings, container, false)
        sup = root.findViewById(R.id.superPartia)
        sup.text = "aaaa"

        Timer("Settingup",false).schedule(500,100){

            activity?.runOnUiThread{
                xd++
                sup.text = xd.toString()
            }
        }
        return root
    }




}