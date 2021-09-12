package pl.polsl.drogi.ui.home

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import pl.polsl.drogi.BackgroundManager
import pl.polsl.drogi.R

class HomeFragment : Fragment() {

    var textStatus: TextView? = null
    var startButton: Button? = null
    var stopButton: Button? = null
    private var x: String = "s"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_home, container, false)

        textStatus = root.findViewById(R.id.text_status)
        startButton = root.findViewById(R.id.button_start)
        stopButton = root.findViewById(R.id.button_stop)

        BackgroundManager.subscribeAcc {
        }

        BackgroundManager.subscribeStatusChanged {
            onStatusChanged(it)
        }

        startButton?.setOnClickListener {
            BackgroundManager.start()
        }

        stopButton?.setOnClickListener {
            BackgroundManager.stop()
        }


        onStatusChanged(BackgroundManager.status)
        return root
    }

    fun onStatusChanged(newStatus: Boolean) {
        if (newStatus) {
            textStatus?.text = "ACTIVE"
            textStatus?.setTextColor(Color.GREEN)
        } else {
            textStatus?.text = "INACTIVE"
            textStatus?.setTextColor(Color.RED)
        }
    }
}