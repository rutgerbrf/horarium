package nl.viasalix.horarium.module.calvijncollege.cup.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import nl.viasalix.horarium.module.calvijncollege.cup.R
import org.jetbrains.anko.sdk27.coroutines.textChangedListener

class SetupStep3 : Fragment() {

    var pin = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_calvijncollege_cup_setup_step3, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val tv = view.findViewById<EditText>(R.id.module_calvijncollege_cup_setup_step3_pin)
        tv.textChangedListener {
            onTextChanged { _, _, _, _ ->
                pin = tv.text.toString()
                Log.d(SetupStep1.TAG, "pin = $pin")
            }
        }
    }

    override fun onAttach(context: Context?) {
        if (context != null && context is CalvijncollegeCupSetup) {
            context.setNextHandler {
                context.pin = pin
            }
        }

        super.onAttach(context)
    }
}
