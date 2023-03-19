package com.example.recorder.ui.Signup

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.recorder.R
import com.example.recorder.databinding.FragmentSignupBinding
import com.example.recorder.utils.launchAndCollectIn
import com.example.recorder.utils.toast

class SignupFragment : Fragment(R.layout.fragment_signup) {

    private val viewModel: SignupViewModel by viewModels()
    private val binding by viewBinding(FragmentSignupBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val app = view.context as AppCompatActivity
        app.supportActionBar?.hide()

        binding.redirectToLogInBtn.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.SignupToLogin)
        }
        binding.signupBtn.setOnClickListener {
            viewModel.processRegData(
                binding.emailInput.text.toString(),
                binding.userNameInput.text.toString(),
                binding.firstNameInput.text.toString(),
                binding.lastNameInput.text.toString(),
                binding.passwordInput.text.toString(),
                binding.repeatPasswordInput.text.toString(),
            )
        }


        viewModel.toastFlow.launchAndCollectIn(viewLifecycleOwner) {
            toast(it)
        }

        viewModel.signupFlow.launchAndCollectIn(viewLifecycleOwner) {
            findNavController().navigate(R.id.SignupToLogin)
        }
    }
}
