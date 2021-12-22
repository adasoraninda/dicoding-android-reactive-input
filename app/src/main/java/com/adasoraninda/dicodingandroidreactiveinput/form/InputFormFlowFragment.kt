package com.adasoraninda.dicodingandroidreactiveinput.form

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
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
class InputFormFlowFragment : Fragment(R.layout.fragment_input_form) {

    private var inputEmail: EditText? = null
    private var inputPassword: EditText? = null
    private var inputConPassword: EditText? = null
    private var buttonRegister: Button? = null

    private val inputEmailState = MutableSharedFlow<String>()
    private val inputPasswordState = MutableSharedFlow<String>()
    private val inputConPasswordState = MutableSharedFlow<String>()

    private val resultEmailState = MutableSharedFlow<Boolean>()
    private val resultPasswordState = MutableSharedFlow<Boolean>()
    private val resultConPasswordState = MutableSharedFlow<Boolean>()
    private val buttonState = MutableStateFlow(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity() as MainActivity).supportActionBar?.title = "Flow Form"
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

        inputEmail?.addTextChangedListener {
            lifecycleScope.launch {
                inputEmailState.emit(it.toString())
            }
        }

        inputPassword?.addTextChangedListener {
            lifecycleScope.launch {
                inputPasswordState.emit(it.toString())
            }
        }

        inputConPassword?.addTextChangedListener {
            lifecycleScope.launch {
                inputConPasswordState.emit(it.toString())
            }
        }

        lifecycleScope.launchWhenCreated {
            inputEmailState
                .map { email -> !Patterns.EMAIL_ADDRESS.matcher(email).matches() }
                .collect {
                    resultEmailState.emit(it)
                }
        }

        lifecycleScope.launchWhenCreated {
            inputPasswordState
                .map { pass -> pass.length < 4 }
                .collect {
                    resultPasswordState.emit(it)
                }
        }

        lifecycleScope.launchWhenCreated {
            inputConPasswordState.combine(inputPasswordState) { v1, v2 ->
                v1 != v2
            }.collect {
                resultConPasswordState.emit(it)
            }
        }

        lifecycleScope.launchWhenCreated {
            resultEmailState.collect {
                showEmailError(it)
            }
        }

        lifecycleScope.launchWhenCreated {
            resultPasswordState.collect {
                showPasswordError(it)
            }
        }

        lifecycleScope.launchWhenCreated {
            resultConPasswordState.collect {
                showConfirmPasswordError(it)
            }
        }

        lifecycleScope.launch {
            combine(
                resultEmailState,
                resultPasswordState,
                resultConPasswordState
            ) { v1, v2, v3 ->
                !v1 && !v2 && !v3
            }.collect {
                buttonState.value = it
            }
        }

        lifecycleScope.launchWhenCreated {
            buttonState.collect {
                buttonRegister?.isEnabled = it
            }
        }

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