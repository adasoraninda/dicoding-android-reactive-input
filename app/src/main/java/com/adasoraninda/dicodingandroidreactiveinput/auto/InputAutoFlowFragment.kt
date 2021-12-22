package com.adasoraninda.dicodingandroidreactiveinput.auto

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.adasoraninda.dicodingandroidreactiveinput.MainActivity
import com.adasoraninda.dicodingandroidreactiveinput.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@FlowPreview
@ExperimentalCoroutinesApi
class InputAutoFlowFragment : Fragment(R.layout.fragment_input_auto) {

    private var inputPlace: AutoCompleteTextView? = null

    private val inputState = MutableSharedFlow<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity() as MainActivity).supportActionBar?.title = "Flow Auto"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inputPlace = view.findViewById(R.id.input_place)

        inputPlace?.addTextChangedListener {
            lifecycleScope.launch {
                inputState.emit(it.toString())
            }
        }

        lifecycleScope.launch {
            inputState
                .debounce(300)
                .distinctUntilChanged()
                .filter {
                    it.trim().isNotEmpty()
                }
                .mapLatest {
                    ApiConfig.provideApiServiceFlow()
                        .getCountryFlow(it, ApiConfig.ACCESS_TOKEN)
                        .features
                }.collect {
                    setPlaceItems(it)
                }
        }

    }

    private fun setPlaceItems(placesItem: List<PlacesItem>) {
        val placesName = arrayListOf<String?>()
        placesItem.map {
            placesName.add(it.placeName)
        }

        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.select_dialog_item, placesName)
        adapter.notifyDataSetChanged()

        inputPlace?.setAdapter(adapter)
    }

}