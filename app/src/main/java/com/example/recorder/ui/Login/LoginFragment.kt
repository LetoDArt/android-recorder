package com.example.recorder.ui.Login

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.recorder.R
import com.example.recorder.databinding.FragmentLoginBinding
import com.example.recorder.utils.launchAndCollectIn
import com.example.recorder.utils.toast


class LoginFragment : Fragment(R.layout.fragment_login) {

    private val loginViewModel: LoginViewModel by viewModels()
    private val binding by viewBinding(FragmentLoginBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val app = view.context as AppCompatActivity
        app.supportActionBar?.hide()

        loginViewModel.clearTokens()

        binding.redirectToSignUpBtn.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.LoginToSignup)
        }

        binding.loginBtn.setOnClickListener {
            loginViewModel.loginProcess(
                binding.emailInput.text.toString(),
                binding.passwordInput.text.toString(),
            )
        }

        loginViewModel.signupFlow.launchAndCollectIn(viewLifecycleOwner) {
            findNavController().navigate(R.id.LoginToMain)
        }

        loginViewModel.toastFlow.launchAndCollectIn(viewLifecycleOwner) {
            toast(it)
        }
    }
}
