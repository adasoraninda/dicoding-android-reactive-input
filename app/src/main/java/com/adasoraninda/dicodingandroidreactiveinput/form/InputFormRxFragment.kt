package com.adasoraninda.dicodingandroidreactiveinput.form

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.adasoraninda.dicodingandroidreactiveinput.MainActivity
import com.adasoraninda.dicodingandroidreactiveinput.R
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
class InputFormRxFragment : Fragment(R.layout.fragment_input_form) {

    private var inputEmail: EditText? = null
    private var inputPassword: EditText? = null
    private var inputConPassword: EditText? = null
    private var buttonRegister: Button? = null

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity() as MainActivity).supportActionBar?.title = "Rx Form"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inputEmail = view.findViewById(R.id.input_email)
        inputPassword = view.findViewById(R.id.input_password)
        inputConPassword = view.findViewById(R.id.input_con_password)
        buttonRegister = view.findViewById(R.id.button_register)

        if (inputEmail == null && inputPassword == null && inputConPassword == null && buttonRegister == null) {
            Toast.makeText(requireContext(), "View error", Toast.LENGTH_SHORT).show()
            return
        }

        val emailObs = RxTextView.textChanges(inputEmail!!)
            .skipInitialValue()
            .map { email -> !Patterns.EMAIL_ADDRESS.matcher(email).matches() }

        val passwordObs = RxTextView.textChanges(inputPassword!!)
            .skipInitialValue()
            .map { pass -> pass.length < 4 }

        val passwordForConPassObs = RxTextView.textChanges(inputPassword!!)
            .skipInitialValue()

        val conPasswordObs = RxTextView.textChanges(inputConPassword!!)
            .skipInitialValue()

        val passMergeObs = Observable.combineLatest(
            passwordForConPassObs,
            conPasswordObs,
        ) { v1, v2 ->
            v1.toString() != v2.toString()
        }

        val emailObsDisposable = emailObs.subscribe(this::showEmailError)
        val passwordObsDisposable = passwordObs.subscribe(this::showPasswordError)
        val passMergeObsDisposable = passMergeObs.subscribe(this::showConfirmPasswordError)

        val buttonObsDisposable = Observable.combineLatest(
            emailObs, passwordObs, passMergeObs
        ) { v1, v2, v3 ->
            !v1 && !v2 && !v3
        }.startWith(false)
            .subscribe(buttonRegister!!::setEnabled)

        compositeDisposable.addAll(
            emailObsDisposable,
            passwordObsDisposable,
            passMergeObsDisposable,
            buttonObsDisposable
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
    }

    private fun showEmailError(isNotValid: Boolean) {
        inputEmail?.error = if (isNotValid) "Email not valid" else null
    }

    private fun showPasswordError(isNotValid: Boolean) {
        inputPassword?.error = if (isNotValid) "Password not valid" else null
    }

    private fun showConfirmPasswordError(isNotValid: Boolean) {
        inputConPassword?.error = if (isNotValid) "Confirm Password not same" else null
    }

}