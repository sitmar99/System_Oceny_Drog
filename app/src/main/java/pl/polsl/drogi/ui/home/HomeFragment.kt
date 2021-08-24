package pl.polsl.drogi.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import pl.polsl.drogi.BackgroundManager
import pl.polsl.drogi.R
import pl.polsl.drogi.utils.Vector3D

class HomeFragment : Fragment() {

    var duspa : TextView? = null
    private var x:String="s"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_home, container, false)

        duspa = root.findViewById<TextView>(R.id.text_home)


        BackgroundManager.subscribeAcc {
            updateX(it)
        }
        return root
    }

    fun updateX(v:Vector3D) {
        duspa?.text ="x " + v.x.toString() + " y " +  v.y.toString() + " z " + v.z.toString()
    }


}