package com.adasoraninda.dicodingandroidreactiveinput.auto

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.adasoraninda.dicodingandroidreactiveinput.MainActivity
import com.adasoraninda.dicodingandroidreactiveinput.R
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import java.util.concurrent.TimeUnit

@FlowPreview
@ExperimentalCoroutinesApi
class InputAutoRxFragment : Fragment(R.layout.fragment_input_auto) {

    private var inputPlace: AutoCompleteTextView? = null

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity() as MainActivity).supportActionBar?.title = "Rx Auto"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inputPlace = view.findViewById(R.id.input_place)

        val inputDisposable = RxTextView.textChanges(inputPlace!!)
            .skipInitialValue()
            .debounce(300L, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .filter { input -> input.trim().isNotEmpty() }
            .switchMap {
                ApiConfig.provideApiServiceRx()
                    .getCountryRx(it.toString(), ApiConfig.ACCESS_TOKEN)
                    .subscribeOn(Schedulers.io())
                    .toObservable()
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    setPlaceItems(result.features)
                },
                { error ->
                    Toast.makeText(requireContext(), "${error.message}", Toast.LENGTH_SHORT).show()
                }
            )

        compositeDisposable.add(inputDisposable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
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