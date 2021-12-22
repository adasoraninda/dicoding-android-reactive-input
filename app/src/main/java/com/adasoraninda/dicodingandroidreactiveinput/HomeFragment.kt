package com.adasoraninda.dicodingandroidreactiveinput

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.adasoraninda.dicodingandroidreactiveinput.auto.InputAutoFlowFragment
import com.adasoraninda.dicodingandroidreactiveinput.auto.InputAutoRxFragment
import com.adasoraninda.dicodingandroidreactiveinput.form.InputFormFlowFragment
import com.adasoraninda.dicodingandroidreactiveinput.form.InputFormRxFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
class HomeFragment : Fragment(R.layout.fragment_home) {

    private var buttonInputFormRx: Button? = null
    private var buttonInputAutoRx: Button? = null
    private var buttonInputFormFlow: Button? = null
    private var buttonInputAutoFlow: Button? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MainActivity).supportActionBar?.title =
            requireActivity().getString(R.string.app_name)

        buttonInputFormRx = view.findViewById(R.id.button_form_rx)
        buttonInputAutoRx = view.findViewById(R.id.button_auto_rx)
        buttonInputFormFlow = view.findViewById(R.id.button_form_flow)
        buttonInputAutoFlow = view.findViewById(R.id.button_auto_flow)

        buttonInputFormRx?.setOnClickListener {
            navigate(InputFormRxFragment())
        }

        buttonInputFormFlow?.setOnClickListener {
            navigate(InputFormFlowFragment())
        }

        buttonInputAutoFlow?.setOnClickListener {
            navigate(InputAutoFlowFragment())
        }

        buttonInputAutoRx?.setOnClickListener {
            navigate(InputAutoRxFragment())
        }

    }

    private fun navigate(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

}