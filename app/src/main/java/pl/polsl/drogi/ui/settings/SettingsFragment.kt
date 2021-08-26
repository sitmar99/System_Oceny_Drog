package pl.polsl.drogi.ui.settings

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import pl.polsl.drogi.BackgroundManager
import pl.polsl.drogi.R
import pl.polsl.drogi.sensors.AccelerometerSensor
import java.util.*
import kotlin.concurrent.schedule
import kotlin.concurrent.timerTask

class SettingsFragment : Fragment() {

    private var editTimeStartBias:EditText?=null
    private var editTimeAfterChangeBias:EditText?=null
    private var editAccBias:EditText?=null

    private lateinit var sup:TextView
    private var xd:Int = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_settings, container, false)

        editTimeStartBias = root.findViewById(R.id.edit_time_start_bias)
        editTimeAfterChangeBias = root.findViewById(R.id.edit_time_after_change_bias)
        editAccBias = root.findViewById(R.id.edit_acc_bias)

        editTimeStartBias?.setText(AccelerometerSensor.TIME_START_BIAS.toString(),TextView.BufferType.EDITABLE)
        editTimeAfterChangeBias?.setText(AccelerometerSensor.TIME_AFTER_CHANGE_BIAS.toString(),TextView.BufferType.EDITABLE)
        editAccBias?.setText(AccelerometerSensor.ACC_BIAS.toString(),TextView.BufferType.EDITABLE)

        editTimeStartBias?.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {

            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
                val x =2
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                val x =2
            }
        })

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